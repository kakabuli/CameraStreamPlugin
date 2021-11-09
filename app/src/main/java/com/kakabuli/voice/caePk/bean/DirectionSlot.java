package com.kakabuli.voice.caePk.bean;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 *
 */
public class DirectionSlot {
    public static String parseIatResult(String json) {
        try {
            StringBuffer buffer = new StringBuffer();
            DirectionSlot directionSlot = JSON.parseObject(json, DirectionSlot.class);
            List<WsBean> ws = directionSlot.getWs();
            for (WsBean wsBean : ws) {
                List<WsBean.CwBean> cw = wsBean.getCw();
                for (WsBean.CwBean cwbean : cw) {
                    buffer.append(cwbean.getW());
                }
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private List<WsBean> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<WsBean> getWs() {
        return ws;
    }

    public void setWs(List<WsBean> ws) {
        this.ws = ws;
    }

    public static class WsBean {
        private int bg;
        private List<CwBean> cw;

        public int getBg() {
            return bg;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public List<CwBean> getCw() {
            return cw;
        }

        public void setCw(List<CwBean> cw) {
            this.cw = cw;
        }

        public static class CwBean {
            private double sc;
            private String w;

            public double getSc() {
                return sc;
            }

            public void setSc(double sc) {
                this.sc = sc;
            }

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }
        }
    }
}
