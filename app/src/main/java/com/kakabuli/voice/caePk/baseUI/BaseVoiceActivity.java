package com.kakabuli.voice.caePk.baseUI;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIMessage;
import com.kakabuli.voice.caePk.CaeOperator;
import com.kakabuli.voice.caePk.adapter.ChatAdapter;
import com.kakabuli.voice.caePk.bean.ChatBean;
import com.kakabuli.voice.caePk.bean.ItemChatType;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public abstract class BaseVoiceActivity extends Activity {
    private final String TAG = "BaseActivity";
    private AIUIAgent mAIUIAgent = null;
    private boolean isAsring = true;

    // 多麦克算法库
    private CaeOperator caeOperator = null;
    private RecyclerView recyclerView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        recyclerView = getRecycleViewFromChild();
        initViewData();
        initRecycleView();
    }

    protected void initViewData() {

    }

    protected abstract RecyclerView getRecycleViewFromChild();

    protected abstract int getLayoutResId();


    private ArrayList<ChatBean> listChat = new ArrayList<>();
    private ChatAdapter adapterChat = new ChatAdapter(listChat);

    private void initRecycleView() {
        recyclerView.setAdapter(adapterChat);
    }

    private StringBuilder stringBuilder = new StringBuilder();


    public void updateChatShow(ItemChatType type, String string) {
        if (!TextUtils.isEmpty(string)) {
            listChat.add(new ChatBean(type, string));
            adapterChat.notifyItemInserted(listChat.size() - 1);
            if (listChat.size() > 6) {
                recyclerView.smoothScrollToPosition(listChat.size() - 1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 开始进入语音交互
     */
    protected void startWakeToAsr() {
        if (caeOperator != null) {
            caeOperator.setRealBeam(0);
        }
        AIUIMessage resetWakeupMsg = new AIUIMessage(
                AIUIConstant.CMD_WAKEUP, 0, 0, "", null
        );
        if (mAIUIAgent != null) {
            mAIUIAgent.sendMessage(resetWakeupMsg);
        }
        isAsring = true;
    }

    /**
     * 手动退出语音交互
     */
    protected void stopWakeToAsr() {
        if (caeOperator != null) {
            caeOperator.setRealBeam(-1);
        }
        AIUIMessage resetWakeupMsg = new AIUIMessage(
                AIUIConstant.CMD_RESET_WAKEUP, 0, 0, "", null
        );
        if (mAIUIAgent != null) {
            mAIUIAgent.sendMessage(resetWakeupMsg);
        }
        isAsring = false;
    }

    protected void playTts(String text) {
        byte[] ttsData = text.getBytes(); //转为二进制数据
        StringBuffer params = new StringBuffer(); //构建合成参数
        params.append("vcn=x2_xiaojuan");//合成发音人
        params.append(",speed=50");//合成速度
        params.append(",pitch=50");//合成音调
        params.append(",volume=50");//合成音量

        Executors.newCachedThreadPool().execute(() -> {
            AIUIMessage startTts =
                    new AIUIMessage(AIUIConstant.CMD_TTS, AIUIConstant.START, 0, params.toString(), ttsData);
            if (mAIUIAgent != null) {
                mAIUIAgent.sendMessage(startTts);
            }
        });
    }
}
