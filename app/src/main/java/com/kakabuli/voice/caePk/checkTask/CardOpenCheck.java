package com.kakabuli.voice.caePk.checkTask;


import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.camerastream.R;
import com.kakabuli.voice.caePk.CaeOperator;
import com.kakabuli.voice.caePk.checkTask.base.BaseCheckTask;
import com.kakabuli.voice.caePk.checkTask.base.CheckType;
import com.kakabuli.voice.osCaeHelper.CheckResult;

public class CardOpenCheck extends BaseCheckTask {

    @Override
    public void checkHandler() {
        CheckResult openAndStartRecord = CaeOperator.getInstance().openAndStartRecord(-1);
        if (openAndStartRecord.state) {
            checkDeviceBean.checkResultStatus = 1;
        } else {
            checkDeviceBean.checkResultStatus = 0;
        }
        checkDeviceBean.checkResult = openAndStartRecord.msg;
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.CARD_OPEN;
        checkDeviceBean.checkTitle = MyApplication.getInstance().getString(R.string.check_open_card);
    }
}
