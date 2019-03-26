package com.vladislavmyasnikov.courseproject.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Random;

public class DataUpdater extends Thread {

    public static final String UPDATED_POINTS_DATA = "updated_points_data";

    private Handler mHandler;
    private int mDataSize;

    public static DataUpdater newInstance(Handler handler, int dataSize) {
        DataUpdater dataUpdater = new DataUpdater();
        dataUpdater.mHandler = handler;
        dataUpdater.mDataSize = dataSize;
        return dataUpdater;
    }

    @Override
    public void run() {
        Message message = Message.obtain();
        Bundle data = new Bundle();
        Random random = new Random();
        int[] points = new int[mDataSize];
        for (int i = 0; i < points.length; i++) {
            points[i] = random.nextInt(11);
        }
        data.putIntArray(UPDATED_POINTS_DATA, points);
        message.setData(data);
        mHandler.sendMessage(message);
   }
}
