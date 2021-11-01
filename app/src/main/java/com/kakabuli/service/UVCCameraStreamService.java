package com.kakabuli.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.kakabuli.camerastream.weiget.CameraStreamManager;
import com.serenegiant.UVCPublisher;
import com.serenegiant.usb.USBMonitor;

import java.util.Iterator;
import java.util.List;

public class UVCCameraStreamService extends Service {
    private static final String TAG = "UVCCameraStreamService";

    private USBMonitor mUSBMonitor;

    private CameraStreamManager mManager;

//    private UVCPublisher mRtmpPublisher01;

    private List<UsbDevice> mAttachDevice;

//    private boolean isFirst;

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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mUSBMonitor == null) {
            mUSBMonitor = new USBMonitor(getApplicationContext(), mOnDeviceConnectListener);
            mUSBMonitor.register();
        }
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor = null;
        }
        if(mManager != null){
            mManager.release();
        }else{
            CameraStreamManager.getInstance(this).release();
        }
    }

    private void initData() {

    }

    private void initView() {
        mManager = CameraStreamManager.getInstance(this);
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Log.d(TAG, "OnDeviceConnectListener#onAttach:");
            if(mUSBMonitor.isRegistered()){
                String name = device.getConfiguration(0).getName();
                if(!TextUtils.isEmpty(name) && !"null".equals(name))
//                    mUSBMonitor.requestPermission(device);
                    mAttachDevice.add(device);
            }
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            Log.d(TAG, "OnDeviceConnectListener#onConnect:");
            String name = device.getConfiguration(0).getName();

            if(!TextUtils.isEmpty(name) && !"null".equals(name)){
                /*if(!mRtmpPublisher01.isOpened() && !isFirst) {
                    mRtmpPublisher01.stopCamera();
                    Log.d("shulan111", " 111111111111111111");
                    uvcCameraFirst = mRtmpPublisher01.startCamera(ctrlBlock);

                    mRtmpPublisher01.startPublish(RTMP_URL_02);
                    isFirst = true;
                }else if(false){

                }*/
            }


        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            Log.d(TAG, "OnDeviceConnectListener#onDisconnect:");
            /*if((mRtmpPublisher01 != null) && mRtmpPublisher01.isEqual(device)){

                mRtmpPublisher01.stopCamera();
            }*/
        }

        @Override
        public void onDettach(final UsbDevice device) {
            Log.d(TAG, "OnDeviceConnectListener#onDettach:");
            /*if((mRtmpPublisher01 != null) && mRtmpPublisher01.isEqual(device)){
                isFirst = false;
            }*/
            if(mAttachDevice != null && mAttachDevice.size() > 0){
                Iterator<UsbDevice> iterator = mAttachDevice.iterator();
                while (iterator.hasNext()){
                    UsbDevice next = iterator.next();
                    if(next != null ){

                    }
                }
            }
        }

        @Override
        public void onCancel(final UsbDevice device) {
            Log.d(TAG, "OnDeviceConnectListener#onCancel:");

        }
    };

}
