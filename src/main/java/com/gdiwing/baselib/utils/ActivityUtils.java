package com.gdiwing.baselib.utils;

import android.app.Activity;

import com.gdiwing.baselib.activity.TMainActivity;

import java.util.Iterator;
import java.util.Stack;



public class ActivityUtils {

    private static Stack<Activity> mActivityStack;

    public static boolean isMainActivityCreated(Class<Activity> activityClass ) {
        if (mActivityStack == null || mActivityStack.empty()) {
            return false;
        }
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            String classMainName = activityClass.getSimpleName();
            if (activity.getClass().getSimpleName().equals(classMainName)|| activity instanceof TMainActivity) {
                return true;
            }
        }

        return false;
    }

    public static Activity getMainActivity() {
        if (mActivityStack == null || mActivityStack.empty()) {
            return null;
        }
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity ac = iterator.next();
            if (ac instanceof TMainActivity) {
                return ac;
            }
        }
        return null;
    }


    /**
     * 添加一个Activity到堆栈中
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        if (null == mActivityStack) {
            mActivityStack = new Stack<>();
        }
        mActivityStack.add(activity);
    }

    /**
     * 从堆栈中移除指定的Activity
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        if (mActivityStack == null || mActivityStack.empty()) {
            return;
        }
        if (activity != null) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 从堆栈中找到Activity
     *
     * @param activity
     */
    public static Activity getLastActivityByName(Activity activity) {
        if (mActivityStack == null || mActivityStack.empty()) {
            return null;
        }
        Activity lastActivity = null;
        if (activity != null) {
//            Stack<Activity> tempActivityStack = new Stack<>();
            int count = 0;
            int size = mActivityStack.size()-1;
            for (Activity activity1 : mActivityStack){
                if (activity1.getLocalClassName().equals(activity.getLocalClassName())){
                    break;
                }
                lastActivity = activity1;
                count++;
            }
        }
        return lastActivity;
    }


    /**
     * 获取顶部的Activity
     *
     * @return
     */
    @androidx.annotation.Nullable
    public static Activity getTopActivity() {
        if (mActivityStack == null || mActivityStack.isEmpty()) {
            return null;
        } else {
            return mActivityStack.get(mActivityStack.size() - 1);
        }
    }

    @androidx.annotation.Nullable
    public static Activity getSecondActivity() {
        if (mActivityStack == null || mActivityStack.isEmpty() || mActivityStack.size() < 2) {
            return null;
        } else {
            return mActivityStack.get(mActivityStack.size() - 2);
        }
    }

    /**
     * 结束所有的Activity，退出应用
     */
    public static void removeAllActivity() {
        if (mActivityStack != null && mActivityStack.size() > 0) {
            for (Activity activity : mActivityStack) {
                activity.finish();
            }
        }
    }
}
