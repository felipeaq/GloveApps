package com.example.mathias.timerglove;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private int state = 1;
    private long initialTime = 0;
    private Thread t;
    private boolean stopWorker = false;
    boolean firstrun = true;


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
                        Thread.sleep(n);
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {
                            @Override
                            public void run() {
                                ((TextView) findViewById(R.id.textView4)).setText("" + n);
                                displayData(randn(1, 8), firstrun); // this action have to be in UI thread
                            }
                        });
                    } catch (InterruptedException e) {
                        // ooops
                    }
                    firstrun = false;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.textView4)).setText("waiting click");

                    }
                });

            }
        });
        t.start();


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

    private void waitForClick() {
        stopWorker = true;
        t.interrupt();
        initialTime = System.nanoTime();
        final TextView tv = findViewById(R.id.textView);
        final TextView tv2 = findViewById(R.id.textView2);
        final Button button = findViewById(R.id.button);
        button.setText("CLICK ME");
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long finalTime = System.nanoTime();
                long durationInNs = finalTime - initialTime;
                tv.setText(durationInNs + "ns");
                long durationInMs = TimeUnit.MILLISECONDS.convert(durationInNs, TimeUnit.NANOSECONDS);
                tv2.setText(durationInMs + "ms");
                button.setText("DON'T CLICK ME");
                button.setEnabled(false);
                stopWorker = false;
                firstrun = true;

                t.start();

            }
        });


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

    public static int randn(int min, int max) {
        return new Random().nextInt(max + 1 - min) + min;
    }
}
