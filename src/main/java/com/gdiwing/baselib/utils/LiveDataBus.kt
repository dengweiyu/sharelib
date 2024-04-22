package com.gdiwing.baselib.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gdiwing.baselib.viewmodel.Event

class LiveDataBus {
    companion object{
        val instance by lazy (mode = LazyThreadSafetyMode.SYNCHRONIZED){ LiveDataBus() }
    }

    protected val _event = MutableLiveData<Any>()

    fun getEvent() = _event

    fun post(any :Any) {
        this._event.postValue(any)
    }

}