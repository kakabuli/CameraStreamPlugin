package com.kakabuli.camerastream.socket.result;

import java.io.Serializable;

public class BaseResult implements Serializable {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "type='" + type + '\'' +
                '}';
    }
}
