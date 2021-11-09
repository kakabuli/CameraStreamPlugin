package com.kakabuli.voice.player;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class PlayerManager {
    private static final String TAG = "PlayerManager";
    private MediaPlayer mediaPlayer;
    private static PlayerManager playerManager = new PlayerManager();

    public static PlayerManager getInstance() {
        return playerManager;
    }

    private PlayerManager() {
        mediaPlayer = new MediaPlayer();
    }

    public void playUrl(String url, IPlayCallBack iPlayCallBack) {
        Log.e(TAG, "playUrl: " + url);
        try {
            reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "playUrl:  start ");
                mediaPlayer.start();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (iPlayCallBack != null) {
                    iPlayCallBack.onEnd(true, "");
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "playUrl: error  " + what);
                reset();
                if (iPlayCallBack != null) {
                    iPlayCallBack.onEnd(false, "");
                }
                return true;
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "playUrl exception : " + e);
            reset();
            if (iPlayCallBack != null) {
                iPlayCallBack.onEnd(false, "");
            }
        }
    }

    public void playAssets(AssetFileDescriptor afd, IPlayCallBack iPlayCallBack) {
        Log.e(TAG, "playAssets: ");
        try {
            reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "playUrl:  start ");
                mediaPlayer.start();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d(TAG, "playAssets: play end ");
                if (iPlayCallBack != null) {
                    iPlayCallBack.onEnd(true, "");
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "playUrl: error  " + what);
                reset();
                if (iPlayCallBack != null) {
                    iPlayCallBack.onEnd(false, "");
                }
                return true;
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "playUrl exception : " + e);
            reset();
            if (iPlayCallBack != null) {
                iPlayCallBack.onEnd(false, "");
            }
        }
    }

    private void reset() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
}
