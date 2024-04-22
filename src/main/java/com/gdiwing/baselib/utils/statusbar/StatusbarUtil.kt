package com.gdiwing.baselib.utils.statusbar

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt
import com.gdiwing.baselib.utils.statusbar.StatusbarUtil.lightMode
import com.gdiwing.baselib.utils.statusbar.StatusbarUtil.setStatusBarTransparent

/**
 * 仅在Android版本6.0以上进行StatusBar适配，因为低于6.0的设备一是不多，二是不能全部使用白底黑字，所以不需要过于复杂的适配
 * 6.0以下，请配置其他的Theme
 *
 * 如果仅在Activity中使用，只需要调用 [lightMode] 方法，并在布局中设置 fitsSystemWindow=true 即可
 *
 * 当需要在Activity+多Fragment场景下使用时，如果需要动态修改颜色，需要按照以下方式使用：
 * 1. 在Activity setContentView后依次调用 [setStatusBarTransparent] [lightMode] 方法
 * 2. 在每个Fragment的布局中增加一个View，用于占位，例如
 * <View
 *      android:id="@+id/view1"
 *      android:layout_width="match_parent"
 *      android:layout_height="0dp"
 *      android:background="@color/colorPrimaryDark" />
 * 3. 在Fragment的 onViewCreated 方法中，为以上View设置高度和背景颜色：
 *    view1.layoutParams.height = context?.let { StatusBarUtils.getStatusBarHeight(it) } ?: 0
 *    view1.setBackgroundColor(Color.RED)
 * 4. 在Activity中的ViewPager切换事件中，还可以通过调用 [lightMode] 方法来跟随Fragment切换动态调整Statusbar的文本颜色
 */
object StatusbarUtil {
    /**
     * 6.0以下统统返回0
     */
    @JvmStatic
    fun getStatusBarHeightOnlyFromM(context: Context): Int {
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                return context.resources.getDimensionPixelSize(resourceId)
            }
        return 0
    }

    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return context.resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    fun setStatusBarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun setStatusBarBgColor(activity: Activity, @ColorInt color: Int) {
//        if (isFromM()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = color
        }
    }

    fun getStatusBarBgColor(activity: Activity) : Int{
        var color :Int =-1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            color = activity.window.statusBarColor
        }
        return color
    }

    fun setStatusBarIconColor(activity: Activity, blackIcon: Boolean) {
        var option = 0;
        if (blackIcon) {
            option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  //白底黑字
        } else {
            option = View.SYSTEM_UI_FLAG_VISIBLE;     //默认，黑底白字
        }
        activity.window.decorView.systemUiVisibility = option
    }

    //全屏，显示状态栏，控制状态栏字体颜色
    /**
     *  是否全屏 状态栏浮于上面
     *  blackIcon 黑色图标
     */
    fun setStatusBarFullScreen(activity: Activity, isFull: Boolean, blackIcon: Boolean) {
        val decorView = activity.window.decorView
        var option = 0;
        if (isFull) {
            if (blackIcon) {
                option =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                option =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_VISIBLE
            }
        } else {
            if (blackIcon) {
                option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                option = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
        decorView.systemUiVisibility = option
    }

    fun lightMode(activity: Activity, dark: Boolean) {
        if (isFromM()) {
            try {
                if (!MIUILightMode(activity, dark) && !FlymeLightMode(activity, dark)) {
                    SystemLightMode(activity, dark)
                }
            } catch (ignored: Exception) {
            }
        }
    }

    private fun isFromM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun SystemLightMode(activity: Activity, dark: Boolean) {
        activity.window.decorView.systemUiVisibility = when {
            dark -> View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else -> View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    private fun FlymeLightMode(activity: Activity, dark: Boolean): Boolean {
        var success = false
        val window = activity.window
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                value = if (dark) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                success = true
            } catch (ignored: Exception) {
            }

        }
        return success
    }

    @SuppressLint("PrivateApi")
    private fun MIUILightMode(activity: Activity, dark: Boolean): Boolean {
        var success = false
        val window = activity.window
        if (window != null) {
            val clazz = window.javaClass
            try {
                val darkModeFlag: Int
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    SystemLightMode(activity, dark)
                }

                success = true
            } catch (ignored: Exception) {
            }

        }
        return success
    }


}