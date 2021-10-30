package com.kakabuli.camerastream.http;

import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/25.
 */

public class RxjavaHelper {

    /**
     * 切换线程操作
     * @return Observable转换器
     */
    public static <T> ObservableTransformer<T, T> observeOnMainThread() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 切换线程操作
     * @return Observable转换器
     */
    public static <T> ObservableTransformer<T, T> toTimeOut(long timeOut) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.ambWith(getTimeOutObservable(timeOut));
            }
        };
    }
    /**
     * 切换线程操作
     * @return Observable转换器
     */
    public static <T> ObservableTransformer<T, T> toTimeOut(long timeOut, Throwable throwable) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.ambWith(getTimeOutObservable(timeOut));
            }
        };
    }


    public static <T> Observable<T> getTimeOutObservable(long timeOut){
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        try {
                            Thread.sleep(timeOut);
                            if (!emitter.isDisposed() ){
                                emitter.tryOnError(new TimeoutException());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public static <T> Observable<T> getTimeOutObservable(long timeOut, Throwable throwable){
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        try {
                            Thread.sleep(timeOut);
                            if (!emitter.isDisposed() ){
                                emitter.tryOnError(throwable);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

}

