package com.gama.alessandrogirardi.comunicacao_bluetooth_luva;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private BluetoothAdapter mBluetoothAdapter;
    private volatile boolean stopWorker;
    private double toRad = 10430.3783505;
    private double resist = 3276.8;
    private Sensor s1 = new Sensor();
    private Sensor s2 = new Sensor();
    private Sensor s3 = new Sensor();
    private Sensor s4 = new Sensor();
    private Sensor s5 = new Sensor();
    private Sensor s6 = new Sensor();
    private TextToSpeech tts;
    private TextView predictedText;
    private TextView myLabel;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);

        // ------------- Predicts Things ------

        predictedText = findViewById(R.id.predictedText);

        Button btn_speak = findViewById(R.id.btn_speak);
        btn_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(predictedText.getText().toString());
            }
        });

        Button btn_apagar = findViewById(R.id.btn_apagar_ultima_letra);
        btn_apagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String palavra = predictedText.getText().toString();
                StringBuilder novaPalavra = new StringBuilder();
                if (palavra.length() > 0) {
                    for (int i = 0; i < palavra.length() - 1; i++)
                        novaPalavra.append(palavra.charAt(i));

                }
                predictedText.setText(novaPalavra.toString());


            }
        });
        Button btn_apagar_palavra = findViewById(R.id.btn_apagar_palavra);
        btn_apagar_palavra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                predictedText.setText("");

            }
        });

        myLabel = findViewById(R.id.label);


        //configButton
        Button configButton = findViewById(R.id.btn_config);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConfigsActivity.class);
                startActivity(intent);
            }
        });

        //graph button
        Button graphButton = findViewById(R.id.btn_graphs);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GraficosActivity.class);
                startActivity(intent);
            }
        });

        //Open Button
        Button openButton = findViewById(R.id.btn_open);
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                } catch (IOException ex) {
                    Log.d("openButtonEX", ex.getMessage());
                }
            }
        });

        //Close button
        Button closeButton = findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                    Log.d("closeButtonEX", ex.getMessage());

                }
            }
        });


    }

    @Override
    public void onInit(int status) {}

    private void speak(String fala) {
        if (tts != null) {
            int result = tts.setLanguage(new PreferencesUtils(this).getArchivedLocale());
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not suported", Toast.LENGTH_LONG).show();
            } else {
                tts.speak(fala, TextToSpeech.QUEUE_FLUSH, null);
            }
        }

    }

    private void findBT() throws IOException {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, Consts.REQUEST_ENABLE_BT);
        } else {
            findPairedDevices();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Consts.REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            try {
                findPairedDevices();
            } catch (IOException e) {
                Log.d ("onActivityResult", e.getMessage());
            }
        } else {
            Toast.makeText(this, "You need to turn on the Bluetooth to use the app functions with the glove", Toast.LENGTH_LONG).show();
        }

    }

    private void findPairedDevices() throws IOException {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("LUVAMOUSE")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        if (mmDevice == null) {
            Toast.makeText(this, "The glove isn't paired with this device", Toast.LENGTH_LONG).show();
        } else {
            connectWithTheGlove();
            sendAutoBaudCode();
        }
    }

    private void connectWithTheGlove() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        myLabel.setText("Bluetooth Opened");
    }

    private void sendAutoBaudCode() throws IOException {
        // Envia o caractere 'U' para sincronizacao autobaud
        byte sinc[] = new byte[1];
        sinc[0] = 0x55;
        mmOutputStream.write(sinc);
        //mmOutputStream.write(msg.getBytes("US-ASCII"));
        myLabel.setText("Caractere U Sent");
        //sendAutoBaudCode();
        readData();

    }

    private void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }

    private void readData() {

        final Handler handler = new Handler();
        stopWorker = false;


        Thread workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean continua = false;
                int cont = 0;
                byte[] cod1 = new byte[1];
                byte[] cod2 = new byte[1];
                byte[] cod3 = new byte[1];
                byte[] cod4 = new byte[1];

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        //int bytesAvailable = mmInputStream.available();
                        //myLabel.setText("Nro de bytes: " + Integer.toString(bytesAvailable));
                        //if (bytesAvailable > 1) {

                        // ------------ Sincronizacao -------------
                        continua = false;
                        while (!continua && !stopWorker) {
                            mmInputStream.read(cod1);
                            if (cod1[0] == (byte) 0xFF) {  // 255
                                mmInputStream.read(cod2);
                                if (cod2[0] == (byte) 0x7F) {  // 127
                                    mmInputStream.read(cod3);
                                    if (cod3[0] == (byte) 0xFE) {  // 254
                                        mmInputStream.read(cod4);
                                        if (cod4[0] == (byte) 0xFF) {  // 255
                                            continua = true;

                                        }
                                    }
                                }
                            }
                        }

                        // ------------ FIM Sincronizacao -------------


                        // ------------ Leitura dados sensores --------------

                        cont = cont + 1;

                        s1.getGx().add(ReadNextValue() / toRad);
                        s1.getGy().add(ReadNextValue() / toRad);
                        s1.getGz().add(ReadNextValue() / toRad);
                        s1.getAx().add(ReadNextValue() / resist);
                        s1.getAy().add(ReadNextValue() / resist);
                        s1.getAz().add(ReadNextValue() / resist);

                        s2.getGx().add(ReadNextValue() / toRad);
                        s2.getGy().add(ReadNextValue() / toRad);
                        s2.getGz().add(ReadNextValue() / toRad);
                        s2.getAx().add(ReadNextValue() / resist);
                        s2.getAy().add(ReadNextValue() / resist);
                        s2.getAz().add(ReadNextValue() / resist);

                        s3.getGx().add(ReadNextValue() / toRad);
                        s3.getGy().add(ReadNextValue() / toRad);
                        s3.getGz().add(ReadNextValue() / toRad);
                        s3.getAx().add(ReadNextValue() / resist);
                        s3.getAy().add(ReadNextValue() / resist);
                        s3.getAz().add(ReadNextValue() / resist);

                        s4.getGx().add(ReadNextValue() / toRad);
                        s4.getGy().add(ReadNextValue() / toRad);
                        s4.getGz().add(ReadNextValue() / toRad);
                        s4.getAx().add(ReadNextValue() / resist);
                        s4.getAy().add(ReadNextValue() / resist);
                        s4.getAz().add(ReadNextValue() / resist);

                        s5.getGx().add(ReadNextValue() / toRad);
                        s5.getGy().add(ReadNextValue() / toRad);
                        s5.getGz().add(ReadNextValue() / toRad);
                        s5.getAx().add(ReadNextValue() / resist);
                        s5.getAy().add(ReadNextValue() / resist);
                        s5.getAz().add(ReadNextValue() / resist);

                        s6.getGx().add(ReadNextValue() / toRad);
                        s6.getGy().add(ReadNextValue() / toRad);
                        s6.getGz().add(ReadNextValue() / toRad);
                        s6.getAx().add(ReadNextValue() / resist);
                        s6.getAy().add(ReadNextValue() / resist);
                        s6.getAz().add(ReadNextValue() / resist);


                        handler.post(new Runnable() {
                            public void run() {

                                Consts.seriesx1.appendData(new DataPoint(s1.getGx().getRealSize(), s1.getGx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy1.appendData(new DataPoint(s1.getGy().getRealSize(), s1.getGy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz1.appendData(new DataPoint(s1.getGz().getRealSize(), s1.getGz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx2.appendData(new DataPoint(s2.getGx().getRealSize(), s2.getGx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy2.appendData(new DataPoint(s2.getGy().getRealSize(), s2.getGy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz2.appendData(new DataPoint(s2.getGz().getRealSize(), s2.getGz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx3.appendData(new DataPoint(s3.getGx().getRealSize(), s3.getGx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy3.appendData(new DataPoint(s3.getGy().getRealSize(), s3.getGy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz3.appendData(new DataPoint(s3.getGz().getRealSize(), s3.getGz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx4.appendData(new DataPoint(s4.getGx().getRealSize(), s4.getGx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy4.appendData(new DataPoint(s4.getGy().getRealSize(), s4.getGy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz4.appendData(new DataPoint(s4.getGz().getRealSize(), s4.getGz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx5.appendData(new DataPoint(s5.getGx().getRealSize(), s5.getGx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy5.appendData(new DataPoint(s5.getGy().getRealSize(), s5.getGy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz5.appendData(new DataPoint(s5.getGz().getRealSize(), s5.getGz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx6.appendData(new DataPoint(s6.getGx().getRealSize(), s6.getGx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy6.appendData(new DataPoint(s6.getGy().getRealSize(), s6.getGy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz6.appendData(new DataPoint(s6.getGz().getRealSize(), s6.getGz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx1_acc.appendData(new DataPoint(s1.getAx().getRealSize(), s1.getAx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy1_acc.appendData(new DataPoint(s1.getAy().getRealSize(), s1.getAy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz1_acc.appendData(new DataPoint(s1.getAz().getRealSize(), s1.getAz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx2_acc.appendData(new DataPoint(s2.getAx().getRealSize(), s2.getAx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy2_acc.appendData(new DataPoint(s2.getAy().getRealSize(), s2.getAy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz2_acc.appendData(new DataPoint(s2.getAz().getRealSize(), s2.getAz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx3_acc.appendData(new DataPoint(s3.getAx().getRealSize(), s3.getAx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy3_acc.appendData(new DataPoint(s3.getAy().getRealSize(), s3.getAy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz3_acc.appendData(new DataPoint(s3.getAz().getRealSize(), s3.getAz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx4_acc.appendData(new DataPoint(s4.getAx().getRealSize(), s4.getAx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy4_acc.appendData(new DataPoint(s4.getAy().getRealSize(), s4.getAy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz4_acc.appendData(new DataPoint(s4.getAz().getRealSize(), s4.getAz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx5_acc.appendData(new DataPoint(s5.getAx().getRealSize(), s5.getAx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy5_acc.appendData(new DataPoint(s5.getAy().getRealSize(), s5.getAy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz5_acc.appendData(new DataPoint(s5.getAz().getRealSize(), s5.getAz().lastElement()), true, Consts.max_x_points);

                                Consts.seriesx6_acc.appendData(new DataPoint(s6.getAx().getRealSize(), s6.getAx().lastElement()), true, Consts.max_x_points);
                                Consts.seriesy6_acc.appendData(new DataPoint(s6.getAy().getRealSize(), s6.getAy().lastElement()), true, Consts.max_x_points);
                                Consts.seriesz6_acc.appendData(new DataPoint(s6.getAz().getRealSize(), s6.getAz().lastElement()), true, Consts.max_x_points);


                            }
                        });

                        handler.post(new Runnable() {
                            public void run() {
                                myLabel.setText(String.format("%f", s1.getGx().lastElement()));
                            }
                        });


                        //stopWorker = true;


                    } catch (IOException ex) {
                        stopWorker = true;
                        Log.d("EXCEPTION", ex.getMessage());
                        //continua=true;
                        //android.util.Log.d("YourApplicationName", ex.toString());

                    }
                }
            }
        }
        );

        workerThread.start();
        waitForStabilization();
    }

    @Override
    public void onResume() {
        super.onResume();
        Consts.lockPredict = false;
        Log.d("lockPredict", "unlocked");
    }

    @Override
    public void onPause() {
        super.onPause();
        Consts.lockPredict = true;
        Log.d("lockPredict", "locked");
    }

    private void waitForStabilization() {
        Thread t = new Thread(new Runnable() {
            ArrayList<Double> table = new ArrayList<>();
            ArrayList<Double> tablePrevious = new ArrayList<>();
            ArrayList<Double> tableDiff = new ArrayList<>();
            int state = 3;
            int nextState = 0;

            @Override
            public void run() {
                while (!stopWorker) {
                    if (!Consts.lockPredict) {
                        switch (state) {
                            case 1:

                                double max_variation;
                                if (s6.getAz().size() > 2) {
                                    Log.d("state", "" + 1);
                                    table.add(s1.getAx().get(s1.getAx().size() - 1));
                                    table.add(s1.getAy().get(s1.getAy().size() - 1));
                                    table.add(s1.getAz().get(s1.getAz().size() - 1));
                                    table.add(s2.getAx().get(s2.getAx().size() - 1));
                                    table.add(s2.getAy().get(s2.getAy().size() - 1));
                                    table.add(s2.getAz().get(s2.getAz().size() - 1));
                                    table.add(s3.getAx().get(s3.getAx().size() - 1));
                                    table.add(s3.getAy().get(s3.getAy().size() - 1));
                                    table.add(s3.getAz().get(s3.getAz().size() - 1));
                                    table.add(s4.getAx().get(s4.getAx().size() - 1));
                                    table.add(s4.getAy().get(s4.getAy().size() - 1));
                                    table.add(s4.getAz().get(s4.getAz().size() - 1));
                                    table.add(s5.getAx().get(s5.getAx().size() - 1));
                                    table.add(s5.getAy().get(s5.getAy().size() - 1));
                                    table.add(s5.getAz().get(s5.getAz().size() - 1));
                                    table.add(s6.getAx().get(s6.getAx().size() - 1));
                                    table.add(s6.getAy().get(s6.getAy().size() - 1));
                                    table.add(s6.getAz().get(s6.getAz().size() - 1));


                                    tablePrevious.add(s1.getAx().get(s1.getAx().size() - 2));
                                    tablePrevious.add(s1.getAy().get(s1.getAy().size() - 2));
                                    tablePrevious.add(s1.getAz().get(s1.getAz().size() - 2));
                                    tablePrevious.add(s2.getAx().get(s2.getAx().size() - 2));
                                    tablePrevious.add(s2.getAy().get(s2.getAy().size() - 2));
                                    tablePrevious.add(s2.getAz().get(s2.getAz().size() - 2));
                                    tablePrevious.add(s3.getAx().get(s3.getAx().size() - 2));
                                    tablePrevious.add(s3.getAy().get(s3.getAy().size() - 2));
                                    tablePrevious.add(s3.getAz().get(s3.getAz().size() - 2));
                                    tablePrevious.add(s4.getAx().get(s4.getAx().size() - 2));
                                    tablePrevious.add(s4.getAy().get(s4.getAy().size() - 2));
                                    tablePrevious.add(s4.getAz().get(s4.getAz().size() - 2));
                                    tablePrevious.add(s5.getAx().get(s5.getAx().size() - 2));
                                    tablePrevious.add(s5.getAy().get(s5.getAy().size() - 2));
                                    tablePrevious.add(s5.getAz().get(s5.getAz().size() - 2));
                                    tablePrevious.add(s6.getAx().get(s6.getAx().size() - 2));
                                    tablePrevious.add(s6.getAy().get(s6.getAy().size() - 2));
                                    tablePrevious.add(s6.getAz().get(s6.getAz().size() - 2));
                                    for (int i = 0; i < table.size(); i++) {

                                        double valorAtual = table.get(i);
                                        double valorAnterior = tablePrevious.get(i);
                                        double result = valorAnterior - valorAtual;
                                        if (result < 0) {
                                            result *= -1;
                                        }
                                        tableDiff.add(result);
                                    }

                                    max_variation = Collections.max(tableDiff);
                                } else {
                                    max_variation = 100;
                                }
                                Log.d("MAX_VARIATION", "" + max_variation);
                                int estavel = 0;
                                if (max_variation < 0.2) {
                                    estavel++;
                                } else {
                                    estavel = 0;
                                }


                                if (estavel >= 1) {
                                    nextState = 2;
                                } else {
                                    nextState = 1;
                                }

                                break;
                            case 2:
                                Log.d("state", "" + 2);

                                table.add(s1.getGx().get(s1.getGx().size() - 1));
                                table.add(s1.getGy().get(s1.getGy().size() - 1));
                                table.add(s1.getGz().get(s1.getGz().size() - 1));
                                table.add(s2.getGx().get(s2.getGx().size() - 1));
                                table.add(s2.getGy().get(s2.getGy().size() - 1));
                                table.add(s2.getGz().get(s2.getGz().size() - 1));
                                table.add(s3.getGx().get(s3.getGx().size() - 1));
                                table.add(s3.getGy().get(s3.getGy().size() - 1));
                                table.add(s3.getGz().get(s3.getGz().size() - 1));
                                table.add(s4.getGx().get(s4.getGx().size() - 1));
                                table.add(s4.getGy().get(s4.getGy().size() - 1));
                                table.add(s4.getGz().get(s4.getGz().size() - 1));
                                table.add(s5.getGx().get(s5.getGx().size() - 1));
                                table.add(s5.getGy().get(s5.getGy().size() - 1));
                                table.add(s5.getGz().get(s5.getGz().size() - 1));
                                table.add(s6.getGx().get(s6.getGx().size() - 1));
                                table.add(s6.getGy().get(s6.getGy().size() - 1));
                                table.add(s6.getGz().get(s6.getGz().size() - 1));
                                final double[] args = new double[18];
                                for (int i = 0; i < 18; i++) {
                                    args[i] = table.get(i);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        char pred = SVC.predictLetter(args, MainActivity.this);
                                        predictedText.setText(predictedText.getText().toString() + pred);
                                        if (pred == ' ') {
                                            speak(predictedText.getText().toString());
                                        }
                                    }
                                });
                                nextState = 3;
                                break;
                            case 3:

                                double maxVariationAc;
                                if (s6.getAz().size() > 2) {
                                    Log.d("state", "" + 3);

                                    table.add(s1.getAx().get(s1.getAx().size() - 1));
                                    table.add(s1.getAy().get(s1.getAy().size() - 1));
                                    table.add(s1.getAz().get(s1.getAz().size() - 1));
                                    table.add(s2.getAx().get(s2.getAx().size() - 1));
                                    table.add(s2.getAy().get(s2.getAy().size() - 1));
                                    table.add(s2.getAz().get(s2.getAz().size() - 1));
                                    table.add(s3.getAx().get(s3.getAx().size() - 1));
                                    table.add(s3.getAy().get(s3.getAy().size() - 1));
                                    table.add(s3.getAz().get(s3.getAz().size() - 1));
                                    table.add(s4.getAx().get(s4.getAx().size() - 1));
                                    table.add(s4.getAy().get(s4.getAy().size() - 1));
                                    table.add(s4.getAz().get(s4.getAz().size() - 1));
                                    table.add(s5.getAx().get(s5.getAx().size() - 1));
                                    table.add(s5.getAy().get(s5.getAy().size() - 1));
                                    table.add(s5.getAz().get(s5.getAz().size() - 1));
                                    table.add(s6.getAx().get(s6.getAx().size() - 1));
                                    table.add(s6.getAy().get(s6.getAy().size() - 1));
                                    table.add(s6.getAz().get(s6.getAz().size() - 1));


                                    tablePrevious.add(s1.getAx().get(s1.getAx().size() - 2));
                                    tablePrevious.add(s1.getAy().get(s1.getAy().size() - 2));
                                    tablePrevious.add(s1.getAz().get(s1.getAz().size() - 2));
                                    tablePrevious.add(s2.getAx().get(s2.getAx().size() - 2));
                                    tablePrevious.add(s2.getAy().get(s2.getAy().size() - 2));
                                    tablePrevious.add(s2.getAz().get(s2.getAz().size() - 2));
                                    tablePrevious.add(s3.getAx().get(s3.getAx().size() - 2));
                                    tablePrevious.add(s3.getAy().get(s3.getAy().size() - 2));
                                    tablePrevious.add(s3.getAz().get(s3.getAz().size() - 2));
                                    tablePrevious.add(s4.getAx().get(s4.getAx().size() - 2));
                                    tablePrevious.add(s4.getAy().get(s4.getAy().size() - 2));
                                    tablePrevious.add(s4.getAz().get(s4.getAz().size() - 2));
                                    tablePrevious.add(s5.getAx().get(s5.getAx().size() - 2));
                                    tablePrevious.add(s5.getAy().get(s5.getAy().size() - 2));
                                    tablePrevious.add(s5.getAz().get(s5.getAz().size() - 2));
                                    tablePrevious.add(s6.getAx().get(s6.getAx().size() - 2));
                                    ////////PRANDO

                                    tablePrevious.add(s6.getAy().get(s6.getAy().size() - 2));
                                    tablePrevious.add(s6.getAz().get(s6.getAz().size() - 2));
                                    for (int i = 0; i < table.size(); i++) {
                                        double valorAtual = table.get(i);
                                        double valorAnterior = tablePrevious.get(i);
                                        double result = valorAnterior - valorAtual;
                                        if (result < 0) {
                                            result *= -1;
                                        }
                                        tableDiff.add(result);
                                    }

                                    maxVariationAc = Collections.max(tableDiff);
                                } else {
                                    maxVariationAc = 0;
                                }
                                if (maxVariationAc > 1.5) {
                                    nextState = 1;
                                } else {
                                    nextState = 3;
                                }
                                break;

                        }
                    }
                    state = nextState;
                    table.clear();
                    tableDiff.clear();
                    tablePrevious.clear();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }

        });
        t.start();


    }

    public double ReadNextValue() {
        try {
            byte[] buffer_low = new byte[1];
            byte[] buffer_high = new byte[1];
            //byte[] buffer_full = new byte[4];
            mmInputStream.read(buffer_low);
            mmInputStream.read(buffer_high);
            ByteBuffer wrapped = ByteBuffer.wrap((new byte[]{0, 0, buffer_high[0], buffer_low[0]}));
            double num_double = wrapped.getInt();
            double a = Math.pow(2, 15) - 1;
            if (num_double > (a)) {
                num_double = num_double - Math.pow(2, 16);
            }
            return num_double;
        } catch (IOException ex) {
            return 0;
        }
    }
}