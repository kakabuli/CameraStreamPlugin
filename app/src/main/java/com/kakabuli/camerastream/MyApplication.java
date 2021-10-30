package com.kakabuli.camerastream;

import android.app.Application;
import android.util.Log;

import com.tencent.mmkv.MMKV;

public class MyApplication extends Application {

    private String token;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initMMKV(this);
    }

    private void initMMKV(MyApplication myApplication) {
        String rootDir = MMKV.initialize(myApplication);
        Log.v("shulan_mmkv"," mmkv root: " + rootDir);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {  //每次设置token  都更新一下
        this.token = token;
    }
}
