package com.kakabuli.voice.osCaeHelper;

import android.util.Log;

import com.iflytek.iflyos.cae.CAE;
import com.iflytek.iflyos.cae.ICAEListener;
import com.kakabuli.camerastream.BuildConfig;
import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.voice.caePk.OnCaeOperatorListener;
import com.kakabuli.voice.caePk.util.FileUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;


public class CaeCoreHelper {
    static {
        CAE.loadLib();
    }

    private static final CaeCoreHelper instance = new CaeCoreHelper();

    public static CaeCoreHelper getCaeOperateHelp() {
        return instance;
    }

    final static String TAG = "CaeCoreHelper";
    //添加资源文件自动copy 到对应目录下
    private static final String paramHlwName = "hlw.param";
    private static final String iniHlwName = "hlw.ini";
    private static final String caeModelName = "res_cae_model.bin";
    private static final String ivwModelName = "res_ivw_model.bin";
    private static final String authName = "auth.ini";
    private static final String mWorkDir = "/sdcard/cae/";
    private static final String mAuthDir = mWorkDir + "sn/";
    private static final String mIvwPath = mWorkDir + ivwModelName;  //资源文件路径
    private static final String mParamHlwPath = mWorkDir + paramHlwName;
    public static final String mIniHlwPath = mWorkDir + iniHlwName;
    public static final String mCaeModelPath = mWorkDir + caeModelName;
    public static final String mAuthIniPath = mAuthDir + authName;

    private CaeCoreHelper() {
        portingFile();
    }

    private OnCaeOperatorListener caeOperatorListener;

    public void setCaeOperatorListener(OnCaeOperatorListener listener) {
        this.caeOperatorListener = listener;
    }


    private void portingFile() {
        if (!hasModeFile()) {
            createWorkDir();
            MyApplication instance = MyApplication.getInstance();
            FileUtil.CopyAssets2Sdcard(instance, paramHlwName, mParamHlwPath);
            FileUtil.CopyAssets2Sdcard(instance, iniHlwName, mIniHlwPath);
            FileUtil.CopyAssets2Sdcard(instance, caeModelName, mCaeModelPath);
            FileUtil.CopyAssets2Sdcard(instance, ivwModelName, mIvwPath);
            FileUtil.CopyAssets2Sdcard(instance, authName, mAuthIniPath);
        }
    }

    private boolean hasModeFile() {
        File file = new File(mIvwPath);
        if (file != null) {
            return file.exists();
        }
        return false;
    }

    private void createWorkDir() {
        File dir = new File(mWorkDir);
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        Log.e(TAG, "createWorkDir: " + mAuthDir);
        File auth = new File(mAuthDir);
        if (auth != null && !auth.exists()) {
            auth.mkdirs();
        }
    }

    public CheckResult caeEngineInit() {
        boolean rst;
        int isInit = CAE.CAENew(mIniHlwPath, mParamHlwPath, mCAEListener);
        String ver = CAE.CAEGetVersion();
        Log.d(TAG, "EngineInit  result:    " + isInit + "version:" + ver);
        rst = isInit == 0;
        if (!rst) {
            Log.d(TAG, "初始化失败");
            return new CheckResult(false, "初始化失败");
        }
        String osAuth = "";
        File file = new File(mAuthDir);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                byte[] bytesFromFile = FileUtil.getBytesFromFile(files[0].getAbsolutePath());
                if (bytesFromFile != null) {
                    osAuth = new String(bytesFromFile);
                }
            }
        }

        Log.d(TAG, "caeEngineInit: osAuth == " + osAuth);
        int auth = CAE.CAEAuth(osAuth);
        if (auth >= 0) {
            Log.d(TAG, "鉴权成功");
            CAE.CAESetShowLog(1);
            return new CheckResult(true, "鉴权成功");
        } else {
            Log.d(TAG, "鉴权失败：" + auth);
            return new CheckResult(false, String.format(Locale.getDefault(), "鉴权失败,错误码为[ %d ]", auth));
        }
    }

    /**
     * 配置波束，4MIC 有3个波束、6MIC有6个波束
     *
     * @param beam
     */
    public void setRealBeam(int beam) {
        CAE.CAESetRealBeam(beam);
    }

    public void reLoadResource(String modeFilePath) {
        CAE.CAEReloadResource(modeFilePath);
    }

    //送入原始音频到算法中
    public void writeAudio(byte[] audio) {
        CAE.CAEAudioWrite(audio, audio.length);
    }

    //重置引擎，需要初始化引擎
    public void resetEngine() {

    }

    public void destroyCae() {
        CAE.CAEDestory();
    }

    //iflyos yue kuo banben
    private ICAEListener mCAEListener = new ICAEListener() {
        @Override
        public void onWakeup(float power, int angle, int beam, String msg) {
            if (caeOperatorListener != null) {
                if (BuildConfig.MicType.equals("6mic")) {
                    try {
                        JSONObject jsonObject = new JSONObject(msg);
                        beam = jsonObject.optInt("physical");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                caeOperatorListener.onWakeup(angle, beam);
            }
        }

        @Override
        public void onAudioCallback(byte[] audioData, int dataLen) {
            if (caeOperatorListener != null) {
                caeOperatorListener.onAudio(audioData, dataLen);
            }
        }

        @Override
        public void onIvwAudioCallback(byte[] bytes, int i) {

        }
    };

}
