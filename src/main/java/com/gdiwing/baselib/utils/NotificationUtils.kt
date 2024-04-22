package com.gdiwing.baselib.utils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.ToastUtils
import com.gdiwing.baselib.R
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * api33
 */
public object NotificationUtils {
    private const val CHECK_OP_NO_THROW = "checkOpNoThrow"
    private const val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

    //调用该方法获取是否开启通知栏权限
    fun isNotifyEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isEnableV26(context)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                isEnabledV19(context)
            } else {
                true
            }
        }
    }

    /**
     * 8.0以下判断
     *
     * @param context api19  4.4及以上判断
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun isEnabledV19(context: Context): Boolean {
        val mAppOps: AppOpsManager =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo: ApplicationInfo = context.getApplicationInfo()
        val pkg: String = context.getApplicationContext().getPackageName()
        val uid: Int = appInfo.uid
        val appOpsClass: Class<*>
        return try {
            appOpsClass = Class.forName(AppOpsManager::class.java.getName())
            val checkOpNoThrowMethod: Method = appOpsClass.getMethod(
                CHECK_OP_NO_THROW,
                Integer.TYPE, Integer.TYPE, String::class.java
            )
            val opPostNotificationValue: Field = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
            val value = opPostNotificationValue.get(Int::class.java) as Int
            checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) as Int ===
                    AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }


    /**
     * 8.0及以上通知权限判断
     *
     * @param context
     * @return
     */
    private fun isEnableV26(context: Context): Boolean {
        val appInfo: ApplicationInfo = context.getApplicationInfo()
        val pkg: String = context.getApplicationContext().getPackageName()
        val uid: Int = appInfo.uid
        try {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            @SuppressLint("DiscouragedPrivateApi") val sServiceField: Method =
                notificationManager.javaClass.getDeclaredMethod("getService")
            sServiceField.setAccessible(true)
            val sService: Any = sServiceField.invoke(notificationManager)
            var method: Method? = null
            if (sService != null) {
                method = sService.javaClass.getDeclaredMethod(
                    "areNotificationsEnabledForPackage",
                    String::class.java,
                    Integer.TYPE
                )
                method.setAccessible(true)
            }
            return (method?.invoke(sService, pkg, uid) ?: false) as Boolean
        } catch (e: Exception) {
            return true
        }
    }

}

/**
 * api<33
 */
public object NotificationUtil {
    /**
     * 系统层面通知开关有没有开启
     * Build.VERSION.SDK_INT >= 24
     * Build.VERSION.SDK_INT >= 19
     *
     * @param mContext
     * @return
     */
    fun checkNotifyPermission(mContext: Context): Boolean {
        val manager: NotificationManagerCompat = NotificationManagerCompat.from(mContext)
        return manager.areNotificationsEnabled()
    }


    /**
     * 如果通知未打开 跳转到通知设定界面
     * @param mContext
     */
    fun tryJumpNotifyPage(mContext: Context) {
        val intent = Intent()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
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