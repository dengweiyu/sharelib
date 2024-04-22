package com.gdiwing.baselib.utils;

import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by LtLei on 2019/6/2.
 * Email: fighting_our_life@foxmail.com
 * <p>
 * Description: RxJava2 utils
 */
public final class RxUtils {
    private RxUtils() {
        throw new UnsupportedOperationException("RxUtils not support initialize.");
    }

    /**
     * 将Flowable从IO线程切换到主线程
     *
     * @param <T>
     * @return 原有的Flowable对象
     */
    public static <T> FlowableTransformer<T, T> _ioMain() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> __ioMain() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> ioMain() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
