package com.kakabuli.voice.caePk.bean;


import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.kakabuli.voice.caePk.checkTask.base.CheckType;

public class CheckDeviceBean extends MultiItemEntity {
    public ItemCheckType itemCheckType = ItemCheckType.ITEM_TEXT;
    public String checkTitle = "";
    public String checkResult = "";
    public int checkResultStatus = -1;
    public CheckType checkType = CheckType.CARD_NUMBER;

    public void setItemCheckType(ItemCheckType itemCheckType) {
        this.itemCheckType = itemCheckType;
    }

    public void setCheckTitle(String checkTitle) {
        this.checkTitle = checkTitle;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public void setCheckResultStatus(int checkResultStatus) {
        this.checkResultStatus = checkResultStatus;
    }

    public void setCheckType(CheckType checkType) {
        this.checkType = checkType;
    }

    public ItemCheckType getItemCheckType() {
        return itemCheckType;
    }

    public String getCheckTitle() {
        return checkTitle;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public int getCheckResultStatus() {
        return checkResultStatus;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    @Override
    public String toString() {
        return "CheckDeviceBean{" +
                "itemCheckType=" + itemCheckType +
                ", checkTitle='" + checkTitle + '\'' +
                ", checkResult='" + checkResult + '\'' +
                ", checkResultStatus=" + checkResultStatus +
                ", checkType=" + checkType +
                '}';
    }
}
