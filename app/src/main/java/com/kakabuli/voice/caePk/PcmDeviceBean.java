package com.kakabuli.voice.caePk;


import com.kakabuli.camerastream.BuildConfig;

public class PcmDeviceBean {
    //mic数量

    // pcm录音设备号，根据实际情况设置
    //tinycap /sdcard/test.pcm -D 3 -d 0 -c 8 -r 16000 -b 16 -p 1536 -n 10
    /**
     * pcm 声卡号 -D
     */
    private int mPcmCard = 0;

    /**
     * pcm 声卡设备号
     */
    private int mPcmDevice = 0;
    /**
     * 通道数量
     */
    private int mPcmChannel = 8;//6mic
    //private   int mPcmChannel = 4;//2mic
    /**
     * 采样率
     */
    private int mPcmSampleRate = 16000;
    /**
     * 一次中断的帧数 一般不同修改，某些不支持这么大数字时会报错，可以尝试减小改值，例如 1023
     */
    private final int mPcmPeriodSize = 1536;
    /**
     * 周期数 一般不同修改
     */
    private final int mPcmPeriodCount = 8;
    /**
     * pcm 位宽 0-PCM_FORMAT_S16_LE、<br>1-PCM_FORMAT_S32_LE、<br>2-PCM_FORMAT_S8、<br>3-PCM_FORMAT_S24_LE、<br>4-PCM_FORMAT_MAX
     */
    private int mPcmFormat = 0;

    private int micCount = 0 ;

    private MicType micType;

    public PcmDeviceBean(int card) {
        mPcmCard = card;
        switch (BuildConfig.MicType) {
            case "2mic":
                micType = MicType.DulMic;
                mPcmChannel = 4;
                micCount =2 ;
                break;
            case "4mic":
                micType = MicType.FMic;
              /*  if (BuildConfig.Platform.equals("Rk3326")) {
                    mPcmChannel = 6;
                } else {
                    mPcmChannel = 8;
                }*/
                mPcmChannel = 8;
                micCount =4;
                break;
            case "6mic":
                micType = MicType.SMic;
                mPcmChannel = 8;
                micCount =6 ;
                break;
        }
    /*    if (BuildConfig.Platform.equals("Rk3326")) {
            mPcmDevice = 1;
        }*/
    }

    public int getmPcmCard() {
        return mPcmCard;
    }

    public int getmPcmChannel() {
        return mPcmChannel;
    }

    public int getmPcmDevice() {
        return mPcmDevice;
    }

    public int getmPcmFormat() {
        return mPcmFormat;
    }

    public int getmPcmPeriodCount() {
        return mPcmPeriodCount;
    }

    public int getmPcmPeriodSize() {
        return mPcmPeriodSize;
    }

    public int getmPcmSampleRate() {
        return mPcmSampleRate;
    }

    public MicType getMicType() {
        return micType;
    }

    public int getMicCount() {
        return micCount;
    }

    @Override
    public String toString() {
        return "[" +
                "mPcmCard=" + mPcmCard +
                ", mPcmChannel=" + mPcmChannel +
                ", mPcmSampleRate=" + mPcmSampleRate +
                ", mPcmFormat=" + mPcmFormat +
                ", mPcmDevice=" + mPcmDevice +
                ", micType=" + micType +
                ", micCount=" + micCount +
                ']';
    }
}
