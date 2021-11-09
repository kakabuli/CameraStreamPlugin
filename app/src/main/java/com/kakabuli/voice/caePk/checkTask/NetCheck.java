package com.kakabuli.voice.caePk.checkTask;


import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.camerastream.R;
import com.kakabuli.voice.caePk.checkTask.base.BaseCheckTask;
import com.kakabuli.voice.caePk.checkTask.base.CheckType;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetCheck extends BaseCheckTask {
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    @Override
    public void checkHandler() {
        try {
            Request request = new Request.Builder().url("http://www.baidu.com").build();
            Response execute = okHttpClient.newCall(request).execute();
            if (execute.isSuccessful()) {
                checkDeviceBean.checkResult = MyApplication.getInstance().getString(R.string.check_net_result_success);
                checkDeviceBean.checkResultStatus = 1;
            } else {
                checkDeviceBean.checkResult = MyApplication.getInstance().getString(R.string.check_net_result_error);
                checkDeviceBean.checkResultStatus = 0;
            }
        } catch (Exception e) {
            checkDeviceBean.checkResult = MyApplication.getInstance().getString(R.string.check_net_result_error);
            checkDeviceBean.checkResultStatus = 0;
        }
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.NET;
        checkDeviceBean.checkTitle = MyApplication.getInstance().getString(R.string.check_net_title);
    }
}
