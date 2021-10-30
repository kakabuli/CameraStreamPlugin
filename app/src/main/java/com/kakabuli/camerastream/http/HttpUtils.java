package com.kakabuli.camerastream.http;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.RequestBody;
import retrofit2.HttpException;

public class HttpUtils<T> {

    public RequestBody getBodyToken(T t) {
        String obj = new Gson().toJson(t);
        String contentType = "application/json; charset=utf-8";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(contentType), obj);
        return body;
    }

    public RequestBody getBodyNoToken(T t){
        String obj = new Gson().toJson(t);
        String contentType = "application/json; charset=utf-8";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(contentType), obj);
        return body;
    }

    public RequestBody getBody(T t) {
        String obj = new Gson().toJson(t);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj);
        return body;
    }

}
