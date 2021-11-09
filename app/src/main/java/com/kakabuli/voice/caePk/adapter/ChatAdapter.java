package com.kakabuli.voice.caePk.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kakabuli.camerastream.R;
import com.kakabuli.voice.caePk.bean.ChatBean;
import com.kakabuli.voice.caePk.bean.ItemChatType;

import java.util.List;

public class ChatAdapter extends BaseMultiItemQuickAdapter<ChatBean> {
    public ChatAdapter(List<ChatBean> dataList) {
        super(dataList);
        addItemType(ItemChatType.People.ordinal(), R.layout.layout_item_people);
        addItemType(ItemChatType.Robot.ordinal(), R.layout.layout_item_robot);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, ChatBean chatBean) {
        if (viewHolder.getItemViewType() == ItemChatType.People.ordinal()) {
            TextView view = viewHolder.itemView.findViewById(R.id.text_people);
            view.setText(chatBean.getData());
        } else if (viewHolder.getItemViewType() == ItemChatType.Robot.ordinal()) {
            TextView view = viewHolder.itemView.findViewById(R.id.text_robot);
            view.setText(chatBean.getData());
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatBean chatBean = (ChatBean) getItem(position);
        return chatBean.getItemType();
    }
}
