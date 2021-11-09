package com.kakabuli.voice;

import android.content.res.AssetFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.kakabuli.camerastream.R;
import com.kakabuli.voice.caePk.CaeOperator;
import com.kakabuli.voice.caePk.baseUI.BaseVoiceActivity;
import com.kakabuli.voice.caePk.bean.ChatBean;
import com.kakabuli.voice.caePk.bean.ItemChatType;
import com.kakabuli.voice.player.IPlayCallBack;
import com.kakabuli.voice.player.PlayerManager;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class NlpVoiceActivity extends BaseVoiceActivity {

    @NotNull
    @Override
    public RecyclerView getRecycleViewFromChild() {
        return findViewById(R.id.chatRecycleView);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_nlp;
    }

    private DrawerLayout drawerLayout;


    @Override
    protected void initViewData() {
        EventBus.getDefault().register(this);
        drawerLayout = findViewById(R.id.drawerLayout);

        SwitchCompat switchCompat = findViewById(R.id.logSwitch);
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CaeOperator.getInstance().isSaveAudio = isChecked;
        });

        SwitchCompat pcmSwitch = findViewById(R.id.pcmSwitch);
        pcmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                boolean needClose = false;
                boolean saveAudio = CaeOperator.getInstance().isSaveAudio;
                if (!saveAudio) {
                    needClose = true;
                    CaeOperator.getInstance().isSaveAudio = true;
                }
                AssetFileDescriptor open = null;
                try {
                    open = getAssets().openFd("saoping.wav");
                    boolean finalNeedClose = needClose;
                    PlayerManager.getInstance().playAssets(open, new IPlayCallBack() {
                        @Override
                        public void onEnd(boolean state, String msg) {
                            if (finalNeedClose) {
                                CaeOperator.getInstance().isSaveAudio = false;
                            }
                            Toast.makeText(NlpVoiceActivity.this, "扫频测试完毕", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setting(View view) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(ChatBean message) {
        Log.d("zdx", "onGetMessage: " + message.toString());
        if(!TextUtils.isEmpty(message.getData()) && message.getData().equals("进入休眠")){
            finish();
        }else {
            updateChatShow(message.getItemType() == 0 ? ItemChatType.People: ItemChatType.Robot,message.getData());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
