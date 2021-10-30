package com.kakabuli.camerastream.socket.result;

public class VideoPlayResult {

    private VideoParam data;

    private String type;


    public class VideoParam{
        private String rtmpUrl;

        public String getRtmpUrl() {
            return rtmpUrl;
        }

        public void setRtmpUrl(String rtmpUrl) {
            this.rtmpUrl = rtmpUrl;
        }

        @Override
        public String toString() {
            return "VideoParam{" +
                    "rtmpUrl='" + rtmpUrl + '\'' +
                    '}';
        }
    }

    public String getType() {
        return type;
    }

    public VideoParam getData() {
        return data;
    }

    public void setData(VideoParam data) {
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VideoPlayResult{" +
                "data=" + data +
                ", type='" + type + '\'' +
                '}';
    }
}
