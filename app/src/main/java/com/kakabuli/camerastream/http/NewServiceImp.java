package com.kakabuli.camerastream.http;

import com.kakabuli.camerastream.http.post.LoginBean;
import com.kakabuli.camerastream.http.post.UpDeviceBean;
import com.kakabuli.camerastream.http.result.GetVideoDownLoadResult;
import com.kakabuli.camerastream.http.result.GetVideoResult;
import com.kakabuli.camerastream.http.result.LoginResult;

import io.reactivex.Observable;

public class NewServiceImp {

    public static Observable<LoginResult> login(String username, String password) {
        LoginBean loginBean = new LoginBean(username, password);
        return RetrofitServiceManager.getNoTokenInstance().create(INewService.class)
                .login(new HttpUtils<LoginBean>().getBodyNoToken(loginBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }

    public static Observable<GetVideoResult> getVideo() {
        return RetrofitServiceManager.getInstance().create(INewService.class)
                .getVideo()
                .compose(RxjavaHelper.observeOnMainThread());
    }

    public static Observable<GetVideoDownLoadResult> getVideoDownload(String url) {
        return RetrofitServiceManager.getInstance().create(INewService.class)
                .getVideoDownload(url)
                .compose(RxjavaHelper.observeOnMainThread());
    }

    public static Observable<LoginResult> uploadDeviceData(String data) {
        UpDeviceBean upDeviceBean = new UpDeviceBean(data);
        return RetrofitServiceManager.getInstance().create(INewService.class)
                .uploadDeviceData(new HttpUtils<UpDeviceBean>().getBodyNoToken(upDeviceBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }
}
