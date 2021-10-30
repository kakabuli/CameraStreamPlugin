package com.kakabuli.camerastream.http;

import android.text.TextUtils;
import android.util.Log;

import com.kakabuli.camerastream.BuildConfig;
import com.kakabuli.camerastream.MyApplication;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * @author Administrator
 * @date 2018/3/24
 */

public class RetrofitServiceManager {
    private static final int DEFAULT_TIME_OUT = 15;//超时时间
    private static final int DEFAULT_READ_TIME_OUT = 15;//读取时间
    private static final int DEFAULT_WRITE_TIME_OUT = 15;//读取时间
    private static Retrofit mRetrofit;  //还有token
    private static Retrofit noRetrofit;
    private static OkHttpClient.Builder builder;
    private static OkHttpClient.Builder noBuilder;


    public static Retrofit getInstance() {
        if (mRetrofit == null) {
            builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
            builder.writeTimeout(DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    return true;
                }
            });
            addInterceptor(builder, true);
            mRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .baseUrl(HttpConstants.HTTP_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    public static Retrofit getNoTokenInstance() {
        if (noRetrofit == null) {
            noBuilder = new OkHttpClient.Builder();
            noBuilder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            noBuilder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
            noBuilder.writeTimeout(DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);


            noBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {

                    return true;
                }
            });
//            noBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            addInterceptor(noBuilder, false);
            noRetrofit = new Retrofit.Builder()
                    .client(noBuilder.build())
                    .baseUrl(HttpConstants.HTTP_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return noRetrofit;
    }

    public static class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            if(BuildConfig.DEBUG)
                Log.e("shulan_http","网络请求体   " + message);
        }
    }

    public static class HttpHeadLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            if(BuildConfig.DEBUG)
                Log.e("shulan_http","网络请求头   " +  message);
        }
    }


    /**
     * 添加各种拦截器
     *
     * @param builder
     */
    private static void addInterceptor(OkHttpClient.Builder builder, boolean isToken) {
        // 添加日志拦截器，非debug模式不打印任何日志
//        LoggingInterceptor loggingInterceptor = new LoggingInterceptor.Builder()
//                .loggable(true)
//                .request()
//                .requestTag("网络请求 Request")
//                .response()
//                .responseTag("网络请求 response")
//                .build();
        HttpLoggingInterceptor logInterceptor1 = new HttpLoggingInterceptor(new HttpHeadLogger());
        logInterceptor1.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (isToken) {
            HttpHeaderInterceptor httpHeaderInterceptor = null;
            if (!TextUtils.isEmpty(MyApplication.getInstance().getToken())) {
                httpHeaderInterceptor = new HttpHeaderInterceptor.Builder()
                        .addHeaderParams("Authorization", MyApplication.getInstance().getToken())
                        .build();
                builder.addInterceptor(httpHeaderInterceptor);
            }
        } else {
            HttpHeaderInterceptor  httpHeaderInterceptor = new HttpHeaderInterceptor.Builder()
                    .build();
            builder.addInterceptor(httpHeaderInterceptor);
        }

        builder.addInterceptor(logInterceptor1);
        builder.addInterceptor(logInterceptor);
    }


    public <T> T creat(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

}
