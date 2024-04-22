package com.gdiwing.baselib.net;

import com.gdiwing.baselib.net.callback.ApiResponse;
import com.google.gson.annotations.SerializedName;

public class BaseDefaultAPIResponse<T> implements ApiResponse<T> {

    private static final int SUCCESS_CODE = 200;
    @SerializedName(value = "code")
    private int code = 200;
    @SerializedName(value = "message", alternate = {"msg"})
    private String message;

    @SerializedName(value = "data", alternate = {"result"})
    private T data;

    @Override
    public boolean isSuccess() {
        return getCode() == SUCCESS_CODE || getCode() == 0;  //兼容200，0的返回值
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultAPIResponseV1{");
        sb.append("code=").append(code);
        sb.append(", message='").append(message).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
