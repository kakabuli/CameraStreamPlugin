package com.kakabuli.voice.caePk.checkTask;


import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.camerastream.R;
import com.kakabuli.voice.caePk.checkTask.base.BaseCheckTask;
import com.kakabuli.voice.caePk.checkTask.base.CheckType;
import com.kakabuli.voice.caePk.utils.ShellUtils;

public class CardNumberCheck extends BaseCheckTask {
    @Override
    public void checkHandler() {
        int fetchCards = ShellUtils.fetchCards();
        if (fetchCards < 0) {
            checkDeviceBean.checkResultStatus = 0;
            boolean haveRoot = ShellUtils.haveRoot();
            if (haveRoot) {
                checkDeviceBean.checkResult = MyApplication.getInstance().getString(R.string.check_cards_num_no_exist);
            } else {
                checkDeviceBean.checkResult = MyApplication.getInstance().getString(R.string.system_no_root_permission);
            }
        } else {
            checkDeviceBean.checkResultStatus = 1;
            checkDeviceBean.checkResult = String.format(
                    MyApplication.getInstance().getString(
                            R.string.check_cards_num_result
                    ), fetchCards
            );
        }
    }

    @Override
    public void checkStart() {
        checkDeviceBean.checkType = CheckType.CARD_NUMBER;
        checkDeviceBean.checkTitle = MyApplication.getInstance().getString(R.string.check_cards_num_title);
    }
}
