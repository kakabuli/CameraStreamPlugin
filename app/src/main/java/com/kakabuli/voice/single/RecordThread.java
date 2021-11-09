package com.kakabuli.voice.single;

import android.util.Log;

public class RecordThread extends Thread {

    private static RecordThread instance;
    private static IRecordListener mRecLisenerCb;
    private static RecorderOperator recorderOperator;

    public static RecordThread getInstance(IRecordListener listener) {
        if (null == instance) {
            instance = new RecordThread();
            mRecLisenerCb = listener;
        }
        return instance;
    }

    private RecordThread() {
        recorderOperator = RecorderOperator.getInstance();
    }

    /**
     * 停止录音；
     */
    public void stopRec() {
        recorderOperator.stopRecord();
    }

    @Override
    public void run() {
        recorderOperator.setRecordingListener(mRecLisenerCb);
        Log.d("RecordThread", "start RecordThread!");
        recorderOperator.startRecord();
    }
}
