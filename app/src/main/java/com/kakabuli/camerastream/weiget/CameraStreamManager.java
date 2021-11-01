package com.kakabuli.camerastream.weiget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.kakabuli.camerastream.R;
import com.kakabuli.camerastream.rtmp.RtmpPublisher;
import com.serenegiant.UVCCameraView;
import com.serenegiant.UVCPublisher;
import com.serenegiant.usb.UVCCamera;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;

public class CameraStreamManager {
    private static final String TAG = "CameraStreamManager";

    private Context mContext;
    public static CameraStreamManager instance;
    private WindowManager mWindowManager;
    private View mView;
    private UVCCameraView mUVCCameraView01;
    private UVCCameraView mUVCCameraView02;
    WindowManager.LayoutParams layoutParams;

    private UVCPublisher uvcPublisher01;
    private UVCPublisher uvcPublisher02;

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
        if(uvcPublisher01 == null)
            uvcPublisher01 = new UVCPublisher(mUVCCameraView01);
        uvcPublisher01.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener01));
        uvcPublisher01.setRtmpHandler(new RtmpHandler(rtmpListener01),8);
        uvcPublisher01.setRecordHandler(new SrsRecordHandler(srsRecordListener01));
        uvcPublisher01.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher01.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        uvcPublisher01.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher01.setVideoHDMode();

        if(uvcPublisher02 != null){
            uvcPublisher02 = new UVCPublisher(mUVCCameraView02);
        }
        uvcPublisher02.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener02));
        uvcPublisher02.setRtmpHandler(new RtmpHandler(rtmpListener02),8);
        uvcPublisher02.setRecordHandler(new SrsRecordHandler(srsRecordListener02));
        uvcPublisher02.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher02.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        uvcPublisher02.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher02.setVideoHDMode();
    }

    public void startPublish(){
        addView();

    }

    public void stopPublish(){
        removeView();
    }

    private void addView(){
        if(layoutParams == null){
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = dip2px(100);
            layoutParams.height = dip2px(50);
            layoutParams.gravity = Gravity.LEFT|Gravity.TOP;
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            layoutParams.format = PixelFormat.RGBA_8888;
        }
        mWindowManager.addView(mView,layoutParams);
    }

    private void removeView() {
        mWindowManager.removeView(mView);

    }

    private void startService(String pkgName,String mainAct){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(pkgName, mainAct);
        intent.setComponent(componentName);
        mContext.startService(intent);
    }

    public void releasePublish(){
        if(uvcPublisher01 != null){
            uvcPublisher01.stopPublish();
            uvcPublisher01.stopRecord();
        }

        if(uvcPublisher02 != null){
            uvcPublisher02.stopPublish();
            uvcPublisher02.stopRecord();
        }
    }

    public void release(){
        releasePublish();
        instance.removeView();

        instance = null;
    }


    public  int dip2px(float dip) {
        float density = mContext.getResources().getDisplayMetrics().density;// 设备密度
        int px = (int) (dip * density + 0.5f);// 3.1->3, 3.9+0.5->4.4->4
        return px;
    }

    private SrsEncodeHandler.SrsEncodeListener srsEncodeListener02 = new SrsEncodeHandler.SrsEncodeListener() {
        @Override
        public void onNetworkWeak() {

        }

        @Override
        public void onNetworkResume() {

        }

        @Override
        public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }
    };

    private RtmpHandler.RtmpListener rtmpListener02 = new RtmpHandler.RtmpListener() {
        @Override
        public void onRtmpConnecting(String msg) {
            Log.d(TAG, "onRtmpConnecting-->" + msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            Log.d(TAG, "onRtmpConnected-->" + msg);
        }

        @Override
        public void onRtmpVideoStreaming() {
            Log.d(TAG, "onRtmpVideoStreaming-->");
        }

        @Override
        public void onRtmpAudioStreaming() {
            Log.d(TAG, "onRtmpAudioStreaming-->");
        }

        @Override
        public void onRtmpStopped() {
            Log.d(TAG, "onRtmpStopped-->");
        }

        @Override
        public void onRtmpDisconnected() {
            Log.d(TAG, "onRtmpDisconnected-->");
        }

        @Override
        public void onRtmpVideoFpsChanged(double fps) {
            Log.d(TAG, "onRtmpVideoFpsChanged-->" + fps);
        }

        @Override
        public void onRtmpVideoBitrateChanged(double bitrate) {
            Log.d(TAG, "onRtmpVideoBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpAudioBitrateChanged(double bitrate) {
            Log.d(TAG, "onRtmpAudioBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpSocketException(SocketException e) {
            Log.d(TAG, "onRtmpSocketException-->" + e.toString());
        }

        @Override
        public void onRtmpIOException(IOException e) {
            Log.d(TAG, "onRtmpIOException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
            Log.d(TAG, "onRtmpIllegalArgumentException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalStateException(IllegalStateException e) {
            Log.d(TAG, "onRtmpIllegalStateException-->" + e.toString());
        }


    };

    private SrsRecordHandler.SrsRecordListener srsRecordListener02 = new SrsRecordHandler.SrsRecordListener() {
        @Override
        public void onRecordPause() {

        }

        @Override
        public void onRecordResume() {

        }

        @Override
        public void onRecordStarted(String msg) {

        }

        @Override
        public void onRecordFinished(String msg) {

        }

        @Override
        public void onRecordIllegalArgumentException(IllegalArgumentException e) {

        }

        @Override
        public void onRecordIOException(IOException e) {

        }
    };

    private SrsEncodeHandler.SrsEncodeListener srsEncodeListener01 = new SrsEncodeHandler.SrsEncodeListener() {
        @Override
        public void onNetworkWeak() {

        }

        @Override
        public void onNetworkResume() {

        }

        @Override
        public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
            Log.e(TAG, e.toString());
        }
    };

    private RtmpHandler.RtmpListener rtmpListener01 = new RtmpHandler.RtmpListener() {
        @Override
        public void onRtmpConnecting(String msg) {
            Log.d(TAG, "onRtmpConnecting-->" + msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            Log.d(TAG, "onRtmpConnected-->" + msg);
        }

        @Override
        public void onRtmpVideoStreaming() {
            Log.d(TAG, "onRtmpVideoStreaming-->");
        }

        @Override
        public void onRtmpAudioStreaming() {
            Log.d(TAG, "onRtmpAudioStreaming-->");
        }

        @Override
        public void onRtmpStopped() {
            Log.d(TAG, "onRtmpStopped-->");
        }

        @Override
        public void onRtmpDisconnected() {
            Log.d(TAG, "onRtmpDisconnected-->");
        }

        @Override
        public void onRtmpVideoFpsChanged(double fps) {
            Log.d(TAG, "onRtmpVideoFpsChanged-->" + fps);
        }

        @Override
        public void onRtmpVideoBitrateChanged(double bitrate) {
            Log.d(TAG, "onRtmpVideoBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpAudioBitrateChanged(double bitrate) {
            Log.d(TAG, "onRtmpAudioBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpSocketException(SocketException e) {
            Log.d(TAG, "onRtmpSocketException-->" + e.toString());
        }

        @Override
        public void onRtmpIOException(IOException e) {
            Log.d(TAG, "onRtmpIOException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
            Log.d(TAG, "onRtmpIllegalArgumentException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalStateException(IllegalStateException e) {
            Log.d(TAG, "onRtmpIllegalStateException-->" + e.toString());
        }


    };

    private SrsRecordHandler.SrsRecordListener srsRecordListener01 = new SrsRecordHandler.SrsRecordListener() {
        @Override
        public void onRecordPause() {

        }

        @Override
        public void onRecordResume() {

        }

        @Override
        public void onRecordStarted(String msg) {

        }

        @Override
        public void onRecordFinished(String msg) {

        }

        @Override
        public void onRecordIllegalArgumentException(IllegalArgumentException e) {

        }

        @Override
        public void onRecordIOException(IOException e) {

        }
    };
}
