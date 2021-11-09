package com.kakabuli.voice.caePk;

/**
 * Created by admin on 2019/5/6.
 */

public interface OnCaeOperatorListener {
    void onAudio(byte[] audioData, int dataLen);
    void onWakeup(int angle ,int beam);
    void onError(int errorCode,String msg);

}
