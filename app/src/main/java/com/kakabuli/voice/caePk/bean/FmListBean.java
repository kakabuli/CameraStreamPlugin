package com.kakabuli.voice.caePk.bean;

import java.util.List;

public class FmListBean {
    private List<Info> list;

    public void setList(List<Info> list){
        this.list = list;
    }
    public List<Info> getList(){
        return this.list;
    }

    public class Info{

            private int listenerCount;
            private String hz;
            private String programName;
            private int locationId;
            private String name;
            private String pic;
            private String programCompere;
            private int categoryId;
            private String url;
            private long programId;
            private String channelId;
            public void setListenerCount(int listenerCount) {
                this.listenerCount = listenerCount;
            }
            public int getListenerCount() {
                return listenerCount;
            }

            public void setHz(String hz) {
                this.hz = hz;
            }
            public String getHz() {
                return hz;
            }

            public void setProgramName(String programName) {
                this.programName = programName;
            }
            public String getProgramName() {
                return programName;
            }

            public void setLocationId(int locationId) {
                this.locationId = locationId;
            }
            public int getLocationId() {
                return locationId;
            }

            public void setName(String name) {
                this.name = name;
            }
            public String getName() {
                return name;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }
            public String getPic() {
                return pic;
            }

            public void setProgramCompere(String programCompere) {
                this.programCompere = programCompere;
            }
            public String getProgramCompere() {
                return programCompere;
            }

            public void setCategoryId(int categoryId) {
                this.categoryId = categoryId;
            }
            public int getCategoryId() {
                return categoryId;
            }

            public void setUrl(String url) {
                this.url = url;
            }
            public String getUrl() {
                return url;
            }

            public void setProgramId(long programId) {
                this.programId = programId;
            }
            public long getProgramId() {
                return programId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
            }
            public String getChannelId() {
                return channelId;
            }

            @Override
            public String toString() {
                return "JsonRootBean{" +
                        "listenerCount=" + listenerCount +
                        ", hz='" + hz + '\'' +
                        ", programName='" + programName + '\'' +
                        ", locationId=" + locationId +
                        ", name='" + name + '\'' +
                        ", pic='" + pic + '\'' +
                        ", programCompere='" + programCompere + '\'' +
                        ", categoryId=" + categoryId +
                        ", url='" + url + '\'' +
                        ", programId=" + programId +
                        ", channelId='" + channelId + '\'' +
                        '}';
            }
    }

    @Override
    public String toString() {
        return "FmListBean{" +
                "list=" + list +
                '}';
    }
}
