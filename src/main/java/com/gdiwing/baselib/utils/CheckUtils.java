package com.gdiwing.baselib.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * Created by LtLei on 2019/6/2.
 * Email: fighting_our_life@foxmail.com
 * <p>
 * Description: CheckUtils
 */
public final class CheckUtils {
    private CheckUtils() {
        throw new UnsupportedOperationException("CheckUtils not support initialize.");
    }

    /**
     * 若泛型T为空，抛出NullPointerException
     *
     * @param t
     * @param message
     * @param <T>
     * @return 传入的泛型对象
     */
    public static <T> T checkNotNull(T t, @Nullable String message) {
        if (t == null) {
            Log.e("xxx",message);
        }
        return t;
    }

    /**
     * 若字符串为空，抛出NullPointerException
     *
     * @param str
     * @param message
     * @return 返回传入的字符串对象
     */
    public static String checkNotEmpty(String str, @Nullable String message) {
        if (isNullOrEmpty(str)) {
            Log.e("xxx",message);
        }
        return str;
    }

    /**
     * 检查字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }

    /**
     * 若类是Primitive，抛出IllegalArgumentException
     *
     * @param type
     */
    public static void checkNotPrimitive(Type type) {
        if (type instanceof Class<?> && ((Class<?>) type).isPrimitive()) {
            throw new IllegalArgumentException();
        }
    }
}