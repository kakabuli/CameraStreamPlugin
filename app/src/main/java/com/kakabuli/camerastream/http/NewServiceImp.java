package com.kakabuli.camerastream.http;

import com.kakabuli.camerastream.http.post.LoginBean;
import com.kakabuli.camerastream.http.result.LoginResult;

import io.reactivex.Observable;

public class NewServiceImp {

    public static Observable<LoginResult> login(String username, String password) {
        LoginBean loginBean = new LoginBean(username, password);
        return RetrofitServiceManager.getNoTokenInstance().create(INewService.class)
                .login(new HttpUtils<LoginBean>().getBodyNoToken(loginBean))
                .compose(RxjavaHelper.observeOnMainThread());
    }
}
