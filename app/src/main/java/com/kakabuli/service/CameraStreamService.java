package com.kakabuli.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.kakabuli.camerastream.rtmp.RtmpPublisher;
import com.kakabuli.camerastream.weiget.CameraStreamView;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/** 后台获取数据，可行性高
 *  IFrameCallback，UVCCamera预览的每一帧都有回调
 *  数据1382400，通道数height * width * 2 / 3 (YUV420 NV21)
 *  修改libenc.cc --> git log : 182c41327e9ab60832bc2162eaeef5c4206ea0de
 *  导致绿屏，不确定lienc.so问题，还是SPS & PPS 参数问题那一部分对不上
 */

public class CameraStreamService extends Service {

    private UVCCamera uvcCameraFirst;

    private static final String TAG = "CameraStreamService";

    private USBMonitor mUSBMonitor;

    private CameraStreamView cameraStreamView01;
    private CameraStreamView cameraStreamView02;

    private RtmpPublisher mRtmpPublisher01;
    private RtmpPublisher mRtmpPublisher02;

    private List<UsbDevice> mAttachDevice;

    private boolean isFirst = false;

    private static String RTMP_URL_01 = "rtmp://118.190.36.40/devices/13723789649";
    //    private static String RTMP_URL_01 = "rtmp://192.168.1.100:1935/myapp/camera01";
    private static String RTMP_URL_02 = "rtmp://192.168.1.100:1935/myapp/camera02";


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:" + intent);
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind:" + intent);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind:" + intent);
        return super.onUnbind(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mUSBMonitor == null) {
            mUSBMonitor = new USBMonitor(getApplicationContext(), mOnDeviceConnectListener);
            mUSBMonitor.register();
        }
        initView();
        initData();
    }

    private void initView() {
        if(cameraStreamView01 == null)
            cameraStreamView01 = new CameraStreamView(this);

    }

    private void initData() {
        if(mAttachDevice == null)
            mAttachDevice = new ArrayList<>();

        if(mRtmpPublisher01 == null)
            mRtmpPublisher01 = new RtmpPublisher(cameraStreamView01);
        mRtmpPublisher01.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener1));
        mRtmpPublisher01.setRtmpHandler(new RtmpHandler(rtmpListener1),8);
        mRtmpPublisher01.setRecordHandler(new SrsRecordHandler(srsRecordListener1));
        mRtmpPublisher01.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mRtmpPublisher01.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        mRtmpPublisher01.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mRtmpPublisher01.setVideoHDMode();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mUSBMonitor == null) {
            mUSBMonitor = new USBMonitor(getApplicationContext(), mOnDeviceConnectListener);
            mUSBMonitor.register();
        }
        return super.onStartCommand(intent, flags, startId);

    }

    public void onStartRecord(){
        if(mRtmpPublisher01 != null){
            mRtmpPublisher01.resumeRecord();
        }

    }

    public void onPauseRecord(){
        if(mRtmpPublisher01 != null){
            mRtmpPublisher01.pauseRecord();
        }
    }

    public void onStartPreview(){
        if(mRtmpPublisher01 != null){
            mRtmpPublisher01.startPreview();
        }
    }

    public void onStopPreview(){
        if(mRtmpPublisher01 != null){
            mRtmpPublisher01.stopPreview();
        }
    }

    public void onStartPublish(){
        if(mRtmpPublisher01 != null){
            mRtmpPublisher01.startPublish(RTMP_URL_02);
        }
    }

    public void onStopPublish(){
        if(mRtmpPublisher01 != null){
            mRtmpPublisher01.stopPublish();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor = null;
        }
        if(mRtmpPublisher01 != null){
            mRtmpPublisher01.stopPublish();
            mRtmpPublisher01.stopRecord();
        }

    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
             Log.d(TAG, "OnDeviceConnectListener#onAttach:");
            if(mUSBMonitor.isRegistered()){
                String name = device.getConfiguration(0).getName();
                if(!TextUtils.isEmpty(name) && !"null".equals(name))
                    mUSBMonitor.requestPermission(device);
//                    mAttachDevice.add(device);
            }
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
             Log.d(TAG, "OnDeviceConnectListener#onConnect:");
            String name = device.getConfiguration(0).getName();

            if(!TextUtils.isEmpty(name) && !"null".equals(name)){
                if(!mRtmpPublisher01.isOpened() && !isFirst) {
                    mRtmpPublisher01.stopCamera();
                    Log.d("shulan111", " 111111111111111111");
                    uvcCameraFirst = mRtmpPublisher01.startCamera(ctrlBlock);

                    mRtmpPublisher01.startPublish(RTMP_URL_02);
                    isFirst = true;
                }else if(false){

                }
            }


        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
             Log.d(TAG, "OnDeviceConnectListener#onDisconnect:");
            if((mRtmpPublisher01 != null) && mRtmpPublisher01.isEqual(device)){

                mRtmpPublisher01.stopCamera();
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
             Log.d(TAG, "OnDeviceConnectListener#onDettach:");
            if((mRtmpPublisher01 != null) && mRtmpPublisher01.isEqual(device)){
                isFirst = false;
            }
        }

        @Override
        public void onCancel(final UsbDevice device) {
             Log.d(TAG, "OnDeviceConnectListener#onCancel:");

        }
    };

    private SrsEncodeHandler.SrsEncodeListener srsEncodeListener1 = new SrsEncodeHandler.SrsEncodeListener() {
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

    private RtmpHandler.RtmpListener rtmpListener1 = new RtmpHandler.RtmpListener() {
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

    private SrsRecordHandler.SrsRecordListener srsRecordListener1 = new SrsRecordHandler.SrsRecordListener() {
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
