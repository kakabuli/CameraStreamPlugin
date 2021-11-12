package com.kakabuli.camerastream.utils;

public class Constants {

    /**
     * http登录成功的Token，WebSocket连接的password
     */
    public static final String DEVICE_TOKEN = "token";

    /**
     * WebSocket登录信息
     */
    public static final String TASK_LOGIN = "login";

    /**
     * WebSocket拉取当前任务指令
     */
    public static final String TASK_CHECK = "task_check";

    /**
     * WebSocket当前任务信息指令
     */
    public static final String TASK_CHECK_CALLBACK = "task_check_callback";

    /**
     * WebSocket提交任务成功回调
     */
    public static final String TASK_SUBMIT_SUCCESS = "task_submit_success";

    /**
     * WebSocket提交任务失败回调
     */
    public static final String TASK_SUBMIT_FAIL = "task_submit_fail";

    /**
     * WebSocket开始任务
     */
    public static final String TASK_START = "task_start";

    /**
     * WebSocket开始任务成功回调
     */
    public static final String TASK_START_SUCCESS = "task_start_success";

    /**
     * WebSocket开始任务失败回调
     */
    public static final String TASK_START_FAIL = "task_start_fail";

    /**
     *  WebSocket开始推流指令
     */
    public static final String VIDEO_PLAY = "video_play";

    /**
     * WebSocket推流成功指令
     */
    public static final String VIDEO_PLAY_SUCCESS = "video_play_success";

    /**
     * WebSocket推流失败指令
     */
    public static final String VIDEO_PLAY_FAIL = "video_play_fail";

    /**
     * WebSocket推流停止指令
     */
    public static final String VIDEO_PLAY_STOP = "video_play_stop";
}
