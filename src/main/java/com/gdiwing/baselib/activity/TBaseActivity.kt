package com.gdiwing.baselib.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gdiwing.baselib.utils.ActivityUtils
import com.gdiwing.baselib.utils.hookStar
import com.gdiwing.baselib.utils.onTrackPageView
import com.gdiwing.baselib.utils.permission.PermissionsManager
import com.gdiwing.baselib.utils.permission.PermissionsResultAction
import com.gdiwing.baselib.utils.statusbar.StatusbarUtil.lightMode
import com.gdiwing.baselib.utils.statusbar.StatusbarUtil.setStatusBarFullScreen
import com.gdiwing.baselib.utils.statusbar.StatusbarUtil.setStatusBarTransparent

abstract class TBaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    private var isHasScreenShotListener = false
    private var path: String = ""
    protected var trackMap : HashMap<String,String?> ? =null
    abstract fun getLayoutID(): Int
    abstract fun onPageTrack(): HashMap<String, String?>?
    abstract fun pageType(): String?
    abstract fun initViews()
    abstract fun initIntent()


    private lateinit var dataBinding: T


    fun getBinding(): T = dataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.addActivity(this)
        onPageTrack()?.let {
            trackMap = onTrackPageView(it.apply {

            })
        }

        dataBinding = DataBindingUtil.setContentView(this, getLayoutID())
        lightMode(this, true)
        setStatusBarTransparent(this)
        setStatusBarFullScreen(this, isFull = true, blackIcon = true)
        dataBinding.lifecycleOwner = this
        initIntent()
        initViews()
    }

    override fun onDestroy() {
        dataBinding.unbind()
        ActivityUtils.removeActivity(this)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        hookStar()
    }

    override fun onPause() {
        super.onPause()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults)
    }

    protected fun requestPermissionsIfNecessary(
        block: (Boolean) -> Unit,
        arraylist: Array<String>
    ) {
        PermissionsManager.getInstance()
            .requestPermissionsIfNecessaryForResult(this, arraylist,
                object : PermissionsResultAction() {
                    override fun onGranted() {
                        block.invoke(true)
                    }

                    override fun onDenied(permission: String) {
                        block.invoke(false)
                    }
                })
    }
}