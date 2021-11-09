package com.kakabuli.voice.aiui.base;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public abstract class BaseService<T> {
    protected Gson gson = new Gson();

    public abstract ServiceType getType();

    public abstract String handlerNlp(T semanticJson, String answer, JsonArray resultArray);

    public boolean semanticArray() {
        return true;
    }

    public void handlerWake() {

    }
}
