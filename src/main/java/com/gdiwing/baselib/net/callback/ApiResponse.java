package com.gdiwing.baselib.net.callback;

/**
 * Created by LtLei on 2019/6/2.
 * Email: fighting_our_life@foxmail.com
 * <p>
 * Description: 网络数据返回规范
 */
public interface ApiResponse<T> {
    /**
     * 服务器状态是否成功，和getCode类似
     *
     * @return 若成功，返回true
     */
    boolean isSuccess();

    /**
     * 服务器返回的状态码
     *
     * @return 返回正确和出错时的状态码
     */
    int getCode();

    /**
     * 服务器返回的状态描述
     *
     * @return 描述
     */
    String getMessage();

    /**
     * 服务器返回的具体数据
     *
     * @return 具体的Entity
     */
    T getData();
}
