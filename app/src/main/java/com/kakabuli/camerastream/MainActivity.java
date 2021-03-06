package com.kakabuli.camerastream;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.faucamp.simplertmp.RtmpHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.aiui.AIUISetting;
import com.kakabuli.bean.DownFileBean;
import com.kakabuli.camerastream.socket.result.VideoPlayResult;
import com.kakabuli.camerastream.utils.DataConversion;
import com.kakabuli.camerastream.utils.DeviceUtils;
import com.kakabuli.camerastream.utils.FucUtil;
import com.kakabuli.voice.VoiceService;
import com.kakabuli.voice.aiui.AiuiServiceManager;
import com.kakabuli.voice.caePk.CaeOperator;
import com.kakabuli.voice.caePk.OnCaeOperatorListener;
import com.kakabuli.voice.caePk.adapter.ChatAdapter;
import com.kakabuli.voice.caePk.bean.ChatBean;
import com.kakabuli.voice.caePk.bean.FmListBean;
import com.kakabuli.voice.caePk.bean.FmTypeBean;
import com.kakabuli.voice.caePk.bean.IatBean;
import com.kakabuli.voice.caePk.bean.ItemChatType;
import com.kakabuli.voice.caePk.util.AESUtil;
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
import com.zdx.serialportlibrary.Device;
import com.zdx.serialportlibrary.SerialPortFinder;
import com.zdx.serialportlibrary.SerialPortManager;
import com.zdx.serialportlibrary.listener.OnOpenSerialPortListener;
import com.zdx.serialportlibrary.listener.OnSerialPortDataListener;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsRecordHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/** ???UVC Camera ????????????????????????
 *
 */
public class MainActivity extends Activity implements MessageDialogFragment.MessageDialogListener, CameraDialog.CameraDialogParent {
    private UVCCameraView uvcCameraView;
    private UVCCameraView uvcCameraView1;
    private Button btn_start;
    private RecyclerView recyclerView ;
    private ArrayList<ChatBean> listChat = new ArrayList<>();
    private ChatAdapter adapterChat ;

    //USB????????????????????????
    private USBMonitor usbMonitor;
    private UVCCamera uvcCameraFirst;
    private UVCCamera uvcCameraSecond;
    private UVCPublisher uvcPublisher;
    private UVCPublisher uvcPublisher1;
    //?????????????????????rtmp??????
    private static String RTMP_URL_01 = "rtmp://118.190.36.40/devices/13723789649";//rtmp://118.190.36.40/devices/13723789649
//    private static String RTMP_URL_01 = "rtmp://192.168.1.100:1935/myapp/camera01";
    private static String RTMP_URL_02 = "";//rtmp://192.168.1.100:1935/myapp/camera02
    //http ?????????????????????token
    private String token = "";
    //????????????websocket???????????????VIDEO_PLAY???????????????rtmp??????
    private MySocket mMySocket;
    //??????usb??????????????????????????????socket???????????????UVCCamera
    private List<UsbDevice> mAttachDevice;

    // ??????USB??????
    private SerialPortManager mUSB0SerialPortManager, mUSB1SerialPortManager, mUSB2SerialPortManager;

    private final String CAMERA_TAG = "Camera";


    // ??????appId??????????????????
    private static final String yourAppId = "r6eyp1op1kau";
    // ??????????????????????????????
    private static final String yourKey = "3A47E29D4E60DEFABC6C48D3618B7804";   //key????????????

    private static final String id = "123456789";
    private List<FmListBean.Info> listBeans = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();;
    private int index = 0;

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
        initVideo();
        createAgent();
        getFM();
        //startService(new Intent(this, VoiceService.class));

    }

    @Override
    protected void onPause() {
        super.onPause();
        uvcPublisher.pauseRecord();
        uvcPublisher1.pauseRecord();
        pauseVideo();
    }

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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if(!TextUtils.isEmpty(MyApplication.getInstance().getToken()) || !TextUtils.isEmpty(MMKVUtils.getStringMMKV(Constants.DEVICE_TOKEN))){
                if(TextUtils.isEmpty(MyApplication.getInstance().getToken())){
                    initSocket(MMKVUtils.getStringMMKV(Constants.DEVICE_TOKEN));
                }else{
                    initSocket(MyApplication.getInstance().getToken());
                }
            }
        },1500);
        resumeVideo();

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
        closeAllUSBSerialPort();
        destroyAgent();
        uvcPublisher.stopPublish();
        uvcPublisher1.stopPublish();
        uvcPublisher1.stopRecord();
        uvcPublisher.stopRecord();
        usbMonitor.unregister();
        destroyVideo();
        destroySpeech();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void copyBigDataToSD(String strOutFileName) throws IOException {
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

    private void initSocket(String deviceToken) {
        LogUtils.dTag(CAMERA_TAG, "initSocket------------");
        mMySocket = new MySocket(URI.create(SocketConstants.SOCKET_URL + SocketConstants.LOGIN + deviceToken), new IRTMPListener() {
            @Override
            public void onSocketConnect(int code, String message) {

            }

            @Override
            public void onSocketMessage(String message) {
                BaseResult mBaseResult = new Gson().fromJson(message,
                        new TypeToken<BaseResult>() {}.getType());
                switch (mBaseResult.getType()){
                    case Constants.TASK_LOGIN://socket??????
                        break;
//                    case Constants.VIDEO_PLAY:
                    case Constants.TASK_CHECK_CALLBACK://TODO 2021-11-10 ??????????????????rtmp ??????????????????????????????socket???????????????????????????????????????
                        try{
                            VideoPlayResult mVideoPlayResult = new Gson().fromJson(message, new TypeToken<VideoPlayResult>() {}.getType());
                            LogUtils.dTag(CAMERA_TAG, "mVideoPlayResult--->" + mVideoPlayResult.toString());
                            LogUtils.dTag(CAMERA_TAG, "mVideoPlayResult.getData().getRtmpUrl()--->" + mVideoPlayResult.getData().getRtmpUrl());
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
        uvcCameraView = findViewById(R.id.uvcCameraView);
        uvcCameraView1 = findViewById(R.id.uvcCameraView1);
        btn_start = findViewById(R.id.btn_start);
        recyclerView = findViewById(R.id.chatRecycleView);
        adapterChat = new ChatAdapter(listChat);
        recyclerView.setAdapter(adapterChat);
    }

    public void updaterRecyclerViewShow(ItemChatType type, String string) {
        if (!TextUtils.isEmpty(string)) {
            listChat.add(new ChatBean(type, string));
            adapterChat.notifyItemInserted(listChat.size() - 1);
            if (listChat.size() > 6) {
                recyclerView.smoothScrollToPosition(listChat.size() - 1);
            }
        }
    }

    private void initData() {
        btn_start.setOnClickListener(mStopClickListener);

        mAttachDevice = new ArrayList<>();
        usbMonitor = new USBMonitor(this, mOnDeviceConnectListener);

        uvcPublisher1 = new UVCPublisher(uvcCameraView1);
        uvcPublisher1.setEncodeHandler(new SrsEncodeHandler(mSrsEncodeListener1));
        uvcPublisher1.setRtmpHandler(new RtmpHandler(mRtmpListener1),8);
        uvcPublisher1.setRecordHandler(new SrsRecordHandler(mSrsRecordListener1));
        uvcPublisher1.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher1.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        uvcPublisher1.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher1.setVideoHDMode();

        uvcPublisher = new UVCPublisher(uvcCameraView);
        uvcPublisher.setEncodeHandler(new SrsEncodeHandler(mSrsEncodeListener));
        uvcPublisher.setRtmpHandler(new RtmpHandler(mRtmpListener),9);
        uvcPublisher.setRecordHandler(new SrsRecordHandler(mSrsRecordListener));
        uvcPublisher.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        uvcPublisher.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher.setVideoHDMode();
    }

    /**
     *  ??????UVC Camera??????
     */
    private void requestUSBMonitor(){
        LogUtils.dTag(CAMERA_TAG,"+++requestUSBMonitor");
         if(usbMonitor.isRegistered()){
             if(mAttachDevice != null && mAttachDevice.size() > 0)
             for (UsbDevice device : mAttachDevice) {
                 LogUtils.dTag(CAMERA_TAG,usbMonitor.isRegistered() + " ");
                 String name = device.getConfiguration(0).getName();
                 if(!TextUtils.isEmpty(name) && !"null".equals(name))//??????USB??????????????????????????????????????????
                     usbMonitor.requestPermission(device);
             }
            }
    }

    private final View.OnClickListener mStopClickListener = new View.OnClickListener() {
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
    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice device) {
            LogUtils.dTag(CAMERA_TAG, "onAttach?????????");
            if(usbMonitor.isRegistered()){
                String name = device.getConfiguration(0).getName();
                if(!TextUtils.isEmpty(name) && !"null".equals(name))
                    mAttachDevice.add(device);
            }


            LogUtils.dTag(CAMERA_TAG, "mAttachDevice.size()-->" + mAttachDevice.size() + "\n mAttachDevice.toString-->" + mAttachDevice.toString());
        }

        @Override
        public void onDettach(UsbDevice device) {
            LogUtils.d(CAMERA_TAG, "onDettach????????????");
        }

        @Override
        public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
            LogUtils.d(CAMERA_TAG, "onConnect?????????");

            if(!uvcPublisher.isOpened() && !isFirst){
                uvcPublisher.stopCamera();
                LogUtils.d(CAMERA_TAG, "uvcPublisher stopCamera");
                uvcCameraFirst = uvcPublisher.startCamera(ctrlBlock);
                if (uvcCameraFirst == null) {
                    LogUtils.d(CAMERA_TAG, "?????????????????????, uvcCameraFirst == null");
                    return;
                }
                if(!TextUtils.isEmpty(RTMP_URL_01))
                    uvcPublisher.startPublish(RTMP_URL_01);
                isFirst = true;
            }else if(!uvcPublisher1.isOpened()){
                uvcPublisher1.stopCamera();
                uvcCameraSecond = uvcPublisher1.startCamera(ctrlBlock);
                if(uvcCameraSecond == null){
                    LogUtils.d(CAMERA_TAG, "?????????????????????, uvcCameraSecond == null");
                    return;
                }
                if(!TextUtils.isEmpty(RTMP_URL_02))
                    uvcPublisher1.startPublish(RTMP_URL_02);
            }
            LogUtils.d(CAMERA_TAG, "????????????????????????????????????");
        }

        @Override
        public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
            LogUtils.d(CAMERA_TAG, "onDisconnect????????????");
            if((uvcPublisher != null) && uvcPublisher.isEqual(device)){

                uvcPublisher.stopCamera();
            }

            if((uvcPublisher1 != null) && uvcPublisher1.isEqual(device)){

                uvcPublisher1.stopCamera();
            }
        }

        @Override
        public void onCancel(UsbDevice device) {
            LogUtils.d(CAMERA_TAG, "onCancel?????????");
        }
    };

    private final RtmpHandler.RtmpListener mRtmpListener = new RtmpHandler.RtmpListener() {
        @Override
        public void onRtmpConnecting(String msg) {
            LogUtils.dTag("shulan222","onRtmpConnecting-->" + msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            LogUtils.dTag("shulan222","onRtmpConnected-->" + msg);
        }

        @Override
        public void onRtmpVideoStreaming() {
            LogUtils.dTag("shulan222","onRtmpVideoStreaming-->");
        }

        @Override
        public void onRtmpAudioStreaming() {
            LogUtils.dTag("shulan222","onRtmpAudioStreaming-->");
        }

        @Override
        public void onRtmpStopped() {
            LogUtils.dTag("shulan222","onRtmpStopped-->");
        }

        @Override
        public void onRtmpDisconnected() {
            LogUtils.dTag("shulan222","onRtmpDisconnected-->");
        }

        @Override
        public void onRtmpVideoFpsChanged(double fps) {
            LogUtils.dTag("shulan222","onRtmpVideoFpsChanged-->" + fps);
        }

        @Override
        public void onRtmpVideoBitrateChanged(double bitrate) {
            LogUtils.dTag("shulan222","onRtmpVideoBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpAudioBitrateChanged(double bitrate) {
            LogUtils.dTag("shulan222","onRtmpAudioBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpSocketException(SocketException e) {
            LogUtils.dTag("shulan222","onRtmpSocketException-->" + e.toString());
        }

        @Override
        public void onRtmpIOException(IOException e) {
            LogUtils.dTag("shulan222","onRtmpIOException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
            LogUtils.dTag("shulan222","onRtmpIllegalArgumentException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalStateException(IllegalStateException e) {
            LogUtils.dTag("shulan222","onRtmpIllegalStateException-->" + e.toString());
        }
    };

    private final RtmpHandler.RtmpListener mRtmpListener1 = new RtmpHandler.RtmpListener() {
        @Override
        public void onRtmpConnecting(String msg) {
            LogUtils.dTag("shulan333", "onRtmpConnecting-->" + msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            LogUtils.dTag("shulan333", "onRtmpConnected-->" + msg);
        }

        @Override
        public void onRtmpVideoStreaming() {
            LogUtils.dTag("shulan333", "onRtmpVideoStreaming-->");
        }

        @Override
        public void onRtmpAudioStreaming() {
            LogUtils.dTag("shulan333", "onRtmpAudioStreaming-->");
        }

        @Override
        public void onRtmpStopped() {
            LogUtils.dTag("shulan333", "onRtmpStopped-->");
        }

        @Override
        public void onRtmpDisconnected() {
            LogUtils.dTag("shulan333", "onRtmpDisconnected-->");
        }

        @Override
        public void onRtmpVideoFpsChanged(double fps) {
            LogUtils.dTag("shulan333", "onRtmpVideoFpsChanged-->" + fps);
        }

        @Override
        public void onRtmpVideoBitrateChanged(double bitrate) {
            LogUtils.dTag("shulan333", "onRtmpVideoBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpAudioBitrateChanged(double bitrate) {
            LogUtils.dTag("shulan333", "onRtmpAudioBitrateChanged-->" + bitrate);
        }

        @Override
        public void onRtmpSocketException(SocketException e) {
            LogUtils.dTag("shulan333", "onRtmpSocketException-->" + e.toString());
        }

        @Override
        public void onRtmpIOException(IOException e) {
            LogUtils.dTag("shulan333", "onRtmpIOException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
            LogUtils.dTag("shulan333", "onRtmpIllegalArgumentException-->" + e.toString());
        }

        @Override
        public void onRtmpIllegalStateException(IllegalStateException e) {
            LogUtils.dTag("shulan333", "onRtmpIllegalStateException-->" + e.toString());
        }


    };

    private final SrsRecordHandler.SrsRecordListener mSrsRecordListener = new SrsRecordHandler.SrsRecordListener() {
        @Override
        public void onRecordPause() {
            LogUtils.dTag("shulan222","onRecordPause");
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

    private final SrsRecordHandler.SrsRecordListener mSrsRecordListener1 = new SrsRecordHandler.SrsRecordListener() {
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

    private final SrsEncodeHandler.SrsEncodeListener mSrsEncodeListener1 = new SrsEncodeHandler.SrsEncodeListener() {
        @Override
        public void onNetworkWeak() {

        }

        @Override
        public void onNetworkResume() {

        }

        @Override
        public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
            LogUtils.e(e.toString());
        }
    };

    private final SrsEncodeHandler.SrsEncodeListener mSrsEncodeListener = new SrsEncodeHandler.SrsEncodeListener() {
        @Override
        public void onNetworkWeak() {

        }

        @Override
        public void onNetworkResume() {

        }

        @Override
        public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
            LogUtils.e(e.toString());
        }
    };

    private void getUserToken() {
        NewServiceImp.login(/*"13723789649","123456"*/"admin","admin").subscribe(new Observer<LoginResult>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                LogUtils.dTag("shulan_http","onSubscribe");
            }

            @Override
            public void onNext(@NonNull LoginResult loginResult) {
                LogUtils.dTag("shulan_http","onNext-->" + loginResult.toString());

                if(loginResult.getMeta().getCode() == 200 && loginResult.getMeta().isSuccess()) {
                    if(!TextUtils.isEmpty(loginResult.getData().getToken())){
                        token =  loginResult.getData().getToken();
                        MMKVUtils.setMMKV(Constants.DEVICE_TOKEN,loginResult.getData().getToken());
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtils.dTag("shulan_http","onError");
            }

            @Override
            public void onComplete() {
                LogUtils.dTag("shulan_http","onComplete");
                if(!TextUtils.isEmpty(token))
                    initSocket(token);
            }
        });
    }

    //================================================================================

    /**
     * MessageDialogFragment?????????????????????????????????????????????????????????????????????
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
            // ?????????????????????????????????OK??????????????????????????????????????????????????????
            if (BuildCheck.isMarshmallow()) {
                requestPermissions(permissions, requestCode);
                return;
            }
        }
        // ???????????????????????????????????????????????????????????????Android6??????????????????????????????????????????#checkPermissionResult???????????????
        for (final String permission : permissions) {
            checkPermissionResult(requestCode, permission, PermissionCheck.hasPermission(this, permission));
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);    // ??????????????????????????????????????????
        final int n = Math.min(permissions.length, grantResults.length);
        for (int i = 0; i < n; i++) {
            checkPermissionResult(requestCode, permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * ???????????????????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????Toast????????????????????????????????????
     *
     * @param requestCode
     * @param permission
     * @param result
     */
    protected void checkPermissionResult(final int requestCode, final String permission, final boolean result) {
        // ????????????????????????????????????????????????????????????????????????
        if (!result && (permission != null)) {
            if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                runOnUiThread(() -> ToastUtils.showLong(R.string.permission_audio));
            }
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                runOnUiThread(() -> ToastUtils.showLong(R.string.permission_ext_storage));
            }
            if (Manifest.permission.INTERNET.equals(permission)) {
                runOnUiThread(() -> ToastUtils.showLong(R.string.permission_network));
            }
        }
    }

    // ??????????????????????????????????????????????????????
    protected static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x12345;
    protected static final int REQUEST_PERMISSION_AUDIO_RECORDING = 0x234567;
    protected static final int REQUEST_PERMISSION_NETWORK = 0x345678;
    protected static final int REQUEST_PERMISSION_CAMERA = 0x537642;

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????
     *
     * @return true ?????????????????????????????????????????????????????????????????????
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
     * ??????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????
     *
     * @return true ???????????????????????????????????????
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
     * ??????????????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????
     *
     * @return true ???????????????????????????????????????????????????????????????
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
     * ?????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????????????????
     *
     * @return true ??????????????????????????????????????????????????????
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

    /*------------------------------------- USB????????????start -------------------------------------*/

    private void initOpenUSBSerialPort() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();

        ArrayList<Device> devices = serialPortFinder.getDevices();

        mUSB0SerialPortManager = new SerialPortManager();
        mUSB1SerialPortManager = new SerialPortManager();
        mUSB2SerialPortManager = new SerialPortManager();

        Device deviceUSB0 = null, deviceUSB1 = null, deviceUSB2 = null;
        for (Device device : devices) {
            switch (device.getName()) {
                case "ttyUSB0":
                    deviceUSB0 = device;
                    break;
                case "ttyUSB1":
                    deviceUSB1 = device;
                    break;
                case "ttyUSB2":
                    deviceUSB2 = device;
                    break;
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
                    LogUtils.dTag("USB", "USB0: " + DataConversion.encodeHexString(bytes));
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
                    LogUtils.dTag("USB", "USB1: " + DataConversion.encodeHexString(bytes));
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
                    LogUtils.dTag("USB", "USB2: " + DataConversion.encodeHexString(bytes));
                }

                @Override
                public void onDataSent(byte[] bytes) {

                }
            }).openSerialPort(deviceUSB2.getFile(), 115200);
        }
    }

    private void closeAllUSBSerialPort() {
        if (mUSB0SerialPortManager != null) mUSB0SerialPortManager.closeSerialPort();
        if (mUSB1SerialPortManager != null) mUSB1SerialPortManager.closeSerialPort();
        if (mUSB2SerialPortManager != null) mUSB2SerialPortManager.closeSerialPort();
    }

    /*------------------------------------- USB????????????end -------------------------------------*/

    /*-------------------------------- ??????????????????start ---------------------------------------*/

    String mPlayingPath;
    private VideoView mVideoView;
    private TextView mTv;
    int mStopPosition = 0;
    private String mPlayName = null;

    private void getDownloadVideo(){
        File file = new File(MyApplication.getInstance().getPATH());
        LogUtils.d("getDownloadVideo: " + file.exists());
        if(file.exists()){
            File[] fileArray = file.listFiles();
            if(fileArray != null && fileArray.length > 0){
                File fileName = fileArray[0];
                mPlayName = fileName.getName();
                LogUtils.d("playName: " + mPlayName);
                LogUtils.d("playName substring: " + mPlayName.substring(mPlayName.length() - 3));
                if(!mPlayName.substring(mPlayName.length() - 3).equals("mp4")){
                    LogUtils.d("????????????????????? = " + new File(fileName.getPath()).delete());
                }
                if(fileName.exists() && mPlayName.endsWith("mp4")){
                    startVideo(fileName.getPath());
                    mPlayingPath = fileName.getPath();
                }
                LogUtils.d("getDownloadVideo: " + fileName.getPath());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownFileBean(DownFileBean downFileBean) {
        Log.d("zdx", "onDownFileBean: " + downFileBean.toString());
        if(downFileBean.getCode() == 404){
            mTv.setText(" ???????????? ???"+ downFileBean.getMsg());
        }else if(downFileBean.getCode() == 100){
            mTv.setText("????????????"+ downFileBean.getMsg() + "%");
        }else if(downFileBean.getCode() == 200 && !TextUtils.isEmpty(downFileBean.getMsg())){
            File file = new File(MyApplication.getInstance().getPATH());
            if(file.exists()){
                File[] fileArray = file.listFiles();
                if(fileArray != null && fileArray.length == 1){
                    if(!mVideoView.isPlaying()){
                        startVideo(downFileBean.getMsg());
                        mPlayingPath = downFileBean.getMsg();
                    }
                } else if(fileArray != null && fileArray.length > 1 && mVideoView.isPlaying()){
                    startVideo(downFileBean.getMsg());
                    boolean isDelete = new File(mPlayingPath).delete();
                    LogUtils.d("onDownFileBean: " + isDelete);
                    if(isDelete){
                        mPlayingPath = downFileBean.getMsg();
                    }
                }
            }
        }
    }

    private void startVideo(String path){
        mVideoView.setVisibility(View.VISIBLE);
        mTv.setVisibility(View.GONE);
        mVideoView.resume();
        mVideoView.setVideoPath(path);
        //??????MediaController??????
        MediaController mediaController = new MediaController(this);

        //VideoView???MediaController????????????
        mediaController.setVisibility(View.GONE);
        mVideoView.setMediaController(mediaController);

        //???VideoView????????????
        mVideoView.requestFocus();
        mVideoView.start();
        mVideoView.setOnCompletionListener(mPlayer -> {
            mPlayer.start();
            mPlayer.setLooping(true);
        });
    }

    private void initVideo() {
        EventBus.getDefault().register(this);
        mVideoView = findViewById(R.id.videoView);
        mTv = findViewById(R.id.tv);
        getDownloadVideo();
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            Intent intent = new Intent(MainActivity.this, WorkService.class);
//            intent.putExtra("playName", mPlayName);
//            startService(intent);
//        }, 1800);
    }

    private void resumeVideo() {
        if(mVideoView != null && mVideoView.canPause()){
            mVideoView.seekTo(mStopPosition);
            mVideoView.start();
        }
    }

    private void pauseVideo() {
        if(mVideoView.isPlaying()){
            mStopPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }
    }

    private void destroyVideo() {
        EventBus.getDefault().unregister(this);
        if(mVideoView != null){
            mVideoView.resume();
        }
    }

    /*-------------------------------- ??????????????????end ---------------------------------------*/


    /*---------------------------------- ??????????????????start ---------------------------------------*/


    final private String SpeechTAG = "??????";

    private int mAIUIState = AIUIConstant.STATE_IDLE;
    private boolean mIsWakeupEnable = false;
    private boolean isAsring = true;
    private boolean playTTSByApp = true;

    // ??????????????????
    private CaeOperator caeOperator = null;

    private StringBuilder stringBuilder = new StringBuilder();

    private AIUIAgent mAIUIAgent;

    private int mAngle = 0;
    private int mBeam = 0;


    final private AIUIListener mAIUIListener = event -> {
//            LogUtils.iTag( SpeechTAG,  "on event: " + event.eventType );
        switch (event.eventType) {
            //????????????
            case AIUIConstant.EVENT_SLEEP: {
                isAsring = false;
                LogUtils.dTag(SpeechTAG, "????????????");
                updateChatShow(ItemChatType.Robot, "????????????");
                resumeVideo();

                break;
            }
            case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
                LogUtils.dTag(SpeechTAG,"??????????????????");
                initCaeEngine();
                startWakeUp();
                break;

            case AIUIConstant.EVENT_SERVER_DISCONNECTED:
                LogUtils.dTag(SpeechTAG,"??????????????????");
                break;

            case AIUIConstant.EVENT_WAKEUP:
                LogUtils.dTag(SpeechTAG,"??????????????????" );
                // TODO: 2021/12/22 ????????????????????????????????????
                //playTts(getString(R.string.wake_success_tip));
                updateChatShow(ItemChatType.Robot,getString(R.string.wake_success_tip));
                break;

            case AIUIConstant.EVENT_RESULT: {
                try {
                    JSONObject bizParamJson = new JSONObject(event.info);
                    JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                    JSONObject params = data.getJSONObject("params");
                    JSONObject content = data.getJSONArray("content").getJSONObject(0);
                    if (content.has("cnt_id")) {
                        String cntId = content.getString("cnt_id");
                        String cntStr = new String(event.data.getByteArray(cntId), StandardCharsets.UTF_8);

                        if (TextUtils.isEmpty(cntStr)) {
                            LogUtils.dTag(SpeechTAG, "onEvent: cntStr is null");
                            return;
                        }
                        JSONObject cntJson = new JSONObject(cntStr);
                        String sub = params.optString("sub");
                        String result = cntJson.optString("intent");
                        if ("nlp".equals(sub)) {
                            // ????????????????????????
                            LogUtils.eTag(SpeechTAG, "nlp : " + result);
                            if (!"{}".equals(result)) {
                                String parseResult =
                                        AiuiServiceManager.getInstance()
                                                .parseResult(result);
                                if (!parseResult.equals("")) {
                                    updateChatShow(ItemChatType.Robot, parseResult);
                                    if (playTTSByApp) {
                                        playTts(parseResult);
                                    }
                                }
                            }

                        } else if ("iat".equals(sub)) {
                            LogUtils.eTag(SpeechTAG, "iat : " + cntStr);
                            IatBean iatBean = JSON.parseObject(cntStr, IatBean.class);
                            List<IatBean.TextBean.WsBean> wsBeanList = iatBean.getText().getWs();
                            StringBuilder stringBuilderTemp = new StringBuilder();
                            for (IatBean.TextBean.WsBean wsBean : wsBeanList) {
                                IatBean.TextBean.WsBean.CwBean cwBean = wsBean.getCw().get(0);
                                stringBuilderTemp.append(cwBean.getW());
                            }

                            if (stringBuilderTemp.length() > 0) {
                                stringBuilder.delete(0, stringBuilder.length());
                                stringBuilder.append(stringBuilderTemp);
                            }
                            if (iatBean.getText().isLs()) {
                                String toString = stringBuilder.toString();
                                if (!TextUtils.isEmpty(toString) && !toString.trim().equals(".")) {
                                    updateChatShow(
                                            ItemChatType.People,
                                            stringBuilder.toString()
                                    );
                                }
                                stringBuilder.delete(0, stringBuilder.length());
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
//                        mNlpText.append( "\n" );
//                        mNlpText.append( e.getLocalizedMessage());
                }

            } break;

            case AIUIConstant.EVENT_ERROR: {
                LogUtils.eTag(SpeechTAG, "?????????" + event.arg1 + "\n" + event.info);
            } break;

            case AIUIConstant.EVENT_VAD: {
                if (AIUIConstant.VAD_BOS == event.arg1) {
                    LogUtils.dTag(SpeechTAG, "??????vad_bos");
                } else if (AIUIConstant.VAD_EOS == event.arg1) {
                    LogUtils.dTag(SpeechTAG, "??????vad_eos");
                } else {
//                        LogUtils.dTag(SpeechTAG, "" + event.arg2);
                }
            } break;

            case AIUIConstant.EVENT_START_RECORD: {
                LogUtils.dTag(SpeechTAG, "???????????????");
            } break;

            case AIUIConstant.EVENT_STOP_RECORD: {
                LogUtils.dTag(SpeechTAG, "???????????????");
            } break;

            case AIUIConstant.EVENT_STATE: {	// ????????????
                mAIUIState = event.arg1;

                if (AIUIConstant.STATE_IDLE == mAIUIState) {
                    // ???????????????AIUI?????????
                    LogUtils.dTag(SpeechTAG, "STATE_IDLE");
                } else if (AIUIConstant.STATE_READY == mAIUIState) {
                    // AIUI????????????????????????
                    LogUtils.dTag(SpeechTAG, "STATE_READY");
                } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                    // AIUI???????????????????????????
                    LogUtils.dTag(SpeechTAG, "STATE_WORKING");
                } else {
                    LogUtils.dTag(SpeechTAG, "STATE_IDLE = " + mAIUIState);
                }
            } break;

            case AIUIConstant.EVENT_CMD_RETURN: {
                LogUtils.dTag(SpeechTAG, "STATE_IDLE EVENT_CMD_RETURN");
            } break;
            default:
                break;
        }
    };

    private void createAgent() {
        if (null == mAIUIAgent) {
            LogUtils.iTag(SpeechTAG, "?????? aiui ??????");
            //???????????????????????????????????????SN?????????????????????????????????(mac???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            AIUISetting.setSystemInfo(AIUIConstant.KEY_SERIAL_NUM, DeviceUtils.getWifiMac(this));
            mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), mAIUIListener);
        }

        if (null == mAIUIAgent) {
            LogUtils.eTag(SpeechTAG, "??????AIUIAgent?????????");
        } else {
            LogUtils.dTag(SpeechTAG, "AIUIAgent?????????");
        }
    }

    private void destroyAgent() {
        if (null != mAIUIAgent) {
            LogUtils.iTag(SpeechTAG, "destroy aiui agent");

            mAIUIAgent.destroy();
            mAIUIAgent = null;

            LogUtils.dTag(SpeechTAG, "AIUIAgent?????????");
        } else {
            LogUtils.dTag(SpeechTAG, "AIUIAgent??????");
        }
    }

    private void startWakeUp() {
        if (null == mAIUIAgent) {
            LogUtils.eTag(SpeechTAG, "AIUIAgent?????????????????????");
            return;
        }
        // ????????????????????????????????????????????????
        // ????????????
        AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0 ,0, "data_type=audio,sample_rate=16000", null);
        mAIUIAgent.sendMessage(msg);
    }

    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);

            JSONObject paramsJson = new JSONObject(params);

            mIsWakeupEnable = !"off".equals(paramsJson.optJSONObject("speech").optString("wakeup_mode"));
            if(mIsWakeupEnable) {
                FucUtil.copyAssetFolder(this, "ivw", "/sdcard/AIUI/ivw");
            }

            params = paramsJson.toString();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    private void destroySpeech() {
        if (null != mAIUIAgent) {
            LogUtils.dTag(SpeechTAG, "?????? aiui agent");
            mAIUIAgent.destroy();
            mAIUIAgent = null;
        }
    }

    final private OnCaeOperatorListener onCaeOperatorListener = new OnCaeOperatorListener() {
        @Override
        public void onAudio(byte[] audioData, int dataLen) {
            if (isAsring && mAIUIState == AIUIConstant.STATE_WORKING) {
                String params = "data_type=audio,sample_rate=16000";
                AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, audioData);
                mAIUIAgent.sendMessage(msg);
            } else {
                LogUtils.eTag(SpeechTAG,"?????????????????? mAIUIState ="+mAIUIState+"  isAsring??? "+isAsring);
            }
        }

        @Override
        public void onWakeup(int angle, int beam) {
            LogUtils.eTag(SpeechTAG, "onWakeup: " + angle + "   " + beam);
            AIUIMessage resetWakeupMsg = new AIUIMessage(
                    AIUIConstant.CMD_WAKEUP, angle, beam, "", null
            );
            mAngle = angle;
            mBeam = beam;
            mAIUIAgent.sendMessage(resetWakeupMsg);
            onWakeupHandler(angle, beam);
        }

        @Override
        public void onError(int errorCode, String msg) {

        }
    };

    private void initCaeEngine() {
        if (null == caeOperator) {
            caeOperator = CaeOperator.getInstance();
        } else {
            LogUtils.dTag(SpeechTAG, "initCaeEngine is Init Done!");
        }
        caeOperator.setCaeOperatorListener(onCaeOperatorListener);
    }

    protected void playTts(String text) {
        byte[] ttsData = text.getBytes(); //?????????????????????
        StringBuffer params = new StringBuffer(); //??????????????????
        params.append("vcn=x2_xiaojuan");//???????????????
        params.append(",speed=50");//????????????
        params.append(",pitch=50");//????????????
        params.append(",volume=50");//????????????

        Executors.newCachedThreadPool().execute(() -> {
            AIUIMessage startTts =
                    new AIUIMessage(AIUIConstant.CMD_TTS, AIUIConstant.START, 0, params.toString(), ttsData);
            if (mAIUIAgent != null) {
                mAIUIAgent.sendMessage(startTts);
            }
        });
    }

    private void updateChatShow(ItemChatType type, String content) {
        // TODO: 2021/12/22  ??????UI????????????
        updaterRecyclerViewShow(type,content);
        LogUtils.dTag(SpeechTAG,"type = " + type + " content: " + content);
        if (!TextUtils.isEmpty(content)) {
            Log.d("zdx", "updateChatShow: " + content);
            if(TextUtils.equals(content,"???????????????") || TextUtils.equals(content,"????????????")
                    || TextUtils.equals(content,"??????FM")){
                if(listBeans.size() > 0) {
                    String uri = listBeans.get(index).getUrl();
                    if (uri != null) {
                        Uri uri1 = Uri.parse(uri);
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(this, uri1);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                }
            }else if(TextUtils.equals(content,"?????????????????????") || TextUtils.equals(content,"???????????????")){
                if(listBeans.size() > 0 && index <= listBeans.size()) {
                    index = index + 1;
                    String uri = listBeans.get(index).getUrl();
                    if (uri != null) {
                        Uri uri1 = Uri.parse(uri);
                        try {
                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.reset();
                            }
                            mediaPlayer.setDataSource(this, uri1);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                }
            }else if(TextUtils.equals(content,"?????????????????????") || TextUtils.equals(content,"???????????????")){
                if(listBeans.size() > 0 && index > 0) {
                    index = index - 1;
                    String uri = listBeans.get(index).getUrl();
                    if (uri != null) {
                        Uri uri1 = Uri.parse(uri);
                        try {
                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.reset();
                            }
                            mediaPlayer.setDataSource(this, uri1);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                }
            }else if(TextUtils.equals(content,"???????????????") || TextUtils.equals(content,"????????????")
                    || TextUtils.equals(content,"??????FM")){
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
            }
        }
    }

    private void stopTTS() {
        AIUIMessage resetWakeupMsg = new AIUIMessage(
                AIUIConstant.CMD_TTS, AIUIConstant.CANCEL, 0, "", null
        );
        if (mAIUIAgent != null) {
            mAIUIAgent.sendMessage(resetWakeupMsg);
        }
    }

    protected void onWakeupHandler(int angle, int beam) {
        stopTTS();
        AiuiServiceManager.getInstance().handlerWake();
    }


    /*---------------------------------- ??????????????????end ---------------------------------------*/

    /*---------------------------------- ????????????FM??????start ---------------------------------------*/
    private void getFM(){
        // step1 : ?????????????????? json
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("devId", id);

        // step2 :?????? aes ??????????????????
        String urlData = getUrlData(stringObjectHashMap);
        String url = "https://test-wbd.kuwo.cn/api/bd/book/news/category?" + urlData;
        // step3: ????????????
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String data = response.body().string();
                    Log.d("zdx", "decryptToAES: " + data);
                    StringBuilder str = new StringBuilder();
                    str.append(data);
                    try {
                        String url = URLDecoder.decode(str.toString(), "utf-8");
                        Log.d("zdx", "url: " +url);
                        String res = AESUtil.decryptByAES(url,yourKey);
                        JSONObject josnStr = new JSONObject(res);
                        String s = josnStr.getString("data");
                        FmTypeBean fmTypeBean = new Gson().fromJson(s, FmTypeBean.class);
                        for(int i = 0; i < fmTypeBean.getList().size() ; i ++){
                            String name = fmTypeBean.getList().get(i).getName();
                            if(!TextUtils.isEmpty(name)){
                                if(TextUtils.equals(name,"??????")){
                                    getCategoryNews(fmTypeBean.getList().get(i).getCategoryId());
                                }
                                else if(TextUtils.equals(name,"??????")){
                                    getCategoryNews(fmTypeBean.getList().get(i).getCategoryId());
                                }
                                else if(TextUtils.equals(name,"??????")){
                                    getCategoryNews(fmTypeBean.getList().get(i).getCategoryId());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("false");
                }
            }
        });
    }

    //??????id??????
    private void getCategoryNews(int categoryId){
        // step1 : ?????????????????? json
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("devId", id);
        stringObjectHashMap.put("categoryId", categoryId);
        // step2 :?????? aes ??????????????????
        String urlData = getUrlData(stringObjectHashMap);
        String url = "https://test-wbd.kuwo.cn/api/bd/book/news/categoryNews?" + urlData;
        // step3: ????????????
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        String data = response.body().string();
                        Log.d("zdx", "decryptToAES: " + data);
                        StringBuilder str = new StringBuilder();
                        str.append(data);
                        try {
                            String url = URLDecoder.decode(str.toString(), "utf-8");
                            String res = AESUtil.decryptByAES(url,yourKey);
                            JSONObject josnStr = new JSONObject(res);
                            String s = josnStr.getString("data");
                            FmListBean fmListBean = new Gson().fromJson(s, FmListBean.class);
                            for(int i = 0; i < fmListBean.getList().size() ; i ++){
                                listBeans.add(fmListBean.getList().get(i));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        System.out.println("false");
                    }
                }
            }
        });
    }

    /**
     * ??????
     */
    private String getUrlData(Map<String, Object> map) {
        String jsonStr = new Gson().toJson(map);
        String aes = AESUtil.encryptToAES(jsonStr, yourKey);
        String time = String.valueOf(System.currentTimeMillis());
        try {
            String data = URLEncoder.encode(aes, "utf-8");
            String sign = AESUtil.encryptToMD5(yourAppId + aes + time);
            return String.format("data=%s&sign=%s&appId=%s&time=%s", data, sign, yourAppId, time);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*---------------------------------- ????????????FM??????end ---------------------------------------*/

}
