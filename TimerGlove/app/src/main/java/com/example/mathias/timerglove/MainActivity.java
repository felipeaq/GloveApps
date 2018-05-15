package com.example.mathias.timerglove;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private int state = 1;
    private long initialTime = 0;
    private Thread t;
    private boolean stopWorker = false;
    boolean firstrun = true;
    BluetoothThings bluetoothThings = new BluetoothThings(this);
    private Thread waitingMovementThread;
    double parameter = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!Thread.interrupted() && !stopWorker) {
                    try {
                        final int n = randn(randn(200, 1000), randn(1250, 2000));
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.delayTV)).setText("waiting click");
                                ((TextView) findViewById(R.id.delayTV)).setText("" + n);
                                displayData(randn(1, 8), firstrun); // this action have to be in UI thread
                            }
                        });
                        Thread.sleep(n);
                    } catch (InterruptedException e) {
                        // ooops
                    }
                    firstrun = false;
                }
            }
        });


        waitingMovementThread = new Thread(new Runnable() {
            @Override
            public void run() {
                double aVariable = bluetoothThings.getAccDif(1);

                while (aVariable < parameter && !waitingMovementThread.isInterrupted()) {
                    aVariable = bluetoothThings.getAccDif(1);
                }
                if (!waitingMovementThread.isInterrupted()) {
                    Log.d("waitingMovementThread", "não era pra vir aqui");
                    actionDone();
                }


            }
        });


        findViewById(R.id.openBTButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bluetoothThings.findBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.closeBTButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bluetoothThings.closeBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "STARTED", Toast.LENGTH_SHORT).show();
                startColorChange();
            }
        });
    }

    private void displayData(int n, boolean firstrun) {
        View v = findViewById(R.id.view2);
        Drawable mDrawable = getResources().getDrawable(R.drawable.circle);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(getMyColor(n, firstrun), PorterDuff.Mode.MULTIPLY));
        v.setBackground(mDrawable);
        if (n == 6 && !firstrun) {
            waitForClick();
        }
    }

    private void startColorChange() {
        stopWorker = false;
        firstrun = true;
        t.start();
    }


    private void waitForClick() {
        stopWorker = true;
        t.interrupt();
        initialTime = System.nanoTime();

        if (waitingMovementThread.isInterrupted() || !waitingMovementThread.isAlive())
            waitingMovementThread.start();
        setStopCounterButtonEnabled();


    }

    private void actionDone() {

        TextView tv = findViewById(R.id.nanoSecondsTV);
        TextView tv2 = findViewById(R.id.miliSecondsTV);
        long finalTime = System.nanoTime();
        long durationInNs = finalTime - initialTime;
        tv.setText(durationInNs + "ns");
        long durationInMs = TimeUnit.MILLISECONDS.convert(durationInNs, TimeUnit.NANOSECONDS);
        tv2.setText(durationInMs + "ms");

        TextView newtv = new TextView(this);
        newtv.setText(durationInMs + "ms");
        ((LinearLayout) findViewById(R.id.myListView)).addView(newtv);
        waitingMovementThread.interrupt();
        setStopCounterButtonDisabled();
        startColorChange();

    }

    private int getMyColor(int n, boolean firstrun) {
        int retorno = Color.BLACK;
        if (firstrun) {
            while (n == 6) {
                n = randn(1, 8);
            }
        }
        switch (n) {
            case 1:
                retorno = Color.BLACK;
                break;
            case 2:
                retorno = Color.BLUE;
                break;
            case 3:
                retorno = Color.YELLOW;
                break;
            case 4:
                retorno = Color.CYAN;
                break;
            case 5:
                retorno = Color.GREEN;
                break;
            case 6:
                retorno = Color.RED;
                break;
            case 7:
                retorno = Color.GRAY;
                break;
            case 8:
                retorno = Color.MAGENTA;
                break;
        }

        return retorno;
    }

    private void setStopCounterButtonEnabled() {
        final Button button = findViewById(R.id.theGreatButton);
        button.setText("CLICK ME");
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDone();

            }
        });

    }

    private void setStopCounterButtonDisabled() {
        final Button button = findViewById(R.id.theGreatButton);
        button.setText("DON'T CLICK ME");
        button.setEnabled(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bluetoothThings.onActivityResult(requestCode, resultCode, data);
    }


    public static int randn(int min, int max) {
        return new Random().nextInt(max + 1 - min) + min;
    }
}
