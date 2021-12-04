package com.kakabuli.camerastream;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakabuli.camerastream.socket.result.VideoPlayResult;
import com.kakabuli.camerastream.utils.DataConversion;
import com.serenegiant.UVCCameraView;
import com.serenegiant.UVCPublisher;
import com.serenegiant.dialog.MessageDialogFragment;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.utils.BuildCheck;
import com.serenegiant.utils.PermissionCheck;
import com.kakabuli.camerastream.http.NewServiceImp;
import com.kakabuli.camerastream.http.result.LoginResult;
import com.kakabuli.camerastream.socket.IRTMPListener;
import com.kakabuli.camerastream.socket.MySocket;
import com.kakabuli.camerastream.socket.SocketConstants;
import com.kakabuli.camerastream.socket.result.BaseResult;
import com.kakabuli.camerastream.utils.Constants;
import com.kakabuli.camerastream.utils.MMKVUtils;
import com.tencent.mmkv.MMKV;
import com.zdx.serialportlibrary.Device;
import com.zdx.serialportlibrary.SerialPortFinder;
import com.zdx.serialportlibrary.SerialPortManager;
import com.zdx.serialportlibrary.listener.OnOpenSerialPortListener;
import com.zdx.serialportlibrary.listener.OnSerialPortDataListener;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/** 双UVC Camera 前台预览推流界面
 *
 */
public class MainActivity extends Activity implements MessageDialogFragment.MessageDialogListener, CameraDialog.CameraDialogParent {
    private UVCCameraView uvcCameraView;
    private UVCCameraView uvcCameraView1;
    private Button btn_start;

    //USB挂载设备的控制器
    private USBMonitor usbMonitor;
    private UVCCamera uvcCameraFirst;
    private UVCCamera uvcCameraSecond;
    private UVCPublisher uvcPublisher;
    private UVCPublisher uvcPublisher1;
    //工程外网搭建的rtmp地址
    private static String RTMP_URL_01 = "rtmp://118.190.36.40/devices/13723789649";//rtmp://118.190.36.40/devices/13723789649
//    private static String RTMP_URL_01 = "rtmp://192.168.1.100:1935/myapp/camera01";
    private static String RTMP_URL_02 = "";//rtmp://192.168.1.100:1935/myapp/camera02
    //http 设备登录返回的token
    private String token = "";
    //用于通过websocket登录，收到VIDEO_PLAY指令来获取rtmp地址
    private MySocket mMySocket;
    //获取usb挂载的设备，用于收到socket指令来开启UVCCamera
    private List<UsbDevice> mAttachDevice;

    // 打开USB串口
    private SerialPortManager mUSB0SerialPortManager, mUSB1SerialPortManager, mUSB2SerialPortManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
//        try {
//            copyBigDataToSD("/sdcard/msc/msc.cfg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        initOpenUSBSerialPort();

    }

    private void copyBigDataToSD(String strOutFileName) throws IOException
    {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = this.getAssets().open("msc.cfg");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initOpenUSBSerialPort() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();

        ArrayList<Device> devices = serialPortFinder.getDevices();

        mUSB0SerialPortManager = new SerialPortManager();
        mUSB1SerialPortManager = new SerialPortManager();
        mUSB2SerialPortManager = new SerialPortManager();

        Device deviceUSB0 = null, deviceUSB1 = null, deviceUSB2 = null;
        for (Device device : devices) {
            if (device.getName().equals("ttyUSB0")) {
                deviceUSB0 = device;
            } else if (device.getName().equals("ttyUSB1")) {
                deviceUSB1 = device;
            } else if (device.getName().equals("ttyUSB2")) {
                deviceUSB2 = device;
            }
        }

        if (deviceUSB0 != null) {
            boolean openUSB0 = mUSB0SerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
                @Override
                public void onSuccess(File device) {

                }

                @Override
                public void onFail(File device, Status status) {

                }
            }).setOnSerialPortDataListener(new OnSerialPortDataListener() {
                @Override
                public void onDataReceived(byte[] bytes) {
                    Log.d("USB", "USB0: " + DataConversion.encodeHexString(bytes));
                }

                @Override
                public void onDataSent(byte[] bytes) {

                }
            }).openSerialPort(deviceUSB0.getFile(), 115200);
        }
        if (deviceUSB1 != null) {
            boolean openUSB1 = mUSB1SerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
                @Override
                public void onSuccess(File device) {

                }

                @Override
                public void onFail(File device, Status status) {

                }
            }).setOnSerialPortDataListener(new OnSerialPortDataListener() {
                @Override
                public void onDataReceived(byte[] bytes) {
                    Log.d("USB", "USB1: " + DataConversion.encodeHexString(bytes));
                }

                @Override
                public void onDataSent(byte[] bytes) {

                }
            }).openSerialPort(deviceUSB1.getFile(), 115200);
        }
        if (deviceUSB2 != null) {
            boolean openUSB2 = mUSB2SerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
                @Override
                public void onSuccess(File device) {

                }

                @Override
                public void onFail(File device, Status status) {

                }
            }).setOnSerialPortDataListener(new OnSerialPortDataListener() {
                @Override
                public void onDataReceived(byte[] bytes) {
                    Log.d("USB", "USB2: " + DataConversion.encodeHexString(bytes));
                }

                @Override
                public void onDataSent(byte[] bytes) {

                }
            }).openSerialPort(deviceUSB2.getFile(), 115200);
        }
    }

    private void initSocket(String deviceToken) {
        Log.d("liuhai_MySocket","initSocket------------");
        mMySocket = new MySocket(URI.create(SocketConstants.SOCKET_URL + SocketConstants.LOGIN + deviceToken), new IRTMPListener() {
            @Override
            public void onSocketConnect(int code, String message) {

            }

            @Override
            public void onSocketMessage(String message) {
                BaseResult mBaseResult = new Gson().fromJson(message,
                        new TypeToken<BaseResult>() {}.getType());
                switch (mBaseResult.getType()){
                    case Constants.TASK_LOGIN://socket登录
                        break;
//                    case Constants.VIDEO_PLAY:
                    case Constants.TASK_CHECK_CALLBACK://TODO 2021-11-10 需要本地测试rtmp 视频流打开这个，还有socket登录之后，发送拉取任务指令
                        try{
                            VideoPlayResult mVideoPlayResult = new Gson().fromJson(message, new TypeToken<VideoPlayResult>() {}.getType());
                            Log.e("shulan111","mVideoPlayResult--->" + mVideoPlayResult.toString());
                            Log.e("shulan111","mVideoPlayResult.getData().getRtmpUrl()--->" + mVideoPlayResult.getData().getRtmpUrl());
                            if(!TextUtils.isEmpty(mVideoPlayResult.getData().getRtmpUrl()))
                                RTMP_URL_01 = mVideoPlayResult.getData().getRtmpUrl();
                        }catch (Exception e){

                        }
                        requestUSBMonitor();
                        break;
                    case Constants.VIDEO_PLAY_STOP://
                        if(uvcPublisher != null){
                            uvcPublisher.stopCamera();
                        }
                        if(uvcPublisher1 != null){
                            uvcPublisher1.stopCamera();
                        }
                        break;
                }
            }

            @Override
            public void onSocketClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onSocketError(Exception e) {

            }
        });
        mMySocket.connect();
    }

    private void initView() {
        uvcCameraView = (UVCCameraView) findViewById(R.id.uvcCameraView);
        uvcCameraView1 = (UVCCameraView) findViewById(R.id.uvcCameraView1);
        btn_start = (Button) findViewById(R.id.btn_start);
    }

    private void initData() {
        btn_start.setOnClickListener(clickListener);

        mAttachDevice = new ArrayList<>();
        usbMonitor = new USBMonitor(this, deviceConnectListener);

        uvcPublisher1 = new UVCPublisher(uvcCameraView1);
        uvcPublisher1.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener1));
        uvcPublisher1.setRtmpHandler(new RtmpHandler(rtmpListener1),8);
        uvcPublisher1.setRecordHandler(new SrsRecordHandler(srsRecordListener1));
        uvcPublisher1.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher1.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        uvcPublisher1.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher1.setVideoHDMode();

        uvcPublisher = new UVCPublisher(uvcCameraView);
        uvcPublisher.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener));
        uvcPublisher.setRtmpHandler(new RtmpHandler(rtmpListener),9);
        uvcPublisher.setRecordHandler(new SrsRecordHandler(srsRecordListener));
        uvcPublisher.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        uvcPublisher.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher.setVideoHDMode();
    }

    /**
     *  请求UVC Camera连接
     */
    private void requestUSBMonitor(){
        Log.d("shulan111","+++requestUSBMonitor");
         if(usbMonitor.isRegistered()){
             if(mAttachDevice != null && mAttachDevice.size() > 0)
             for (UsbDevice device : mAttachDevice) {
                 Log.d("shulan111",usbMonitor.isRegistered() + " ");
                 String name = device.getConfiguration(0).getName();
                 if(!TextUtils.isEmpty(name) && !"null".equals(name))//存在USB设备卷头为空，即无设备的判断
                     usbMonitor.requestPermission(device);
             }
            }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_start) {
                if (uvcCameraFirst == null && uvcCameraSecond == null) {
//                    CameraDialog.showDialog(MainActivity.this);
                } else {
                    uvcPublisher.stopCamera();
                }
            }
        }
    };
    private boolean isFirst = false;
    private USBMonitor.OnDeviceConnectListener deviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "onAttach已关联", Toast.LENGTH_LONG).show();
                }
            });
            if(usbMonitor.isRegistered()){
                String name = device.getConfiguration(0).getName();
                if(!TextUtils.isEmpty(name) && !"null".equals(name))
                    mAttachDevice.add(device);
            }


            Log.d("shulan111","mAttachDevice.size()-->" + mAttachDevice.size());
            Log.d("shulan111","mAttachDevice.tostring-->" + mAttachDevice.toString());
        }

        @Override
        public void onDettach(UsbDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "onDettach失去关联", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "onConnect已连接", Toast.LENGTH_LONG).show();
                }
            });

            if(!uvcPublisher.isOpened() && !isFirst){
                uvcPublisher.stopCamera();
                Log.d("shulan111", " 111111111111111111");
                uvcCameraFirst = uvcPublisher.startCamera(ctrlBlock);
                if (uvcCameraFirst == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "摄像头打开失败", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                if(!TextUtils.isEmpty(RTMP_URL_01))
                    uvcPublisher.startPublish(RTMP_URL_01);
                isFirst = true;
            }else if(!uvcPublisher1.isOpened()){
                uvcPublisher1.stopCamera();
                uvcCameraSecond = uvcPublisher1.startCamera(ctrlBlock);
                if(uvcCameraSecond == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "摄像头打开失败", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                if(!TextUtils.isEmpty(RTMP_URL_02))
                    uvcPublisher1.startPublish(RTMP_URL_02);
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "摄像头打开成功，开始推流", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "onDisconnect断开连接", Toast.LENGTH_LONG).show();
                }
            });
            if((uvcPublisher != null) && uvcPublisher.isEqual(device)){

                uvcPublisher.stopCamera();
            }

            if((uvcPublisher1 != null) && uvcPublisher1.isEqual(device)){

                uvcPublisher1.stopCamera();
            }
        }

        @Override
        public void onCancel(UsbDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(com.kakabuli.camerastream.MainActivity.this, "onCancel已取消", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private RtmpHandler.RtmpListener rtmpListener = new RtmpHandler.RtmpListener() {
        @Override
        public void onRtmpConnecting(String msg) {
            Log.d("shulan222","onRtmpConnecting-->" + msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            Log.d("shulan222","onRtmpConnected-->" + msg);
        }

        @Override
        public void onRtmpVideoStreaming() {
            Log.d("shulan222","onRtmpVideoStreaming-->");
        }

        @Override
        public void onRtmpAudioStreaming() {
            Log.d("shulan222","onRtmpAudioStreaming-->");
        }

        @Override
        public void onRtmpStopped() {
            Log.d("shulan222","onRtmpStopped-->");
        }

        @Override
        public void onRtmpDisconnected() {
            Log.d("shulan222","onRtmpDisconnected-->");
        }

        @Override
        public void onRtmpVideoFpsChanged(double fps) {
            Log.d("shulan222","onRtmpVideoFpsChanged-->" + fps);
        }

        @Override
        public void onRtmpVideoBitrateChanged(double bitrate) {
            Log.d("shulan222","onRtmpVideoBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpAudioBitrateChanged(double bitrate) {
            Log.d("shulan222","onRtmpAudioBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpSocketException(SocketException e) {
            Log.d("shulan222","onRtmpSocketException-->" + e.toString());
        }

        @Override
        public void onRtmpIOException(IOException e) {
            Log.d("shulan222","onRtmpIOException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
            Log.d("shulan222","onRtmpIllegalArgumentException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalStateException(IllegalStateException e) {
            Log.d("shulan222","onRtmpIllegalStateException-->" + e.toString());
        }
    };

    private RtmpHandler.RtmpListener rtmpListener1 = new RtmpHandler.RtmpListener() {
        @Override
        public void onRtmpConnecting(String msg) {
            Log.d("shulan333", "onRtmpConnecting-->" + msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            Log.d("shulan333", "onRtmpConnected-->" + msg);
        }

        @Override
        public void onRtmpVideoStreaming() {
            Log.d("shulan333", "onRtmpVideoStreaming-->");
        }

        @Override
        public void onRtmpAudioStreaming() {
            Log.d("shulan333", "onRtmpAudioStreaming-->");
        }

        @Override
        public void onRtmpStopped() {
            Log.d("shulan333", "onRtmpStopped-->");
        }

        @Override
        public void onRtmpDisconnected() {
            Log.d("shulan333", "onRtmpDisconnected-->");
        }

        @Override
        public void onRtmpVideoFpsChanged(double fps) {
            Log.d("shulan333", "onRtmpVideoFpsChanged-->" + fps);
        }

        @Override
        public void onRtmpVideoBitrateChanged(double bitrate) {
            Log.d("shulan333", "onRtmpVideoBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpAudioBitrateChanged(double bitrate) {
            Log.d("shulan333", "onRtmpAudioBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpSocketException(SocketException e) {
            Log.d("shulan333", "onRtmpSocketException-->" + e.toString());
        }

        @Override
        public void onRtmpIOException(IOException e) {
            Log.d("shulan333", "onRtmpIOException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
            Log.d("shulan333", "onRtmpIllegalArgumentException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalStateException(IllegalStateException e) {
            Log.d("shulan333", "onRtmpIllegalStateException-->" + e.toString());
        }


    };

    private SrsRecordHandler.SrsRecordListener srsRecordListener = new SrsRecordHandler.SrsRecordListener() {
        @Override
        public void onRecordPause() {
            Log.d("shulan222","onRecordPause");
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


    private SrsEncodeHandler.SrsEncodeListener srsEncodeListener1 = new SrsEncodeHandler.SrsEncodeListener() {
        @Override
        public void onNetworkWeak() {

        }

        @Override
        public void onNetworkResume() {

        }

        @Override
        public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
            Log.e(com.kakabuli.camerastream.MainActivity.class.getSimpleName(), e.toString());
        }
    };

    private SrsEncodeHandler.SrsEncodeListener srsEncodeListener = new SrsEncodeHandler.SrsEncodeListener() {
        @Override
        public void onNetworkWeak() {

        }

        @Override
        public void onNetworkResume() {

        }

        @Override
        public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
            Log.e(com.kakabuli.camerastream.MainActivity.class.getSimpleName(), e.toString());
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        usbMonitor.register();
        uvcPublisher.startPreview();
        uvcPublisher1.startPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uvcPublisher.resumeRecord();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserToken();
            }
        },800);*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(MyApplication.getInstance().getToken()) || !TextUtils.isEmpty(MMKVUtils.getStringMMKV(Constants.DEVICE_TOKEN))){
                    if(TextUtils.isEmpty(MyApplication.getInstance().getToken())){
                        initSocket(MMKVUtils.getStringMMKV(Constants.DEVICE_TOKEN));
                    }else{
                        initSocket(MyApplication.getInstance().getToken());
                    }
                }
            }
        },1500);

    }

    private void getUserToken() {
        NewServiceImp.login(/*"13723789649","123456"*/"admin","admin").subscribe(new Observer<LoginResult>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d("shulan_http","onSubscribe");
            }

            @Override
            public void onNext(@NonNull LoginResult loginResult) {
                Log.d("shulan_http","onNext-->" + loginResult.toString());

                if(loginResult.getMeta().getCode() == 200 && loginResult.getMeta().isSuccess()) {
                    if(!TextUtils.isEmpty(loginResult.getData().getToken())){
                        token =  loginResult.getData().getToken();
                        MMKVUtils.setMMKV(Constants.DEVICE_TOKEN,loginResult.getData().getToken());
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("shulan_http","onError");
            }

            @Override
            public void onComplete() {
                Log.d("shulan_http","onComplete");
                if(!TextUtils.isEmpty(token))
                    initSocket(token);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        uvcPublisher.pauseRecord();
        uvcPublisher1.pauseRecord();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uvcPublisher.stopPreview();
        uvcPublisher1.stopPreview();
        usbMonitor.unregister();
    }

    @Override
    protected void onDestroy() {
        if (mUSB0SerialPortManager != null) mUSB0SerialPortManager.closeSerialPort();
        if (mUSB1SerialPortManager != null) mUSB1SerialPortManager.closeSerialPort();
        if (mUSB2SerialPortManager != null) mUSB2SerialPortManager.closeSerialPort();

        super.onDestroy();
        uvcPublisher.stopPublish();
        uvcPublisher1.stopPublish();
        uvcPublisher1.stopRecord();
        uvcPublisher.stopRecord();
        usbMonitor.unregister();
    }

    //================================================================================

    /**
     * MessageDialogFragmentメッセージダイアログからのコールバックリスナー
     *
     * @param dialog
     * @param requestCode
     * @param permissions
     * @param result
     */
    @SuppressLint("NewApi")
    @Override
    public void onMessageDialogResult(final MessageDialogFragment dialog, final int requestCode, final String[] permissions, final boolean result) {
        if (result) {
            // メッセージダイアログでOKを押された時はパーミッション要求する
            if (BuildCheck.isMarshmallow()) {
                requestPermissions(permissions, requestCode);
                return;
            }
        }
        // メッセージダイアログでキャンセルされた時とAndroid6でない時は自前でチェックして#checkPermissionResultを呼び出す
        for (final String permission : permissions) {
            checkPermissionResult(requestCode, permission, PermissionCheck.hasPermission(this, permission));
        }
    }

    /**
     * パーミッション要求結果を受け取るためのメソッド
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);    // 何もしてないけど一応呼んどく
        final int n = Math.min(permissions.length, grantResults.length);
        for (int i = 0; i < n; i++) {
            checkPermissionResult(requestCode, permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * パーミッション要求の結果をチェック
     * ここではパーミッションを取得できなかった時にToastでメッセージ表示するだけ
     *
     * @param requestCode
     * @param permission
     * @param result
     */
    protected void checkPermissionResult(final int requestCode, final String permission, final boolean result) {
        // パーミッションがないときにはメッセージを表示する
        if (!result && (permission != null)) {
            if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(com.kakabuli.camerastream.MainActivity.this, R.string.permission_audio, Toast.LENGTH_LONG).show();
                    }
                });
            }
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(com.kakabuli.camerastream.MainActivity.this, R.string.permission_ext_storage, Toast.LENGTH_LONG).show();
                    }
                });
            }
            if (Manifest.permission.INTERNET.equals(permission)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(com.kakabuli.camerastream.MainActivity.this, R.string.permission_network, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    // 動的パーミッション要求時の要求コード
    protected static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x12345;
    protected static final int REQUEST_PERMISSION_AUDIO_RECORDING = 0x234567;
    protected static final int REQUEST_PERMISSION_NETWORK = 0x345678;
    protected static final int REQUEST_PERMISSION_CAMERA = 0x537642;

    /**
     * 外部ストレージへの書き込みパーミッションが有るかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true 外部ストレージへの書き込みパーミッションが有る
     */
    protected boolean checkPermissionWriteExternalStorage() {
        if (!PermissionCheck.hasWriteExternalStorage(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                    R.string.permission_title, R.string.permission_ext_storage_request,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return false;
        }
        return true;
    }

    /**
     * 録音のパーミッションが有るかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true 録音のパーミッションが有る
     */
    protected boolean checkPermissionAudio() {
        if (!PermissionCheck.hasAudio(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_AUDIO_RECORDING,
                    R.string.permission_title, R.string.permission_audio_recording_request,
                    new String[]{Manifest.permission.RECORD_AUDIO});
            return false;
        }
        return true;
    }

    /**
     * ネットワークアクセスのパーミッションが有るかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true ネットワークアクセスのパーミッションが有る
     */
    protected boolean checkPermissionNetwork() {
        if (!PermissionCheck.hasNetwork(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_NETWORK,
                    R.string.permission_title, R.string.permission_network_request,
                    new String[]{Manifest.permission.INTERNET});
            return false;
        }
        return true;
    }

    /**
     * カメラアクセスのパーミッションがあるかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true カメラアクセスのパーミッションが有る
     */
    protected boolean checkPermissionCamera() {
        if (!PermissionCheck.hasCamera(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_CAMERA,
                    R.string.permission_title, R.string.permission_camera_request,
                    new String[]{Manifest.permission.CAMERA});
            return false;
        }
        return true;
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return usbMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {

    }

    public void onTask(View view) {
        if(mMySocket != null && mMySocket.isOpen()){
            mMySocket.send("");
        }
    }
}
