package com.kakabuli.voice.aiui.service;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.voice.aiui.base.BaseService;
import com.kakabuli.voice.aiui.base.InsType;
import com.kakabuli.voice.aiui.base.IntentType;
import com.kakabuli.voice.aiui.base.SemanticNormal;
import com.kakabuli.voice.aiui.base.ServiceType;
import com.kakabuli.voice.kwyuliang.KwHandler;
import com.kakabuli.voice.kwyuliang.KwInfo;
import com.kakabuli.voice.kwyuliang.KwPlayAction;
import com.kakabuli.voice.kwyuliang.SystemHelper;

import java.util.List;

public class IflytekMusicProService extends BaseService<JsonArray> {
    private static final String TAG = "IFlytekMusicProService";
    private Handler handler = new Handler(Looper.getMainLooper());
    private static String[] artistList = {"张学友", "邓紫棋", "刘德华", "郑源", "刀郎", "水木年华", "黎明", "古巨基", "张智霖"};

    public IflytekMusicProService() {
    }

    @Override
    public ServiceType getType() {
        return ServiceType.musicPro;
    }

    @Override
    public String handlerNlp(JsonArray data, String answer, JsonArray resultArray) {
//        List<SemanticNormal> semanticList = gson.fromJson(data.toString(), new TypeToken<List<SemanticNormal>>() {
//        }.getType());
//        boolean isFirst = KwHandler.initKw(MyApplication.getInstance());
//        SemanticNormal semantic = semanticList.get(0);
//        String intent = semantic.getIntent();
//        switch (IntentType.IflytekMusicPro.valueOf(intent)) {
//            case PLAY:
//                playMusic(semantic.getSlots());
//                break;
//            case ADVICE:
//                break;
//            case INSTRUCTION:
//                instructionMusic(semantic.getSlots());
//                break;
//            case RANDOM_SEARCH:
//                KwHandler.playOneMusic();
//                break;
//        }
//        handler.removeCallbacksAndMessages(null);
//        if (!isFirst) {
//            handler.postDelayed(() -> SystemHelper.setTopApp(MyApplication.getInstance()), 8000);
//        } else {
//            handler.postDelayed(() -> SystemHelper.setTopApp(MyApplication.getInstance()), 20000);
//        }
        return "好的";
    }

    private void instructionMusic(List<SemanticNormal.Slots> slots) {
//        String insType = slots.get(0).getValue();
//        Log.e(TAG, "instructionMusic: " + insType);
//        if (InsType.IflytekMusicPro.close.name().equals(insType) ||
//                InsType.IflytekMusicPro.pause.name().equals(insType)) {
//            KwHandler.kwPlayCtrl(KwPlayAction.STATE_PAUSE);
//        } else if (InsType.IflytekMusicPro.play.name().equals(insType)) {
//            KwHandler.kwPlayCtrl(KwPlayAction.STATE_PLAY);
//
//        } else if (InsType.IflytekMusicPro.next.name().equals(insType) ||
//                InsType.IflytekMusicPro.past.name().equals(insType)) {
//            mKuInfo.clean();
//            this.mKuInfo.artist = artistList[(int) (Math.random() * artistList.length)];
//            KwHandler.searchPlayInApp(mKuInfo);
//        } else if (InsType.IflytekMusicPro.random.name().equals(insType)) {
//            mKuInfo.clean();
//            this.mKuInfo.artist = artistList[(int) (Math.random() * artistList.length)];
//            KwHandler.searchPlayInApp(mKuInfo);
//        }
    }

    private KwInfo mKuInfo = new KwInfo();

    private void playMusic(List<SemanticNormal.Slots> slots) {
        mKuInfo.clean();
        for (SemanticNormal.Slots slot : slots) {
            String name = slot.getName();
            if (!TextUtils.isEmpty(name)) {
                String value = slot.getValue();
                switch (name) {
                    case "song":
                        this.mKuInfo.song = value;
                        break;
                    case "artist":
                        this.mKuInfo.artist = value;
                        break;
                    case "theme":
                        this.mKuInfo.theme = value;
                        break;
                    case "lang":
                        this.mKuInfo.otherKey = value;
                        break;
                }
            }
        }
        Log.d(TAG, "playMusic: " + mKuInfo);
//        KwHandler.searchPlayInApp(mKuInfo);
    }

    @Override
    public void handlerWake() {
//        KwHandler.kwPlayCtrl(KwPlayAction.STATE_PAUSE);
        SystemHelper.setTopApp(MyApplication.getInstance());
    }
}
