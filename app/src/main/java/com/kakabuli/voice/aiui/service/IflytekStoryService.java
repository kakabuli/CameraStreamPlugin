package com.kakabuli.voice.aiui.service;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.kakabuli.voice.aiui.base.BaseService;
import com.kakabuli.voice.aiui.base.SemanticNormal;
import com.kakabuli.voice.aiui.base.ServiceType;
import com.kakabuli.voice.aiui.bean.StoryBean;
import com.kakabuli.voice.player.PlayerManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IflytekStoryService extends BaseService<JsonArray> {
    private static final String TAG = "IflytekStoryService";

    @Override
    public ServiceType getType() {
        return ServiceType.story;
    }

    private List<StoryBean> storyList = new ArrayList<>();

    @Override
    public String handlerNlp(JsonArray data, String answer, JsonArray resultArray) {
        List<SemanticNormal> semanticList = gson.fromJson(data.toString(), new TypeToken<List<SemanticNormal>>() {
        }.getType());
        if (semanticList != null && semanticList.size() > 0) {
            SemanticNormal semanticNormal = semanticList.get(0);
            String intent = semanticNormal.getIntent();

            Log.e(TAG, "handlerNlp: " + intent);
            if (intent.equals("NEXT") || intent.equals("PREVIOUS")) {
                if (storyList.size() > 0) {
                    StoryBean storyBean = storyList.get((int) (storyList.size() * Math.random()));
                    playRandom(storyBean);
                    answer = "好的";
                }
            } else if (intent.equals("RANDOM_QUERY")) {
                String s = resultArray.toString();
                Type type = new TypeToken<List<StoryBean>>() {
                }.getType();
                List<StoryBean> storyBeanList = gson.fromJson(s, type);
                if (storyBeanList != null && storyBeanList.size() > 0) {
                    storyList.clear();
                    storyList.addAll(storyBeanList);
                    StoryBean storyBean = storyBeanList.get(0);
                    Log.e(TAG, "handlerNlp: " + storyBean);
                    playRandom(storyBean);
                }
            } else if (intent.equals("PAUSE")) {
                PlayerManager.getInstance().pause();
                answer = "好的";
            }
        }

        return "好的";
    }

    private void playRandom(StoryBean storyBean) {
        String playUrl = storyBean.getPlayUrl();
        //String[] split = playUrl.split("\\?");
        PlayerManager.getInstance().playUrl(playUrl, (state, msg) -> Log.e(TAG, "onEnd: " + state + " " + msg));
    }

    @Override
    public void handlerWake() {
        PlayerManager.getInstance().stop();
    }
}
