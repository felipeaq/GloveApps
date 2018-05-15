package com.example.mathias.timerglove;


public class Sensor {

    private int maxSize;
    private AutoRemoveVector<Double> ax = new AutoRemoveVector<>(maxSize);
    private AutoRemoveVector<Double> ay = new AutoRemoveVector<>(maxSize);
    private AutoRemoveVector<Double> az = new AutoRemoveVector<>(maxSize);
    private AutoRemoveVector<Double> gx = new AutoRemoveVector<>(maxSize);
    private AutoRemoveVector<Double> gy = new AutoRemoveVector<>(maxSize);
    private AutoRemoveVector<Double> gz = new AutoRemoveVector<>(maxSize);



    public Sensor(int maxSize){
        this.maxSize=maxSize;
    }


    public AutoRemoveVector<Double> getAx() {
        return ax;
    }

    public AutoRemoveVector<Double> getAy() {
        return ay;
    }

    public AutoRemoveVector<Double> getAz() {
        return az;
    }

    public AutoRemoveVector<Double> getGx() {
        return gx;
    }

    public AutoRemoveVector<Double> getGy() {
        return gy;
    }

    public AutoRemoveVector<Double> getGz() {
        return gz;
    }


}
