package com.kakabuli.camerastream.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakabuli.camerastream.utils.StringUtil;
import com.kakabuli.camerastream.socket.request.TaskCheckInfo;
import com.kakabuli.camerastream.socket.result.BaseResult;
import com.kakabuli.camerastream.utils.Constants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class MySocket extends WebSocketClient {

    private final IRTMPListener mRtmpListener;
    private boolean connectFlag = false;

    public MySocket(URI serverUri ,IRTMPListener rtmpListener) {
        super(serverUri);
        this.mRtmpListener = rtmpListener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("liuhai_MySocket"," RtcWebSocket onOpen == Status == " + handshakedata.getHttpStatus() + " StatusMessage == " + handshakedata.getHttpStatusMessage());
        if(mRtmpListener != null){
            mRtmpListener.onSocketConnect(handshakedata.getHttpStatus(),handshakedata.getHttpStatusMessage());
            connectFlag = true;
        }
    }

    @Override
    public boolean reconnectBlocking() throws InterruptedException {
        return super.reconnectBlocking();
    }

    @Override
    public void reconnect() {
        super.reconnect();
    }

    @Override
    public void onMessage(String message) {
        Log.d("liuhai_MySocket"," RtcWebSocket String onMessage == " + message.toString());
        handleMessage(message);
        if(mRtmpListener != null){
            mRtmpListener.onSocketMessage(message);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
        Log.d("liuhai_MySocket"," RtcWebSocket ByteBuffer onMessage == " + bytes.toString());
        onMessage(StringUtil.getByteBufferToString(bytes));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("liuhai_MySocket"," RtcWebSocket onClose == code " + code + " reason == " + reason + " remote == " + remote);
        if(mRtmpListener != null){
            mRtmpListener.onSocketClose(code,reason,remote);
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.d("liuhai_MySocket"," RtcWebSocket onError== " + ex.getMessage());
        if(mRtmpListener != null){
            mRtmpListener.onSocketError(ex);
            connectFlag = false;
        }
    }

    //==================================消息处理===================================

    private void handleMessage(String message) {
        BaseResult mBaseResult = new Gson().fromJson(message,
                new TypeToken<BaseResult>() {}.getType());
        switch (mBaseResult.getType()){
            case Constants.TASK_LOGIN:
                sendTaskCheck();
                break;

            case Constants.TASK_CHECK_CALLBACK:
                break;
            case Constants.VIDEO_PLAY:
                break;
            case Constants.VIDEO_PLAY_STOP:
                break;
        }
    }











    private void sendTaskCheck() {
        TaskCheckInfo taskCheckInfo = new TaskCheckInfo(Constants.TASK_CHECK);
        String text = new Gson().toJson(taskCheckInfo);
        send(text);
    }
}
