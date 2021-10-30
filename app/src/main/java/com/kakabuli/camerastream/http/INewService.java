package com.kakabuli.camerastream.http;

import com.kakabuli.camerastream.http.result.LoginResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface INewService {


    /**
     * 通过手机号登陆
     *
     * @return
     */
    @POST(HttpConstants.LOGIN)
    Observable<LoginResult> login( @Body RequestBody info);
}
