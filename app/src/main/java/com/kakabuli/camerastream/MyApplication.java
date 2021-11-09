package com.kakabuli.camerastream;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.kakabuli.camerastream.http.NewServiceImp;
import com.kakabuli.camerastream.http.result.LoginResult;
import com.kakabuli.camerastream.utils.Constants;
import com.kakabuli.camerastream.utils.MMKVUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.mmkv.MMKV;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MyApplication extends Application {

    private String token;
    private static MyApplication instance;
    public static String PATH ; // 视频存放的路径；

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        PATH = getExternalFilesDir("").getAbsolutePath() + File.separator + "downloadVideo";
        FileDownloader.setup(this);
        initMMKV(this);
        // TODO: 2021/11/9 要确保拿到TOKEN,不然无法下载视频跟推流 
        getUserToken();
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
            }
        });
    }
}
