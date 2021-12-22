package com.kakabuli.voice.caePk.checkTask;

import com.kakabuli.camerastream.BuildConfig;
import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.camerastream.R;
import com.kakabuli.voice.caePk.checkTask.base.BaseCheckTask;
import com.kakabuli.voice.caePk.checkTask.base.CheckType;

public class PlatformMicCheck extends BaseCheckTask {
    @Override
    public void checkHandler() {
//        checkDeviceBean.checkResult = String.format(
//                MyApplication.getInstance().getString(R.string.platform_mic),
//                BuildConfig.Platform,
//                BuildConfig.MicType
//        );
        checkDeviceBean.checkResultStatus = 1;
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.PLATFORM_MIC;
        checkDeviceBean.checkTitle = MyApplication.getInstance().getString(R.string.check_platform_mic);
    }
}
