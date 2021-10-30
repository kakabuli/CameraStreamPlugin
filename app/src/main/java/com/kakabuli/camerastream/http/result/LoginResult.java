package com.kakabuli.camerastream.http.result;

import java.io.Serializable;

public class LoginResult implements Serializable {

    private DataParam data;
    private MetaParam meta;

    public DataParam getData() {
        return data;
    }

    public void setData(DataParam data) {
        this.data = data;
    }

    public MetaParam getMeta() {
        return meta;
    }

    public void setMeta(MetaParam meta) {
        this.meta = meta;
    }

    public class DataParam{
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "DataParam{" +
                    "token='" + token + '\'' +
                    '}';
        }
    }


    public class MetaParam {
        private String msg;
        private int code;
        private boolean success;
        private String timestamp;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "Param{" +
                    "msg='" + msg + '\'' +
                    ", code=" + code +
                    ", success=" + success +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "LoginResult{" +
                "data='" + data + '\'' +
                ", meta=" + meta +
                '}';
    }
}
