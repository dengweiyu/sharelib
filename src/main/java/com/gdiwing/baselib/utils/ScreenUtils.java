package com.gdiwing.baselib.utils;

import static android.content.Context.UI_MODE_SERVICE;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;

import java.lang.reflect.Method;

/**
 * Created by y on 2016/4/28.
 */

/*
 * 屏幕像素转换工具类
 */
public class ScreenUtils {

    private static int baseWidth = 750;
    private static int baseHeight = 1334;
    private static float scalePercent = 1;
    private static float yScalePercent = 1;
    private static int screenHeight;
    private static int screenWidth;
    private static int statusBarHeight;
    private static boolean lanScape = false;
    public static String SCREEN_LAYOUT_HEIGHT = "slh";

    public static boolean isLanScape() {
        return lanScape;
    }

    public static void init(Activity activity) {
        init(activity, SPUtils.getInstance().getInt(SCREEN_LAYOUT_HEIGHT, 0), false);
    }

    public static void init(Activity activity, int realHeight, boolean forceInit) {
        if (forceInit || screenHeight == 0 || screenWidth == 0) {
            lanScape = false;
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                statusBarHeight = activity.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                LogUtils.e(e);
            }
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            if (realHeight == 0
                    || realHeight * 9 < 16 * screenWidth//分屏时比例一般小于16:9
            ) {
                screenHeight = metrics.heightPixels + getMiSupplementHeight(activity);
                lanScape = true;
            } else {
                screenHeight = realHeight;
            }
            yScalePercent = (float) screenHeight / baseHeight;
            scalePercent = (float) screenWidth / baseWidth;
        }
        int NavigationBar = 0;
        try {
            if (checkDeviceHasNavigationBar(activity)) {
                NavigationBar = getNavigationBarHeight(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(SCREEN_LAYOUT_HEIGHT, "realHeight:" + realHeight + "  screenHeight:" + screenHeight + "  statusBarHeight:" + statusBarHeight + "   " + SPUtils.getInstance().getInt(SCREEN_LAYOUT_HEIGHT, 0) + "   NavigationBar " + NavigationBar);
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            //do something
        }
        return hasNavigationBar;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int getScaleValue(float value) {
        if (lanScape && (int) value != getBaseWidth()) {
            return (int) (value * yScalePercent);
        }
        return (int) (value * scalePercent);
    }

    public static int getScaleY(float y) {
        return (int) (y * yScalePercent);
    }

    public static int getScaleX(float x) {
        return (int) (x * scalePercent);
    }

    public static int px2dp(Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getStatusBarHeight() {
        return statusBarHeight;
    }

    /**
     * px转换成sp
     */
    public int px2sp(Context context,float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    //获取屏幕宽度(px)
    public static int getScreenWidth() {
        return screenWidth;
    }

    //获得屏幕高度（px）
    public static int getScreenHeight() {
        return screenHeight;
    }

    //获取屏幕宽度(px)
    public static int getBaseWidth() {
        return baseWidth;
    }

    //获得屏幕高度（px）
    public static int getBaseHeight() {
        return baseHeight;
    }

    public static int getMiSupplementHeight(Context context) {
        int result = 0;
        //是否是小米系统，不是小米系统则不需要补充高度
        if (isMIUI()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) == 0) {
                //如果虚拟按键已经显示，则不需要补充高度
            } else {
                //如果虚拟按键没有显示，则需要补充虚拟按键高度到屏幕高度
                Resources res = context.getResources();
                int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    result = res.getDimensionPixelSize(resourceId);
                }
            }
        }
        return result;
    }

    private static boolean isMIUI() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        return manufacturer.equals("xiaomi");
    }

    public static boolean TVMode(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }
}
