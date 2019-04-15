package com.vladislavmyasnikov.courseproject.utilities

import android.os.Bundle
import android.os.Handler
import android.os.Message

import java.util.Random

class DataUpdater : Thread() {

    private var mHandler: Handler? = null
    private var mDataSize: Int = 0

    override fun run() {
        val message = Message.obtain()
        val data = Bundle()
        val random = Random()
        val points = IntArray(mDataSize)
        for (i in points.indices) {
            points[i] = random.nextInt(11)
        }
        data.putIntArray(UPDATED_POINTS_DATA, points)
        message.data = data
        mHandler!!.sendMessage(message)
    }

    companion object {

        val UPDATED_POINTS_DATA = "updated_points_data"

        fun newInstance(handler: Handler, dataSize: Int): DataUpdater {
            val dataUpdater = DataUpdater()
            dataUpdater.mHandler = handler
            dataUpdater.mDataSize = dataSize
            return dataUpdater
        }
    }
}
