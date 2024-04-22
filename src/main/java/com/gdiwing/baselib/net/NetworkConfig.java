package com.gdiwing.baselib.net;

import java.net.Proxy;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2019-05-20
 * Time: 10:14
 * Desc: 网络环境的配置
 */
public class NetworkConfig {

    /**
     * 读超时
     */
    private long mReadTimeout = 1000;

    /**
     * 写超时
     */
    private long mWriteTimeout = 1000;

    /**
     * 连接超时
     */
    private long mConnectTimeout = 1000;

    /**
     * 连接失败是否重连
     */
    private boolean mRetryOnConnectionFailure = false;

    /**
     * 拦截器
     */
    private final List<Interceptor> mInterceptors = new LinkedList<>();

    /**
     * 网络拦截器
     */
    private final List<Interceptor> mNetworkInterceptors = new LinkedList<>();

    /**
     * 序列化和反序列化对象时需要的转换器
     */
    private final List<Converter.Factory> mConverterFactories = new LinkedList<>();

    /**
     * 支持网络请求返回结果类型的转换器
     */
    private final List<CallAdapter.Factory> mCallAdapterFactories = new LinkedList<>();

    /**
     * 缓存
     */
    private Cache mCache;

    /**
     * 代理
     */
    private Proxy mProxy = null;

    /**
     * 事件监听
     */
    private EventListener mEventListener;

    /**
     * Cookie
     */
    private CookieJar mCookieJar;

    /**
     * Retrofit 的 默认url
     */
    private String mBaseUrl = "";


    public NetworkConfig setReadTimeout(long timeout) {
        if (timeout <= 0)
            throw new IllegalStateException("ReadTimeout must > 0");
        mReadTimeout = timeout;
        return this;
    }

    public NetworkConfig setConnectTimeout(long timeout) {
        if (timeout <= 0)
            throw new IllegalStateException("ConnectTimeout must > 0");
        mConnectTimeout = timeout;
        return this;
    }

    public NetworkConfig setWriteTimeout(long timeout) {
        if (timeout <= 0)
            throw new IllegalStateException("WriteTimeout must > 0");
        mWriteTimeout = timeout;
        return this;
    }

    public NetworkConfig addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    public NetworkConfig addNetworkInterceptor(Interceptor interceptor) {
        mNetworkInterceptors.add(interceptor);
        return this;
    }

    public NetworkConfig setInterceptors(List<Interceptor> interceptors) {
        mInterceptors.clear();
        mInterceptors.addAll(interceptors);
        return this;
    }

    public NetworkConfig setNetworkInterceptors(List<Interceptor> interceptors) {
        mNetworkInterceptors.clear();
        mNetworkInterceptors.addAll(interceptors);
        return this;
    }

    public NetworkConfig setBaseUrl(String url) {
        mBaseUrl = url;
        return this;
    }

    public NetworkConfig addConverterFactory(Converter.Factory factory) {
        mConverterFactories.add(factory);
        return this;
    }

    public NetworkConfig addCallAdapterFactory(CallAdapter.Factory factory) {
        mCallAdapterFactories.add(factory);
        return this;
    }

    public NetworkConfig setConverterFactories(List<Converter.Factory> factories) {
        mConverterFactories.clear();
        mConverterFactories.addAll(factories);
        return this;
    }

    public NetworkConfig setCallAdapterFactories(List<CallAdapter.Factory> factories) {
        mCallAdapterFactories.clear();
        mCallAdapterFactories.addAll(factories);
        return this;
    }

    public NetworkConfig setRetryOnConnectionFailure(boolean retry) {
        mRetryOnConnectionFailure = retry;
        return this;
    }

    public NetworkConfig setCache(Cache c) {
        this.mCache = c;
        return this;
    }

    public NetworkConfig setProxy(Proxy proxy) {
        this.mProxy = proxy;
        return this;
    }

    public NetworkConfig setEventListener(EventListener listener) {
        this.mEventListener = listener;
        return this;
    }

    public NetworkConfig setCookieJar(CookieJar cookieJar) {
        this.mCookieJar = cookieJar;
        return this;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public boolean isRetryOnConnectionFailure() {
        return mRetryOnConnectionFailure;
    }

    public List<Interceptor> getInterceptors() {
        return mInterceptors;
    }

    public List<Interceptor> getNetworkInterceptors() {
        return mNetworkInterceptors;
    }

    public List<Converter.Factory> getConverterFactories() {
        return mConverterFactories;
    }

    public List<CallAdapter.Factory> getCallAdapterFactories() {
        return mCallAdapterFactories;
    }

    public Cache getCache() {
        return mCache;
    }

    public Proxy getProxy() {
        return mProxy;
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public CookieJar getCookieJar() {
        return mCookieJar;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }
}
