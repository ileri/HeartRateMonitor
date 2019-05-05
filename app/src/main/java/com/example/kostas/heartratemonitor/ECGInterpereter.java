package com.example.kostas.heartratemonitor;

import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class ECGInterpereter {
    public final int MAX_QUEUE_SIZE = 1000;
    private Queue<Integer> values;
    private Integer[] valuesArray;

    ECGInterpereter(){
        values = new CircularFifoQueue<Integer>(MAX_QUEUE_SIZE);
    }

    public void addValue(int value){
        values.add(value);
    }

    public void addValues(int[] values){
        for(int value : values){
            this.values.add(value);
        }
    }

    public void addValues(String valuesString){
        String[] stringValueArray = valuesString.split(" ");
        for(String strValue : stringValueArray){
            addValue(Integer.valueOf(strValue));
        }
    }

    public int[] getPQRST(){
        valuesArray = values.toArray(valuesArray);
        int indexR = findR();
        int indexQ = findQ(indexR);
        int indexP = findP(indexQ);
        int indexS = findS(indexR);
        int indexT = findT(indexS);
        return new int[]{indexP, indexQ, indexR, indexS, indexT};
    }

    private int findR(){
        return getMaxInRange(0, valuesArray.length);
    }

    private int findQ(int indexR){
        return getMinInRange(0, indexR);
    }

    private int findP(int indexQ){
        return getMaxInRange(0, indexQ);
    }

    private int findS(int indexR){
        return getMinInRange(indexR, valuesArray.length);
    }

    private int findT(int indexS){
        return getMaxInRange(indexS, valuesArray.length);
    }

    private int getMaxInRange(int startIndex, int endIndex){
        int maxValue = 0, maxIndex = 0;
        for(int i = startIndex; i < endIndex; i++){
            if(valuesArray[i] > maxValue){
                maxValue = valuesArray[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private int getMinInRange(int startIndex, int endIndex){
        int minValue = 1023, minIndex = 0;
        for(int i = startIndex; i < endIndex; i++){
            if(valuesArray[i] < minValue){
                minValue = valuesArray[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}
