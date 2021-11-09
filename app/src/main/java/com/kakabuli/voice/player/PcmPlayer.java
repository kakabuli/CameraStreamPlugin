package com.kakabuli.voice.player;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;
import java.io.InputStream;

public class PcmPlayer extends BasePlayer {
    private static PcmPlayer pcmPlayer = new PcmPlayer();

    public static PcmPlayer getInstance() {
        return pcmPlayer;
    }

    /**
     * 采样率
     */
    private int sampleRateInHz = 16000;

    /**
     * 通道设置
     * 单声道CHANNEL_IN_MONO,双声道CHANNEL_IN_STEREO
     * AudioRecord 和 AudioTrack 的用的通道配置是分开的
     */
//const val channelConfig = AudioFormat.CHANNEL_IN_MONO //0x10
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO; //0x4

    /**
     * 音频格式
     */
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private AudioTrack audioTrack = null;
    private int minBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

    private PcmPlayer() {
        HandlerThread handlerThread = new HandlerThread("player");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        initPlayer();
    }

    @Override
    protected void initPlayer() {
        if (audioTrack == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioTrack = new AudioTrack.Builder().setBufferSizeInBytes(minBufferSize)
                        .setAudioFormat(
                                new AudioFormat.Builder()
                                        .setSampleRate(sampleRateInHz)
                                        .setChannelMask(channelConfig)
                                        .setEncoding(audioFormat)
                                        .build()
                        )
                        .setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        )
                        .build();
            } else {
                new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRateInHz,
                        channelConfig,
                        audioFormat,
                        minBufferSize,
                        AudioTrack.MODE_STREAM
                );
            }
        }
    }

    @Override
    public void startPlay(InputStream inputStream, IPlayCallBack iPlayCallBack) {
        stop();
        isStop = false;
        handler.post(() -> {
            byte[] byteArray = new byte[minBufferSize];
            startPlay(inputStream, byteArray, iPlayCallBack);
        });
    }

    private boolean startPlay(
            InputStream it,
            byte[] byteArray,
            IPlayCallBack callBack
    ) {
        if (audioTrack == null) {
            return false;
        }
        audioTrack.play();
        startPlayTime = System.currentTimeMillis();
        while (!isStop) {
            int read = 0;
            try {
                read = it.read(byteArray);
                if (System.currentTimeMillis() - startPlayTime > playTotalTime) {
                    read = -1;
                }
                if (read != -1) {
                    audioTrack.write(byteArray, 0, read);
                } else {
                    it.close();
                    if (callBack != null) {
                        callBack.onEnd(true, "");
                    }
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (it != null)
                it.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void stop() {
        if (audioTrack != null) {
            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop();
            }
        }
        isStop = true;
        handler.removeCallbacksAndMessages(null);
    }
}
