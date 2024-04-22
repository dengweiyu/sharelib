package com.gdiwing.baselib.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.gdiwing.baselib.R
import com.gdiwing.baselib.dialog.BaseBottomSheetDialog
import com.gdiwing.baselib.utils.ScreenUtil
import com.gdiwing.baselib.utils.hookStar
import com.gdiwing.baselib.utils.onTrackPageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class BaseBottomDialogFragment<T : ViewDataBinding> : BottomSheetDialogFragment() {

    protected var disMissCallBack: ((Boolean) -> Unit)? = null
    protected var binding: T? = null
    protected var dpiRatio: Float = 0f
    protected var maxHeight: Float = 0f
    protected var touchOutSide: Boolean = false
    protected var trackMap : HashMap<String, String?> ?=null
    protected var styleRes : Int = R.style.ShareDialogFragment
    abstract fun onPageTrack(): HashMap<String, String?>?
    abstract fun pageType(): String?

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, styleRes)
        maxHeight =
            (ScreenUtils.getScreenHeight() - requireContext().resources.getDimensionPixelOffset(R.dimen.sw_px_40) - ScreenUtil.getNavigationBarHeight(requireActivity().resources)).toFloat()
        val dialog = object : BaseBottomSheetDialog(requireContext(), theme, maxHeight.toInt()) {
            override fun dismiss() {
                KeyboardUtils.hideSoftInput(requireView())
                disMissCallBack?.invoke(false)
                super.dismiss()
            }
        }.apply {
            setCanceledOnTouchOutside(touchOutSide)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setWhiteNavigationBar(dialog,Color.WHITE)
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dm = requireActivity().getResources().getDisplayMetrics()
        dm?.let { d ->
            dpiRatio = d.heightPixels.toFloat() / d.widthPixels
        }
        onPageTrack()?.let {
            trackMap = onTrackPageView(it.apply {

            })
        }
    }

    override fun onStart() {
        super.onStart()

    }

    abstract fun getLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding?.lifecycleOwner = this.viewLifecycleOwner
        if (binding != null) {
            return binding?.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        hookStar()
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
     fun setWhiteNavigationBar(@NonNull dialog: Dialog,color : Int) {
        val window: Window? = dialog.window
        if (window != null) {
            val metrics = DisplayMetrics()
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics)
            val dimDrawable = GradientDrawable()
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(color) //这里设置颜色
            val layers: Array<Drawable> = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
        }
    }

}