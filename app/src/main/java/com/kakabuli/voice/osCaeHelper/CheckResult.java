package com.kakabuli.voice.osCaeHelper;

public class CheckResult {
    public String msg;
    public boolean state;

    public CheckResult(boolean state, String msg) {
        this.msg = msg;
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "msg='" + msg + '\'' +
                ", state=" + state +
                '}';
    }
}
