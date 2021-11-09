package com.kakabuli.voice.caePk.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ChatBean extends MultiItemEntity {
    ItemChatType chatType;

    String data;

    public String getData() {
        return data;
    }

    public ChatBean(ItemChatType chatType, String data) {
        this.chatType = chatType;
        this.data = data;
    }

    @Override
    public int getItemType() {
        return chatType.ordinal();
    }
}
