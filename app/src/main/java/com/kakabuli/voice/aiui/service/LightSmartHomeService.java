package com.kakabuli.voice.aiui.service;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kakabuli.voice.aiui.base.BaseService;
import com.kakabuli.voice.aiui.base.ServiceType;
import com.kakabuli.voice.aiui.bean.SmartHomeBean;


public class LightSmartHomeService extends BaseService<JsonObject> {
    private static final String TAG = "LightSmartHomeService";

    @Override
    public ServiceType getType() {
        return ServiceType.story;
    }

    @Override
    public String handlerNlp(JsonObject data, String answer, JsonArray resultArray) {
        JsonObject slots = data.getAsJsonObject("slots");
        SmartHomeBean smartHomeBean = gson.fromJson(slots, new TypeToken<SmartHomeBean>() {
        }.getType());
        Log.e(TAG, "handlerNlp: " + smartHomeBean);
        return answer;
    }
}