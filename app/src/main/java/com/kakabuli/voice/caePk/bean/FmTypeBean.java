package com.kakabuli.voice.caePk.bean;

import java.util.List;

public class FmTypeBean {


    private List<Type> list;

    public void setList(List<Type> list){
        this.list = list;
    }
    public List<Type> getList(){
        return this.list;
    }


    public class Type{
        private String name;

        private int categoryId;

        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setCategoryId(int categoryId){
            this.categoryId = categoryId;
        }
        public int getCategoryId(){
            return this.categoryId;
        }

        @Override
        public String toString() {
            return "Type{" +
                    "name='" + name + '\'' +
                    ", categoryId=" + categoryId +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FmTypeBean{" +
                "list=" + list +
                '}';
    }
}
