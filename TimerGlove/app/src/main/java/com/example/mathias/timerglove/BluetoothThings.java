package com.example.mathias.timerglove;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class BluetoothThings {
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 32848852;
    private volatile boolean stopWorker;
    private double toRad = 10430.3783505;
    private double resist = 3276.8;
    private Sensor s1 = new Sensor(5);
    private Sensor s2 = new Sensor(5);
    private Sensor s3 = new Sensor(5);
    private Sensor s4 = new Sensor(5);
    private Sensor s5 = new Sensor(5);
    private Sensor s6 = new Sensor(5);
    private Activity callingActivity;

    public BluetoothThings(Activity c) {
        this.callingActivity = c;
    }

    public void findBT() throws IOException {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(callingActivity, "No bluetooth adapter available", Toast.LENGTH_LONG).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            callingActivity.startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        } else {
            findPairedDevices();
        }


    }


    public void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        Log.d("BT.closeBT:", "Bluetooth Closed");
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            try {
                findPairedDevices();
            } catch (IOException e) {
                Log.d("onActivityResult", e.getMessage());
            }
        } else {
            Toast.makeText(callingActivity, "You need to turn on the Bluetooth to use the app functions with the glove", Toast.LENGTH_LONG).show();
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
            Toast.makeText(callingActivity, "The glove isn't paired with this device", Toast.LENGTH_LONG).show();
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
        Log.d("BT.connectWithTheGlove", "Bluetooth Opened");
    }

    private void sendAutoBaudCode() throws IOException {
        // Envia o caractere 'U' para sincronizacao autobaud
        byte sinc[] = new byte[1];
        sinc[0] = 0x55;
        mmOutputStream.write(sinc);
        //mmOutputStream.write(msg.getBytes("US-ASCII"));
        Log.d("BT.sendAutoBaudCode", "Caractere U Sent");
        //sendAutoBaudCode();
        readData();

    }


    private void readData() {

        stopWorker = false;

        Toast.makeText(callingActivity, "Connected", Toast.LENGTH_SHORT).show();
        Thread dataReceiverThread = new Thread(new Runnable() {
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

                        getS1().getGx().add(ReadNextValue() / toRad);
                        getS1().getGy().add(ReadNextValue() / toRad);
                        getS1().getGz().add(ReadNextValue() / toRad);
                        getS1().getAx().add(ReadNextValue() / resist);
                        getS1().getAy().add(ReadNextValue() / resist);
                        getS1().getAz().add(ReadNextValue() / resist);

                        getS2().getGx().add(ReadNextValue() / toRad);
                        getS2().getGy().add(ReadNextValue() / toRad);
                        getS2().getGz().add(ReadNextValue() / toRad);
                        getS2().getAx().add(ReadNextValue() / resist);
                        getS2().getAy().add(ReadNextValue() / resist);
                        getS2().getAz().add(ReadNextValue() / resist);

                        getS3().getGx().add(ReadNextValue() / toRad);
                        getS3().getGy().add(ReadNextValue() / toRad);
                        getS3().getGz().add(ReadNextValue() / toRad);
                        getS3().getAx().add(ReadNextValue() / resist);
                        getS3().getAy().add(ReadNextValue() / resist);
                        getS3().getAz().add(ReadNextValue() / resist);

                        getS4().getGx().add(ReadNextValue() / toRad);
                        getS4().getGy().add(ReadNextValue() / toRad);
                        getS4().getGz().add(ReadNextValue() / toRad);
                        getS4().getAx().add(ReadNextValue() / resist);
                        getS4().getAy().add(ReadNextValue() / resist);
                        getS4().getAz().add(ReadNextValue() / resist);

                        getS5().getGx().add(ReadNextValue() / toRad);
                        getS5().getGy().add(ReadNextValue() / toRad);
                        getS5().getGz().add(ReadNextValue() / toRad);
                        getS5().getAx().add(ReadNextValue() / resist);
                        getS5().getAy().add(ReadNextValue() / resist);
                        getS5().getAz().add(ReadNextValue() / resist);

                        getS6().getGx().add(ReadNextValue() / toRad);
                        getS6().getGy().add(ReadNextValue() / toRad);
                        getS6().getGz().add(ReadNextValue() / toRad);
                        getS6().getAx().add(ReadNextValue() / resist);
                        getS6().getAy().add(ReadNextValue() / resist);
                        getS6().getAz().add(ReadNextValue() / resist);

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
        dataReceiverThread.start();
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


    public double getAccDif(int numSensor) {
        switch (numSensor) {
            case 1:
                return (s1.getAy().size() > 2)
                        ? s1.getAy().get(getS1().getAy().size() - 1) - s1.getAy().get(getS1().getAy().size() - 2) : 0;

            case 2:
                return (s2.getAy().size() > 2)
                        ? s2.getAy().get(getS1().getAy().size() - 1) - s2.getAy().get(getS1().getAy().size() - 2) : 0;
            case 3:
                return (s3.getAy().size() > 2)
                        ? s3.getAy().get(getS1().getAy().size() - 1) - s3.getAy().get(getS1().getAy().size() - 2) : 0;
            case 4:
                return (s4.getAy().size() > 2)
                        ? s4.getAy().get(getS1().getAy().size() - 1) - s4.getAy().get(getS1().getAy().size() - 2) : 0;
            case 5:
                return (s5.getAy().size() > 2)
                        ? s5.getAy().get(getS1().getAy().size() - 1) - s5.getAy().get(getS1().getAy().size() - 2) : 0;
            case 6:
                return (s6.getAy().size() > 2)
                        ? s6.getAy().get(getS1().getAy().size() - 1) - s6.getAy().get(getS1().getAy().size() - 2) : 0;
            default:
                return 0.0;
        }
    }

    public Sensor getS1() {
        return s1;
    }

    public void setS1(Sensor s1) {
        this.s1 = s1;
    }

    public Sensor getS2() {
        return s2;
    }

    public void setS2(Sensor s2) {
        this.s2 = s2;
    }

    public Sensor getS3() {
        return s3;
    }

    public void setS3(Sensor s3) {
        this.s3 = s3;
    }

    public Sensor getS4() {
        return s4;
    }

    public void setS4(Sensor s4) {
        this.s4 = s4;
    }

    public Sensor getS5() {
        return s5;
    }

    public void setS5(Sensor s5) {
        this.s5 = s5;
    }

    public Sensor getS6() {
        return s6;
    }

    public void setS6(Sensor s6) {
        this.s6 = s6;
    }
}




