package com.gdiwing.baselib.net;

import android.os.Build;
import android.text.TextUtils;

import com.gdiwing.baselib.net.callback.HttpMethod;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zht on 2018/2/5.
 * 为所有的请求统一添加header
 */

public class HeaderInterceptor implements Interceptor {

    private static final String TAG = "HeaderInterceptor";

    public static final String DEVICE_ID = "deviceid";
    public static final String UID = "uid";
    public static final String TOKEN = "token";
    public static final String DEVICE = "device";
    public static final String DEVICE_TYPE = "devicetype";
    public static final String DEVICE_BRAND = "device-brand";
    public static final String LATITUDE = "latitude"; //纬度
    public static final String LONGITUDE = "longitude";
    public static final String CHANNEL_ID = "channelid";
    public static final String SESSION_ID = "sessionid";
    public static final String USER_AGENT = "user-agent";
    public static final String ENVIRONMENT = "environment";
    public static final String VERSION = "version";
    public static final String TRACEID = "traceid";
    public static final String CITY = "city";
    public static final String PROVICE = "province";
    public static final String X_INSTALL_TIME = "x-install-time";
    public static final String IS_FIRST_DAY_VISIT = "is_first_day_visit";
    public static final String BUNDLE_ID = "bundleid";
    public static final String BUILD = "build";
    public static final String NETWORK_TYPE = "networktype";
    public static final String ABTEST_CLASSIFICATION = "abtest-classification";
    public static final String IMEI = "imei";
    public static final String OAID = "oaid";
    public static final String ANDROID_ID = "androidID";
    public static final String MAC = "mac";
    public static final String CLIENT_TIME = "clienttime";
    public static final String IP = "ip";
    public static final String IS_FIRST_INSTALL = "is_first_install";
    public static final String AD_CHANNEL = "adchannel";//投放渠道
    public static final String LANGUAGE = "language";//国家地区语言


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HashMap<String, String> params = null;
        String signature = null;
        String method = oldRequest.method();
        if (!HttpMethod.METHOD_GET.equals(method)) {
            RequestBody body = oldRequest.body();
            if (body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                params = new HashMap<>();
                for (int i = 0; i < formBody.size(); i++) {
                    params.put(formBody.name(i), formBody.value(i));
                }
                params.put("timestamp", new Date().getTime() + "");
            }
        }

        Request.Builder newBuilder = chain.request().newBuilder();
        newBuilder
                .addHeader(DEVICE, "android")
                .addHeader(TRACEID, UUID.randomUUID().toString())
                .addHeader(CLIENT_TIME, System.currentTimeMillis() + "")
                .build();

        addNewRequestBody(params, newBuilder, oldRequest.method());

        return chain.proceed(newBuilder.build());
    }

    public static String checkBuildHeader(String header) {
        if (header == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int length = header.length(), i = 0; i < length; ++i) {
            char c = header.charAt(i);
            if ((c > 31 || c == 9) && c < 127) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    private RequestBody buildNewRequestBody(HashMap<String, String> params) {
        if (params == null)
            return null;
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            try {
                builder.add(entry.getKey(), entry.getValue());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return builder.build();
    }

    private void addNewRequestBody(HashMap<String, String> params, Request.Builder builder, String method) {
        if (params == null || builder == null)
            return;
        RequestBody requestBody = buildNewRequestBody(params);
        if (requestBody == null)
            return;
        switch (method) {

            case HttpMethod.METHOD_POST:
                builder.post(requestBody);
                break;

            case HttpMethod.METHOD_DELETE:
                builder.delete(requestBody);
                break;

            case HttpMethod.METHOD_PATCH:
                builder.patch(requestBody);
                break;

            default:
                break;
        }
    }

}
