package com.kakabuli.voice;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
//import com.iflytek.aiui.AIUIAgent;
//import com.iflytek.aiui.AIUIConstant;
//import com.iflytek.aiui.AIUIMessage;
import com.kakabuli.camerastream.R;
import com.kakabuli.voice.aiui.AiuiServiceManager;
import com.kakabuli.voice.aiui.AiuiUtil;
import com.kakabuli.voice.caePk.CaeOperator;
import com.kakabuli.voice.caePk.OnCaeOperatorListener;
import com.kakabuli.voice.caePk.bean.ChatBean;
import com.kakabuli.voice.caePk.bean.FmListBean;
import com.kakabuli.voice.caePk.bean.FmTypeBean;
import com.kakabuli.voice.caePk.bean.IatBean;
import com.kakabuli.voice.caePk.bean.ItemChatType;
import com.kakabuli.voice.caePk.util.AESUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VoiceService extends Service {

    private final String TAG = "VoiceService";
//    private AIUIAgent mAIUIAgent = null;
    private boolean isAsring = true;
//    private int mAIUIState = AIUIConstant.STATE_IDLE;
    private boolean playTTSByApp = true;
    private int wakeCount = 0 ;
    // 你的appId，由酷我颁发
    private static final String yourAppId = "r6eyp1op1kau";
    // 你的密钥，由酷我颁发
    private static final String yourKey = "3A47E29D4E60DEFABC6C48D3618B7804";   //key改成这个

    private static final String id = "123456789";
    private List<FmListBean.Info> listBeans = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();;
    private StringBuilder stringBuilder = new StringBuilder();
    private int index = 0;

    // 多麦克算法库
    private CaeOperator caeOperator = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        initAIUIService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

//    private void initAIUIService() {
//        AIUIAgent.setSystemInfo(AIUIConstant.KEY_SERIAL_NUM, Build.SERIAL);
//        mAIUIAgent = AIUIAgent.createAgent(
//                this, AiuiUtil.getAIUIParams(this), event ->
//                {
//                    switch (event.eventType) {
//                        case AIUIConstant.EVENT_CONNECTED_TO_SERVER: {
//                            Log.d(TAG, "initAIUIService: 连接服务器成功 。。。 ");
//                            Toast.makeText(this,getString(R.string.connect_aiui_server),Toast.LENGTH_LONG).show();
//                            initCaeEngine();
//                            startAIUIService();
//                            getFM();
//                        }
//                        break;
//                        case AIUIConstant.EVENT_SERVER_DISCONNECTED: {
//                            Log.d(TAG, "与服务器断连");
//                            Toast.makeText(this,getString(R.string.disConnect_aiui_server),Toast.LENGTH_LONG).show();
//                        }
//                        break;
//                        case AIUIConstant.EVENT_WAKEUP: {
//                            isAsring = true;
//                            Log.d(TAG, "EVENT_WAKEUP: 进入识别状态  angle = $mAngle,  beam = $mBeam");
//                            playTts(getString(R.string.wake_success_tip));
//                            updateChatShow(ItemChatType.Robot,getString(R.string.wake_success_tip));
//                        }
//                        break;
//                        case AIUIConstant.EVENT_TTS: {
//                        }
//                        break;
//                        case AIUIConstant.EVENT_RESULT: {
//                            try {
//                                JSONObject bizParamJson = new JSONObject(event.info);
//                                JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
//                                JSONObject params = data.getJSONObject("params");
//                                JSONObject content = data.getJSONArray("content").getJSONObject(0);
//                                if (content.has("cnt_id")) {
//                                    String cntId = content.getString("cnt_id");
//                                    String cntStr = new String(event.data.getByteArray(cntId), Charset.forName("utf-8"));
//
//                                    if (TextUtils.isEmpty(cntStr)) {
//                                        Log.d(TAG, "onEvent:  cntStr is null ");
//                                        return;
//                                    }
//                                    JSONObject cntJson = new JSONObject(cntStr);
//                                    String sub = params.optString("sub");
//                                    String result = cntJson.optString("intent");
//                                    if ("nlp".equals(sub)) {
//                                        // 解析得到语义结果
//                                        Log.e(TAG, "nlp : " + result);
//                                        if (!"{}".equals(result)) {
//                                            String parseResult =
//                                                    AiuiServiceManager.getInstance()
//                                                            .parseResult(result);
//                                            if (!parseResult.equals("")) {
//                                                updateChatShow(ItemChatType.Robot, parseResult);
//                                                if (playTTSByApp) {
//                                                    playTts(parseResult);
//                                                }
//                                            }
//                                        }
//
//                                    } else
//                                        if ("iat".equals(sub)) {
//                                        Log.e(TAG, "iat : " + cntStr);
//                                        IatBean iatBean = JSON.parseObject(cntStr, IatBean.class);
//                                        List<IatBean.TextBean.WsBean> wsBeanList = iatBean.getText().getWs();
//                                        StringBuilder stringBuilderTemp = new StringBuilder();
//                                        for (IatBean.TextBean.WsBean wsBean : wsBeanList) {
//                                            IatBean.TextBean.WsBean.CwBean cwBean = wsBean.getCw().get(0);
//                                            stringBuilderTemp.append(cwBean.getW());
//                                        }
//
//                                        if (stringBuilderTemp.length() > 0) {
//                                            stringBuilder.delete(0, stringBuilder.length());
//                                            stringBuilder.append(stringBuilderTemp);
//                                        }
//                                        if (iatBean.getText().isLs()) {
//                                            String toString = stringBuilder.toString();
//                                            if (!TextUtils.isEmpty(toString) && !toString.trim().equals(".")) {
//                                                updateChatShow(
//                                                        ItemChatType.People,
//                                                        stringBuilder.toString()
//                                                );
//                                            }
//                                            stringBuilder.delete(0, stringBuilder.length());
//                                        }
//                                    }
//                                }
//                            } catch (Throwable e) {
//                            }
//                        }
//                        break;
//                        case AIUIConstant.EVENT_ERROR: {
//                            Log.e(TAG, "onEvent: " + "错误: " + event.arg1 + "\n" + event.info);
//                            //updateChatShow(ItemChatType.Robot, String.format("AIUI Error %s,%s", event.arg1, event.info));
//                        }
//                        break;
//                        case AIUIConstant.EVENT_SLEEP: {
//                            isAsring = false;
//                            Log.d(TAG, "EVENT_SLEEP");
//                            Toast.makeText(this,getString(R.string.sleep_wake),Toast.LENGTH_LONG).show();
//                            updateChatShow(ItemChatType.Robot, "进入休眠");
//                        }
//                        break;
//                        case AIUIConstant.EVENT_VAD: {
//                            if (AIUIConstant.VAD_BOS == event.arg1) {
//                                Log.d(TAG, "找到vad_bos");
//                            } else if (AIUIConstant.VAD_EOS == event.arg1) {
//                                Log.d(TAG, "找到vad_eos");
//                            } else {
//
//                            }
//                        }
//                        break;
//                        case AIUIConstant.EVENT_START_RECORD: {
//                            Log.d(TAG, "已开始录音");
//                        }
//                        break;
//                        case AIUIConstant.EVENT_STOP_RECORD: {
//                            Log.d(TAG, "已停止录音");
//                        }
//                        break;
//                        case AIUIConstant.EVENT_STATE: {
//                            // 状态事件
//                            mAIUIState = event.arg1;
//                            Log.d(TAG, "STATE_IDLE " + mAIUIState);
//                        }
//                        break;
//                        case AIUIConstant.EVENT_CMD_RETURN: {
//                            Log.d(TAG, "STATE_IDLE EVENT_CMD_RETURN");
//                        }
//                        break;
//                    }
//                });
//        if (mAIUIAgent == null) {
//            Log.e(TAG, "initAIUIService:  is null ");
//        }
//    }

//    private void startAIUIService() {
//        AIUIMessage startMsg = new AIUIMessage(
//                AIUIConstant.CMD_START, 0, 0, "", null
//        );
//        if (mAIUIAgent != null) {
//            mAIUIAgent.sendMessage(startMsg);
//        }
//    }

    private int mAngle = 0;
    private int mBeam = 0;
    private void initCaeEngine() {
        if (null == caeOperator) {
            caeOperator = CaeOperator.getInstance();
        } else {
            Log.d(TAG, "initCaeEngine is Init Done!");
        }
//        caeOperator.setCaeOperatorListener(onCaeOperatorListener);
    }

//    private OnCaeOperatorListener onCaeOperatorListener = new OnCaeOperatorListener() {
//        @Override
//        public void onAudio(byte[] audioData, int dataLen) {
//            if (isAsring && mAIUIState == AIUIConstant.STATE_WORKING) {
//                String params = "data_type=audio,sample_rate=16000";
//                AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, audioData);
//                mAIUIAgent.sendMessage(msg);
//            } else {
//                Log.e(TAG,"未送入音频： mAIUIState ="+mAIUIState+"  isAsring： "+isAsring);
//            }
//        }
//
//        @Override
//        public void onWakeup(int angle, int beam) {
//            Log.e(TAG, "onWakeup: " + angle + "   " + beam);
//            AIUIMessage resetWakeupMsg = new AIUIMessage(
//                    AIUIConstant.CMD_WAKEUP, angle, beam, "", null
//            );
//            mAngle = angle;
//            mBeam = beam;
//            mAIUIAgent.sendMessage(resetWakeupMsg);
//            onWakeupHandler(angle, beam);
//        }
//
//        @Override
//        public void onError(int errorCode, String msg) {
//
//        }
//    };
//
//    protected void onWakeupHandler(int angle, int beam) {
//        stopTTS();
//        AiuiServiceManager.getInstance().handlerWake();
//    }
//
//    private void stopTTS() {
//        AIUIMessage resetWakeupMsg = new AIUIMessage(
//                AIUIConstant.CMD_TTS, AIUIConstant.CANCEL, 0, "", null
//        );
//        if (mAIUIAgent != null) {
//            mAIUIAgent.sendMessage(resetWakeupMsg);
//        }
//    }

    protected void playTts(String text) {
        byte[] ttsData = text.getBytes(); //转为二进制数据
        StringBuffer params = new StringBuffer(); //构建合成参数
        params.append("vcn=x2_xiaojuan");//合成发音人
        params.append(",speed=50");//合成速度
        params.append(",pitch=50");//合成音调
        params.append(",volume=50");//合成音量

//        Executors.newCachedThreadPool().execute(() -> {
//            AIUIMessage startTts =
//                    new AIUIMessage(AIUIConstant.CMD_TTS, AIUIConstant.START, 0, params.toString(), ttsData);
//            if (mAIUIAgent != null) {
//                mAIUIAgent.sendMessage(startTts);
//            }
//        });
    }

    private void updateChatShow(ItemChatType type, String string) {
        if(!isForeground(this,"com.iflytek.caePk.NlpVoiceActivity")){
            Intent intent = new Intent(VoiceService.this, NlpVoiceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("data",string);
            startActivity(intent);
        }else {
            EventBus.getDefault().post(new ChatBean(type,string));
        }

        if (!TextUtils.isEmpty(string)) {
            Log.d("zdx", "updateChatShow: " + string);
            if(TextUtils.equals(string,"播放收音机") || TextUtils.equals(string,"播放电台")
                    || TextUtils.equals(string,"播放FM")){
                if(listBeans.size() > 0) {
                    String uri = listBeans.get(index).getUrl();
                    if (uri != null) {
                        Uri uri1 = Uri.parse(uri);
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(VoiceService.this, uri1);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                }
            }else if(TextUtils.equals(string,"切换下一个频道") || TextUtils.equals(string,"下一个频道")){
                if(listBeans.size() > 0 && index <= listBeans.size()) {
                    index = index + 1;
                    String uri = listBeans.get(index).getUrl();
                    if (uri != null) {
                        Uri uri1 = Uri.parse(uri);
                        try {
                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.reset();
                            }
                            mediaPlayer.setDataSource(VoiceService.this, uri1);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                }
            }else if(TextUtils.equals(string,"切换上一个频道") || TextUtils.equals(string,"上一个频道")){
                if(listBeans.size() > 0 && index > 0) {
                    index = index - 1;
                    String uri = listBeans.get(index).getUrl();
                    if (uri != null) {
                        Uri uri1 = Uri.parse(uri);
                        try {
                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.reset();
                            }
                            mediaPlayer.setDataSource(VoiceService.this, uri1);
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                }
            }else if(TextUtils.equals(string,"关闭收音机") || TextUtils.equals(string,"关闭电台")
                    || TextUtils.equals(string,"关闭FM")){
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
            }
        }
    }

    private void getFM(){
        // step1 : 请求参数转为 json
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("devId", id);

        // step2 :根据 aes 格式拼接数据
        String urlData = getUrlData(stringObjectHashMap);
        String url = "https://test-wbd.kuwo.cn/api/bd/book/news/category?" + urlData;
        // step3: 网络请求
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Log.d(TAG, "url: " + url);
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
                                if(TextUtils.equals(name,"交通")){
                                    getCategoryNews(fmTypeBean.getList().get(i).getCategoryId());
                                }
                                else if(TextUtils.equals(name,"综合")){
                                    getCategoryNews(fmTypeBean.getList().get(i).getCategoryId());
                                }
                                else if(TextUtils.equals(name,"资讯")){
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

    //根据id获取
    private void getCategoryNews(int categoryId){
        // step1 : 请求参数转为 json
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("devId", id);
        stringObjectHashMap.put("categoryId", categoryId);
        // step2 :根据 aes 格式拼接数据
        String urlData = getUrlData(stringObjectHashMap);
        String url = "https://test-wbd.kuwo.cn/api/bd/book/news/categoryNews?" + urlData;
        // step3: 网络请求
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
     * 加密
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

    public static boolean isForeground(Context mContext, String activityClassName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if(info != null && info.size() > 0){
            ComponentName component = info.get(0).topActivity;
            if(component.getClassName().equals(activityClassName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.release();
        }
    }
}
