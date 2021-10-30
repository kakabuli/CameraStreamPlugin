package com.kakabuli.camerastream.socket;

public interface IRTMPListener {

    /** socket连接成功回调
     *
     * @param code
     * @param message
     */
    void onSocketConnect(int code, String message);


    /** socket消息
     *
     * @param message
     */
    void onSocketMessage(String message);


    /** socket关闭连接
     *
     * @param code
     * @param reason
     * @param remote 是否由service端主动关闭
     */
    void onSocketClose(int code, String reason, boolean remote);

    /** socket异常回调
     *
     * @param e
     */
    void onSocketError(Exception e);
}
