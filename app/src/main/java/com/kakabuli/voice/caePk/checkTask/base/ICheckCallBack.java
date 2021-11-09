package com.kakabuli.voice.caePk.checkTask.base;


import com.kakabuli.voice.caePk.bean.CheckDeviceBean;

public interface ICheckCallBack {
    void onCheckStart(CheckDeviceBean checkDeviceBean);

    void onCheckEnd(CheckDeviceBean checkDeviceBean);
}
