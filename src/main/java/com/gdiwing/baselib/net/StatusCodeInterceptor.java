package com.gdiwing.baselib.net;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class StatusCodeInterceptor implements Interceptor {


    public static final int CODE_NETWORK_EXCEPTION = -2;
    public static final int CODE_GET_DATA_FAILED = -3;
    public static final int CODE_PARSE_DATA_ERROR = -4;  //数据解析出错（JSONException）
    public static final int CODE_SESSION_EXPIRED = 106; //session过期
    public static final int CODE_TOKEN_EXPIRED = 402;  //Token过期

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int statusCode = response.code();  //拿的是HTTP请求的状态码
        switch (statusCode) {
            case CODE_SESSION_EXPIRED:
            case CODE_TOKEN_EXPIRED:
                throw new RuntimeException("session or token expired");
        }
        if (statusCode < 200 || statusCode >= 400) {
            try {
                String httpErr = null;
                String networkErr = null;
                if (response.networkResponse() != null) {
                    httpErr = statusCode>0 ? String.valueOf(statusCode) : null;
                } else {
                    networkErr = "networkResponse == null";
                }
                throw new RuntimeException("getData failed!");
            } catch (Throwable e) {
                // Nothing to do
            }
        }
        return response;
    }
}
