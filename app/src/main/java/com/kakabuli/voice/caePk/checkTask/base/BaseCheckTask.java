package com.kakabuli.voice.caePk.checkTask.base;


import com.kakabuli.voice.caePk.bean.CheckDeviceBean;

public abstract class BaseCheckTask implements Runnable {
    protected CheckDeviceBean checkDeviceBean = new CheckDeviceBean();
    private ICheckCallBack iCheckCallBack = null;

    public boolean isCheckSuccess() {
        return checkDeviceBean.checkResultStatus == 1;
    }

    @Override
    public void run() {
        try {
            checkStart();
            checkStartCallBack();
            Thread.sleep(500);
            checkHandler();
            checkEndCallBack();
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    private void checkEndCallBack() {
        if (iCheckCallBack != null) {
            iCheckCallBack.onCheckEnd(checkDeviceBean);
        }
    }

    private void checkStartCallBack() {
        if (iCheckCallBack != null) {
            iCheckCallBack.onCheckStart(checkDeviceBean);
        }
    }

    public void setICheckCallBack(ICheckCallBack iCheckCallBack) {
        this.iCheckCallBack = iCheckCallBack;
    }

    public abstract void checkHandler();

    /**
     * 开始检测
     */
    public abstract void checkStart();
}
