package com.gdiwing.baselib.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.blankj.utilcode.util.ToastUtils
import com.gdiwing.baselib.R

object Utils {

    /**
     * 忽略电池优化
     */
    fun isIgnoreBatteryOptimization(activity: Context): Boolean {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        var hasIgnored = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.packageName)
            return hasIgnored
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        }
        return !hasIgnored
    }

    /**
     * 忽略电池优化
     */
    fun ignoreBatteryOptimization(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        var hasIgnored = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(context.packageName)
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if (!hasIgnored) {
                //未加入电池优化的白名单 则弹出系统弹窗供用户选择(这个弹窗也是一个页面)
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            } else {
                //已加入电池优化的白名单 则进入系统电池优化页面
                val powerUsageIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                val resolveInfo = context.packageManager.resolveActivity(powerUsageIntent, 0)
                //判断系统是否有这个页面
                if (resolveInfo != null) {
                    if (resolveInfo != null) {
                        context.startActivity(powerUsageIntent)
                    }
                }
            }
        }
    }

    fun tryJumpSettingPage(mContext: Context) {
        val intent = Intent()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, mContext.packageName)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                intent.putExtra("app_package", mContext.packageName)
                intent.putExtra("app_uid", mContext.applicationInfo.uid)
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.setData(Uri.parse("package:" + mContext.packageName))
            }
            mContext.startActivity(intent)
        } catch (e: Exception) {
            try {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", mContext.packageName, null)
                intent.setData(uri)
                mContext.startActivity(intent)
            }catch (e :Exception){
                ToastUtils.showShort(R.string.open_notification_setting_failed)
            }

        }
    }
}