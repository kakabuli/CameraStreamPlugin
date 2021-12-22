package com.kakabuli.voice.caePk;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

//import com.iflytek.alsa.bothlentrecorder.AlsaRecorder;
//import com.iflytek.alsa.bothlentrecorder.IPcmCallBack;
//import com.iflytek.alsa.bothlentrecorder.ResultType;
import com.kakabuli.camerastream.BuildConfig;
import com.kakabuli.voice.caePk.util.AudioFormatUtil;
import com.kakabuli.voice.caePk.util.PcmFileUtil;
import com.kakabuli.voice.caePk.utils.ShellUtils;
import com.kakabuli.voice.osCaeHelper.CaeCoreHelper;
import com.kakabuli.voice.osCaeHelper.CheckResult;
import com.kakabuli.voice.single.IRecordListener;
import com.kakabuli.voice.single.RecordThread;

import java.util.Locale;

/**
 * rk3288
 */

public class CaeOperator {
    private static final CaeOperator instance = new CaeOperator();

    public static CaeOperator getInstance() {
        return instance;
    }

    private CaeOperator() {
    }

    private final String TAG = CaeOperator.class.getSimpleName();
//    private AlsaRecorder mAlsaRecorder;
    private RecordThread recordThread;
    // 唤醒成功后抛出的音频保存路径
    private final String mAsrAudioDir = "/sdcard/cae/CAEAsrAudio/";
    // 多通道原始音频保存路径
    private final String mRawAudioDir = "/sdcard/cae/CAERawAudio/";
    private final String mPcmAudioDir = "/sdcard/cae/PcmAudio/";  // 从alsa里面录到的音频
    private PcmFileUtil mPcmFileUtil;
    private CaeCoreHelper mCaeCoreHelper;
    private PcmFileUtil mAsrFileUtil;  // 保存唤醒降噪后录音
    private PcmFileUtil mRawFileUtil;   // 保存多通道原始录音数据
    //原始音频保存，true：保存音频
    public boolean isSaveAudio = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private void onPcmDataHandler(byte[] bytes) {
        if (isSaveAudio) {
            mPcmFileUtil.write(bytes, 0, bytes.length);
        }
        if (null != mCaeCoreHelper) {
            byte[] data = null;
            MicType micType = pcmDeviceBean.getMicType();
            if (MicType.DulMic == micType) {//2mic
                data = AudioFormatUtil.addCnFor2Mic(bytes);
            } else if (MicType.SMic == micType) {//6mic
                data = AudioFormatUtil.addCnForMutiMic(bytes);
            } else if (MicType.FMic == micType) {//4mic 16bits
                int pcmChannel = pcmDeviceBean.getmPcmChannel();
                if (pcmChannel == 8) {
                    data = AudioFormatUtil.adapter4Mic32bit(bytes);
                } else if (pcmChannel == 6) {
                    data = AudioFormatUtil.adapter4Mic6Ch32bit(bytes);
                }
            }
            if (data != null) {
                writeToCae(data);
            }
        }
    }

    private void writeToCae(byte[] data) {
        if (mCaeCoreHelper != null) {
//            mCaeCoreHelper.writeAudio(data);
//            Log.e(TAG, "=========== :   " + data.length);
            if (isSaveAudio && mRawFileUtil != null) {
                mRawFileUtil.write(data, 0, data.length);
            }
        }
    }

    private final OnCaeOperatorListener mOnCaeOperatorListener = new OnCaeOperatorListener() {
        @Override
        public void onAudio(byte[] audioData, int dataLen) {
            if (isSaveAudio) {//保存降噪后音频
                mAsrFileUtil.write(audioData, 0, audioData.length);
            }
            if (null != caeListenerEnd) {
                caeListenerEnd.onAudio(audioData, dataLen);
            }
        }

        @Override
        public void onWakeup(int angle, int beam) {
            if (null != caeListenerEnd) {
                caeListenerEnd.onWakeup(angle, beam);
            }
        }

        @Override
        public void onError(int errorCode, String msg) {
            if (null != caeListenerEnd) {
                caeListenerEnd.onError(errorCode, msg);
            }
        }
    };

    private OnCaeOperatorListener caeListenerEnd;

    public void setRealBeam(int beam) {
        if (null != mCaeCoreHelper) {
            // Log.d(TAG," mcaeCoreHelper.writeAudio(data)");
//            mCaeCoreHelper.setRealBeam(beam);
        }
    }

    public void setCaeOperatorListener(OnCaeOperatorListener onCaeOperatorlistener) {
        caeListenerEnd = onCaeOperatorlistener;
        mCaeCoreHelper = CaeCoreHelper.getCaeOperateHelp();
        mCaeCoreHelper.setCaeOperatorListener(mOnCaeOperatorListener);
    }

    private PcmDeviceBean pcmDeviceBean;

//    public CheckResult openAndStartRecord(int card) {
//        //开始创建录音声卡实例
//        if (BuildConfig.MicType.equals("1mic")) {
//            recordThread = RecordThread.getInstance(iRecordListener);
//            recordThread.start();
//            createFile();
//            return new CheckResult(true, "打开成功:");
//        } else {
//            int pcmCard = 0;
//            if (card < 0) {
//                pcmCard = ShellUtils.fetchCards();
//                if (pcmCard < 0) {
//                    boolean b = ShellUtils.haveRoot();
//                    if (b) {
//                        return new CheckResult(false, "没有找到USB声卡");
//                    } else {
//                        return new CheckResult(false, "没有找到USB声卡,没有root权限");
//                    }
//                }
//            } else {
//                pcmCard = card;
//            }
//            pcmDeviceBean = new PcmDeviceBean(pcmCard);
//            Log.e(TAG, "openAndStartRecord:   " + pcmDeviceBean);
//            mAlsaRecorder = AlsaRecorder.createInstance(pcmDeviceBean.getmPcmCard(), pcmDeviceBean.getmPcmDevice(), pcmDeviceBean.getmPcmChannel(),
//                    pcmDeviceBean.getmPcmSampleRate(),
//                    pcmDeviceBean.getmPcmPeriodSize(), pcmDeviceBean.getmPcmPeriodCount(),
//                    pcmDeviceBean.getmPcmFormat(), pcmDeviceBean.getMicCount());
//            mAlsaRecorder.setLogShow(false);
//            return startRecord();
//        }
//    }

    private final IRecordListener iRecordListener = audioData -> {
        if (isSaveAudio) {
            mPcmFileUtil.write(audioData, 0, audioData.length);
        }
        byte[] data = AudioFormatUtil.adapeter1Mic(audioData);
        writeToCae(data);
    };

    private CheckResult checkResult;

//    private CheckResult startRecord() {
//        checkResult = new CheckResult(true, "打开成功:" + pcmDeviceBean);
//        if (mAlsaRecorder != null) {
//            mAlsaRecorder.startRecording(new IPcmCallBack() {
//                @Override
//                public void onPcmData(byte[] bytes, int i) {
//                    //Log.e(TAG, "onPcmData: "+bytes.length );
//                    onPcmDataHandler(bytes);
//                }
//
//                @Override
//                public void onState(ResultType resultType) {
//                    Log.e(TAG, "onState: " + resultType);
//                    switch (resultType) {
//                        case OPEN_ERROR:
//                            Log.i(TAG, "start recording fail..." + pcmDeviceBean);
//                            checkResult = new CheckResult(false, String.format(Locale.getDefault(), "打开失败,错误码为[ %d ]", -1));
//                            break;
//                        case OPEN_SUCCESS:
//                            createFile();
//                            break;
//                        case READ_DEVICE_ERROR:
//                            mAlsaRecorder.stopRecording();
//                            handler.removeCallbacksAndMessages(null);
//                            handler.postDelayed(() -> {
//                                startRecord();
//                            }, 200);
//                            break;
//                    }
//                }
//            });
//        } else {
//            Log.d(TAG, "AlsaRecorder is null ..  .");
//            checkResult = new CheckResult(false, "打开失败 null");
//        }
//        return checkResult;
//    }

    private void createFile() {
        mAsrFileUtil = new PcmFileUtil(mAsrAudioDir);
        //原始音频
        mRawFileUtil = new PcmFileUtil(mRawAudioDir);
        //从设备中获取到的数据
        mPcmFileUtil = new PcmFileUtil(mPcmAudioDir);
        mAsrFileUtil.createPcmFile();
        mPcmFileUtil.createPcmFile();
        mRawFileUtil.createPcmFile();
    }

    public void stopRecord() {
//        if (mAlsaRecorder != null) {
//            mAlsaRecorder.stopRecording();
//        }
        if (isSaveAudio) {
            mAsrFileUtil.closeWriteFile();
            mRawFileUtil.closeWriteFile();
            mPcmFileUtil.closeWriteFile();
        }
        Log.d(TAG, "stopRecd ok....");
    }

    public void restCaeEngine() {
        if (null != mCaeCoreHelper) {
//            mCaeCoreHelper.resetEngine();
        }
    }

    public void releaseCae() {
        if (isSaveAudio) {
            mAsrFileUtil.closeWriteFile();
            mRawFileUtil.closeWriteFile();
            mPcmFileUtil.closeWriteFile();
        }
//        if (mAlsaRecorder != null) {
//            mAlsaRecorder.stopRecording();
//        }
        if (mCaeCoreHelper != null) {
//            mCaeCoreHelper.resetEngine();
            mCaeCoreHelper = null;
        }
        if (recordThread != null) {
            recordThread.stopRec();
        }
    }
}
