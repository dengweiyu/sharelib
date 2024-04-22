package com.gdiwing.baselib.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.gdiwing.baselib.utils.hookStar
import com.gdiwing.baselib.utils.onTrackPageView
import com.gdiwing.baselib.utils.permission.PermissionsManager
import com.gdiwing.baselib.utils.permission.PermissionsResultAction

abstract class TBaseFragment<T : ViewDataBinding> : Fragment() {
    abstract fun getLayoutID(): Int
    abstract fun onPageTrack(): HashMap<String, String?>?
    abstract fun pageType(): String?
    protected var trackMap : HashMap<String, String?> ?=null
    protected lateinit var dataBinding: T

    fun getThisTrackMap(): HashMap<String, String?> ?= trackMap




    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onPageTrack()?.let {
            trackMap = onTrackPageView(it.apply {

            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,getLayoutID(),container,false)
        dataBinding.lifecycleOwner = this.viewLifecycleOwner
        return dataBinding.root
    }

    override fun onResume() {
        dataBinding.lifecycleOwner = this.viewLifecycleOwner
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hookStar()
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