package com.kakabuli.camerastream.weiget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.kakabuli.camerastream.R;
import com.serenegiant.UVCCameraView;

public class CameraStreamManager {
    private static final String TAG = "CameraStreamManager";

    private Context mContext;
    private static CameraStreamManager instance;
    private WindowManager mWindowManager;
    private View mView;
    private UVCCameraView mUVCCameraView01;
    private UVCCameraView mUVCCameraView02;
    WindowManager.LayoutParams layoutParams;
    boolean isshow = false;

    public static CameraStreamManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CameraStreamManager.class) {
                if (instance == null) {
                    instance = new CameraStreamManager(context);
                }
            }
        }
        return instance;
    }

    private CameraStreamManager(Context context){
        mContext = context;
        mView = View.inflate(mContext, R.layout.start_publish, null);
        mUVCCameraView01 = mView.findViewById(R.id.camera01);
        mUVCCameraView02 = mView.findViewById(R.id.camera02);
        initPublish();


        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    private void initPublish() {

    }

    public void startPublish(){

    }

    public void stopPublish(){

    }

    private void startService(String pkgName,String mainAct){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(pkgName, mainAct);
        intent.setComponent(componentName);
        mContext.startService(intent);
    }

    public static void releas(){


        instance = null;
    }
}
