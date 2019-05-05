package com.example.kostas.heartratemonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class ECGInterpereter {
    //public final int MAX_QUEUE_SIZE = 1000;
    //private Queue<Integer> values;
    private Integer[] valuesArray;
    private Integer[] waveArray;

    ECGInterpereter(){
        //values = new CircularFifoQueue<>(MAX_QUEUE_SIZE);
    }

    /*
    // ACTIVATE IN NEED OF REALTIME SIGNAL STREAMING

    public void addValue(int value){
        values.add(value);
    }

    public void addValues(int[] values){
        for(int value : values)
            this.values.add(value);
    }

    public void addValues(String valuesString){
        String[] stringValueArray = valuesString.split(" ");
        for(String strValue : stringValueArray)
            addValue(Integer.valueOf(strValue));
    }

    public void resetValues(){
        values.clear();
    }
    */

    private void addValues(String valuesString){
        String[] stringValueArray = valuesString.split(" ");
        valuesArray = new Integer[stringValueArray.length];
        for(int i = 0; i < stringValueArray.length; i++)
            valuesArray[i] = Integer.valueOf(stringValueArray[i]);
    }

    private int[] splitECGSignal(String signal){
        addValues(signal);
        return splitECGSignal();
    }

    private int[] splitECGSignal(){
        List<Integer> rWavesIndexes = new ArrayList<>();

        boolean isRWave = false;
        int rStartIndex = 0, rStopIndex = 0;

        for(int i = 0; i < valuesArray.length; i++){
            int value = valuesArray[i];
            if(!isRWave){
                if(value > 650){
                    /* R wave started */
                    isRWave = true;
                    rStartIndex = i;
                }else{
                    /* R wave has not started */}
            }else{
                if(value > 650){
                    /* R wave continues */ }
                else{
                    /* R wave finished */
                    rStopIndex = i;
                    rWavesIndexes.add(((rStartIndex + rStopIndex) / 2));
                    isRWave = false;
                }
            }
        }

        Integer[] rWavesIndexesArray = new Integer[rWavesIndexes.size()];
        rWavesIndexesArray = rWavesIndexes.toArray(rWavesIndexesArray);

        int[] wavesEndIndexes = new int[rWavesIndexesArray.length];

        int i = 0;
        for(; i < rWavesIndexesArray.length-1; i++)
            wavesEndIndexes[i] = rWavesIndexesArray[i] + (int)((rWavesIndexesArray[i+1] - rWavesIndexesArray[i])*(0.66));

        wavesEndIndexes[i] = valuesArray.length;

        return wavesEndIndexes;
    }

    public String processECGSignal(String signal){
        int[] endTimes = splitECGSignal(signal);
        int startTime = 0;

        double prIntervalSum = 0;
        double qtIntervalSum = 0;

        for(int i = 0; i < endTimes.length; i++){
            System.arraycopy(valuesArray, startTime, waveArray, 0, endTimes[i]-startTime);
            int[] pqrst = getPQRST();
            double prInterval = (pqrst[1] - pqrst[0])*(0.006);
            double qtInterval = (pqrst[4] - pqrst[1])*(0.006);

            prIntervalSum += prInterval;
            qtIntervalSum += qtInterval;
        }


        return "PR interval avg: " + (prIntervalSum/endTimes.length) + " QT intervar avg: " + (qtIntervalSum/endTimes.length);
    }

    private int[] getPQRST(){
        //valuesArray = values.toArray(valuesArray);
        int indexR = findR();
        int indexQ = findQ(indexR);
        int indexP = findP(indexQ);
        int indexS = findS(indexR);
        int indexT = findT(indexS);
        return new int[]{indexP, indexQ, indexR, indexS, indexT};
    }

    private int findR(){
        return getMaxInRange(0, waveArray.length);
    }

    private int findQ(int indexR){
        return getMinInRange(0, indexR);
    }

    private int findP(int indexQ){
        return getMaxInRange(0, indexQ);
    }

    private int findS(int indexR){
        return getMinInRange(indexR, waveArray.length);
    }

    private int findT(int indexS){
        return getMaxInRange(indexS, waveArray.length);
    }

    private int getMaxInRange(int startIndex, int endIndex){
        int maxValue = 0, maxIndex = 0;
        for(int i = startIndex; i < endIndex; i++){
            if(waveArray[i] > maxValue){
                maxValue = waveArray[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private int getMinInRange(int startIndex, int endIndex){
        int minValue = 1023, minIndex = 0;
        for(int i = startIndex; i < endIndex; i++){
            if(waveArray[i] < minValue){
                minValue = waveArray[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

}
