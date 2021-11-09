package com.kakabuli.voice.player;

import android.os.Handler;

import java.io.InputStream;

public abstract class BasePlayer {
    protected long playTotalTime = 13000L;
    protected Handler handler;
    protected boolean isStop = false;
    protected long startPlayTime = 0L;

    protected abstract void initPlayer();

    public abstract void startPlay(InputStream inputStream, IPlayCallBack iPlayCallBack);

    public abstract void stop();
}
