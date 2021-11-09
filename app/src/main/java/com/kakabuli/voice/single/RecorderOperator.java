package com.kakabuli.voice.single;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class RecorderOperator {
    private static AudioRecord mRecord;
    // 音频获取源
    private int audioSource = MediaRecorder.AudioSource.MIC;

    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025  
    private static int sampleRateInHz = 16000;// 44100;  
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道  
    private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;// AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 音频大小  
    private int minBufSize;
    private static IRecordListener mIRecordListener;
    private volatile boolean isRecording = false;
    private static RecorderOperator instance;


    public static RecorderOperator getInstance() {
        if (null == instance) {
            instance = new RecorderOperator();
        }
        return instance;
    }

    public RecorderOperator() {
        minBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig,
                audioFormat);
//        minBufSize = 480 * 2;
        mRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig,
                audioFormat, minBufSize);
    }

    public void setRecordingListener(IRecordListener listener) {
        mIRecordListener = listener;
        Log.i("RecOp", "setRecordingListener!");
    }


    public void startRecord() {
        isRecording = true;
        //开启一个线程录音
        byte[] audiodata = new byte[minBufSize];
        mRecord.startRecording();
        if (mIRecordListener != null) {
//            mIRecordListener.onRecordingStart();
        }
        int readsize = 0;

        while (isRecording) {
            readsize = mRecord.read(audiodata, 0, minBufSize);
            if (mIRecordListener != null) {
                mIRecordListener.onRecordingData(audiodata);
            } else {
                Log.d("RecOp", "mIRecordListener is null!");
            }
        }
        Log.d("RecorderOperator", "Stop Rec!");
        mRecord.stop();
        if (mIRecordListener != null) {
//            mIRecordListener.onRecordingStop();
        }
    }

    public void stopRecord() {
        isRecording = false;
    }

}

