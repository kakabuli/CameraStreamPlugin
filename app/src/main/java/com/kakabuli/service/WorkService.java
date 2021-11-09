package com.kakabuli.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.kakabuli.bean.DownFileBean;
import com.kakabuli.camerastream.MyApplication;
import com.kakabuli.camerastream.http.HttpConstants;
import com.kakabuli.camerastream.http.NewServiceImp;
import com.kakabuli.camerastream.http.result.GetVideoDownLoadResult;
import com.kakabuli.camerastream.http.result.GetVideoResult;
import com.kakabuli.camerastream.http.result.LoginResult;
import com.kakabuli.camerastream.utils.Constants;
import com.kakabuli.camerastream.utils.DownFileUtils;
import com.kakabuli.camerastream.utils.MMKVUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WorkService extends Service {

    private static final String TAG = "zdx";
    private String playName;
    private String path;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(playName == null){
            path = MyApplication.PATH;
            playName = intent.getStringExtra("playName");
            Log.d(TAG,"onStartCommand " + playName);
            if(!TextUtils.isEmpty(MyApplication.getInstance().getToken())){
                getVideo();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void getVideo(){
        NewServiceImp.getVideo().subscribe(new Observer<GetVideoResult>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull GetVideoResult getVideoResult) {
                Log.d(TAG,"getVideo onNext-->" + getVideoResult.toString());

                if(getVideoResult.getMeta().getCode() == 200 && getVideoResult.getMeta().isSuccess()) {
                    String name = getVideoResult.getData().getName();
                    if(TextUtils.isEmpty(playName)){
                        downloadVideo(name);
                    }else {
                        if(!TextUtils.equals(playName,name)){
                            downloadVideo(name);
                        }
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("shulan_http","getVideo onError");
            }

            @Override
            public void onComplete() {
                Log.d("shulan_http","getVideo onComplete");
            }
        });
    }

    private void downloadVideo(final String name){
        NewServiceImp.getVideoDownload(HttpConstants.DOWNLOAD_VIDEO + name).subscribe(new Observer<GetVideoDownLoadResult>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull GetVideoDownLoadResult getVideoDownLoadResult) {
                Log.d(TAG,"downloadVideo onNext-->" + getVideoDownLoadResult.toString());

                if(getVideoDownLoadResult.getMeta().getCode() == 200 && getVideoDownLoadResult.getMeta().isSuccess()) {
                    DownFileUtils.createFolder(path);
                    String filePath = path + "/" + name;
                    String fileUrl = getVideoDownLoadResult.getData().getFileUrl();
                    DownFileUtils downFileUtils = new DownFileUtils() {
                        @Override
                        public void onFileExist(String url, String path) {
                            Log.d(TAG,"onFileExist url " + url);
                            Log.d(TAG,"onFileExist path " + path);
                        }

                        @Override
                        public void onDownFailed(String url, String path, Throwable throwable) {
                            Log.d(TAG,"下载文件失败，  " + throwable.getMessage());
                            EventBus.getDefault().post(new DownFileBean(404,throwable.getMessage()));
                        }

                        @Override
                        public void onTaskExist(String url, String path) {

                        }

                        @Override
                        public void onDownComplete(String url, String path) {
                            EventBus.getDefault().post(new DownFileBean(200,path));
                        }

                        @Override
                        public void onDownProgressUpdate(String url, String path, int progress) {
                            EventBus.getDefault().post(new DownFileBean(100,String.valueOf(progress)));
                        }
                    };
                    downFileUtils.downFile(fileUrl, filePath);

                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("shulan_http","downloadVideo onError");
            }

            @Override
            public void onComplete() {
                Log.d("shulan_http","downloadVideo onComplete");
            }
        });
    }

    //上传设备信息
    private void updata(String token, String data){
        NewServiceImp.uploadDeviceData(data).subscribe(new Observer<LoginResult>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull LoginResult loginResult) {
                Log.d(TAG,"updata onNext-->" + loginResult.toString());
                if(loginResult.getMeta().getCode() == 200 && loginResult.getMeta().isSuccess()) {

                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("shulan_http","updata onError");
            }

            @Override
            public void onComplete() {
                Log.d("shulan_http","updata onComplete");
            }
        });
    }

    @Override
    public void onDestroy() {

    }
}
