package com.gdiwing.baselib.activity

import androidx.databinding.ViewDataBinding
import com.gdiwing.baselib.MainActivityInterface

abstract class TMainActivity<T : ViewDataBinding> : TBaseActivity<T>(), MainActivityInterface {

}