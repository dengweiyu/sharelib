package com.gdiwing.baselib.utils;


import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;



/**
 * Created by Rollin on 2015/2/7.
 */
public class ScreenUtil {

    private static int status_bar_heigth = -1;

    public static int getHeightPixels(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay(context).getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getWidthPixels(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay(context).getMetrics(metrics);
        return metrics.widthPixels;
    }

    private static Display getDisplay(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay();
    }

    public static int dpToPx(Context context,float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int pxToDp(Context context,float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static int getStateBarHeight(Resources resources) {
        if (status_bar_heigth < 0) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0, sbar = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                sbar = resources.getDimensionPixelSize(x);
                status_bar_heigth = sbar;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status_bar_heigth;
    }

    public static int getNavigationBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
