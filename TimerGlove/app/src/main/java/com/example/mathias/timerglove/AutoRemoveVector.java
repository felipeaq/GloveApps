package com.example.mathias.timerglove;

import java.util.Vector;

class AutoRemoveVector<E> extends Vector<E> {

    private final int maxSize;
    private int realSize=0;
    public AutoRemoveVector(int maxSize){
        this.maxSize=maxSize;
    }

    @Override
    public synchronized boolean add(E e) {
        if (super.size()>maxSize)
            remove(0);
        realSize = getRealSize() + 1;
        return super.add(e);
    }

    public int getRealSize() {
        return realSize;
    }
}
