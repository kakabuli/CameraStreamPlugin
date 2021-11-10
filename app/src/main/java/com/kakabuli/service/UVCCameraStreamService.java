package com.kakabuli.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.kakabuli.camerastream.R;
import com.kakabuli.camerastream.rtmp.RtmpPublisher;
import com.kakabuli.camerastream.weiget.CameraStreamManager;
import com.serenegiant.UVCCameraView;
import com.serenegiant.UVCPublisher;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UVCCameraStreamService extends Service {
    private static final String TAG = "UVCCameraStreamService";

    private USBMonitor mUSBMonitor;
    private Context mContext;
    private View mView;
    private UVCCameraView mUVCCameraView01;
    private UVCCameraView mUVCCameraView02;

//    private CameraStreamManager mManager;

    private UVCPublisher mRtmpPublisher01;
    private UVCPublisher mRtmpPublisher02;

    private List<UsbDevice> mAttachDevice;
    private WindowManager mWindowManager;
    WindowManager.LayoutParams layoutParams;

    private boolean isFirst;
    private UVCCamera uvcCameraFirst;
    private UVCCamera uvcCameraSecond;

    private static String RTMP_URL_01 = "rtmp://192.168.1.100:1935/myapp/camera01";
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
        Log.d(TAG,"onCreate--->");
        mContext = this;
        if (mUSBMonitor == null) {
            mUSBMonitor = new USBMonitor(getApplicationContext(), mOnDeviceConnectListener);
            mUSBMonitor.register();
        }
        initView();
        initData();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand--->");
        mContext = this;
        if(mAttachDevice == null)
            mAttachDevice = new ArrayList<>();

        if (mUSBMonitor == null) {
            mUSBMonitor = new USBMonitor(getApplicationContext(), mOnDeviceConnectListener);
            mUSBMonitor.register();
        }


        mRtmpPublisher01.startPreview();
        mRtmpPublisher02.startPreview();

        mRtmpPublisher01.resumeRecord();
        mRtmpPublisher02.resumeRecord();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeView();
        mRtmpPublisher01.stopPreview();
        mRtmpPublisher02.stopPreview();
        mRtmpPublisher01.stopPublish();
        mRtmpPublisher02.stopPublish();
        mRtmpPublisher01.stopRecord();
        mRtmpPublisher02.stopRecord();
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor = null;
        }

    }

    private void initData() {

    }

    private void initView() {
        mView = View.inflate(mContext, R.layout.start_publish, null);
        mUVCCameraView01 = mView.findViewById(R.id.camera01);
        mUVCCameraView02 = mView.findViewById(R.id.camera02);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);


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


        initPublish();
    }

    private void removeView() {
        mWindowManager.removeView(mView);

    }

    private void initPublish() {

        if(mRtmpPublisher01 == null)
            mRtmpPublisher01 = new UVCPublisher(mUVCCameraView01);
        mRtmpPublisher01.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener1));
        mRtmpPublisher01.setRtmpHandler(new RtmpHandler(rtmpListener1),7);
        mRtmpPublisher01.setRecordHandler(new SrsRecordHandler(srsRecordListener1));
        mRtmpPublisher01.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mRtmpPublisher01.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        mRtmpPublisher01.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mRtmpPublisher01.setVideoHDMode();


        if(mRtmpPublisher02 == null)
            mRtmpPublisher02 = new UVCPublisher(mUVCCameraView02);
        mRtmpPublisher02.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener2));
        mRtmpPublisher02.setRtmpHandler(new RtmpHandler(rtmpListener2),8);
        mRtmpPublisher02.setRecordHandler(new SrsRecordHandler(srsRecordListener2));
        mRtmpPublisher02.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mRtmpPublisher02.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        mRtmpPublisher02.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mRtmpPublisher02.setVideoHDMode();
    }

    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Log.d(TAG, "OnDeviceConnectListener#onAttach:");
            if(mUSBMonitor.isRegistered()){
                String name = device.getConfiguration(0).getName();
                if(!TextUtils.isEmpty(name) && !"null".equals(name)){
                    mUSBMonitor.requestPermission(device);
                    mAttachDevice.add(device);

                }
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

                    mRtmpPublisher01.startPublish(RTMP_URL_01);
                    isFirst = true;
                }else if(!mRtmpPublisher02.isOpened()){
                    mRtmpPublisher02.stopCamera();
                    uvcCameraSecond = mRtmpPublisher02.startCamera(ctrlBlock);
                    if(!TextUtils.isEmpty(RTMP_URL_02))
                        mRtmpPublisher02.startPublish(RTMP_URL_02);
                }
            }


        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            Log.d(TAG, "OnDeviceConnectListener#onDisconnect:");
            if((mRtmpPublisher01 != null) && mRtmpPublisher01.isEqual(device)){
                removeView();
                mRtmpPublisher01.stopCamera();
            }

            if((mRtmpPublisher02 != null) && mRtmpPublisher02.isEqual(device)){
                removeView();
                mRtmpPublisher02.stopCamera();
            }
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



    ///
    private SrsEncodeHandler.SrsEncodeListener srsEncodeListener2 = new SrsEncodeHandler.SrsEncodeListener() {
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

    private RtmpHandler.RtmpListener rtmpListener2 = new RtmpHandler.RtmpListener() {
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

    private SrsRecordHandler.SrsRecordListener srsRecordListener2 = new SrsRecordHandler.SrsRecordListener() {
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


    public  int dip2px(float dip) {
        float density = mContext.getResources().getDisplayMetrics().density;// 设备密度
        int px = (int) (dip * density + 0.5f);// 3.1->3, 3.9+0.5->4.4->4
        return px;
    }
}
