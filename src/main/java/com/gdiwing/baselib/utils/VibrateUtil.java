package com.gdiwing.baselib.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * 手机震动工具类
 * @author Administrator
 * Created by Administrator on 2017-01-21.
 */
public class VibrateUtil {

    /**
     * final Activity activity ：调用该方法的Activity实例
     * long pattern ：震动的时长，单位是毫秒
     */
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     *
     * @param activity 调用该方法的Activity实例
     * @param pattern long[] pattern ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * @param isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public static void vibrate(final Activity activity, long[] pattern,boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }
}