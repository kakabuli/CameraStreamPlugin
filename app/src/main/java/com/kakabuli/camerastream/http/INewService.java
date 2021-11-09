package com.kakabuli.camerastream.http;

import com.kakabuli.camerastream.http.result.GetVideoDownLoadResult;
import com.kakabuli.camerastream.http.result.GetVideoResult;
import com.kakabuli.camerastream.http.result.LoginResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface INewService {


    /**
     * 通过手机号登陆
     *
     * @return
     */
    @POST(HttpConstants.LOGIN)
    Observable<LoginResult> login( @Body RequestBody info);

    /**
     * 获取视频下载的名称
     *
     * @return
     */
    @GET(HttpConstants.VIDEO_LAST)
    Observable<GetVideoResult> getVideo();

    @GET
    Observable<GetVideoDownLoadResult> getVideoDownload(@Url String url);

    /**
     * 通过手机号登陆
     *
     * @return
     */
    @POST(HttpConstants.UPLOAD_DEVICE_DATA)
    Observable<LoginResult> uploadDeviceData( @Body RequestBody info);
}
