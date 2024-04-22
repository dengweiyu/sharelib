package com.gdiwing.baselib.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected val _event = MutableLiveData<Event<Int>>()
    val event: LiveData<Event<Int>> = _event
    fun sendEvent(event:Int){
        _event.postValue(Event(event))
    }

    protected fun launchDefault(block:(CoroutineScope)->Unit) =
        viewModelScope.launch(Dispatchers.Default){
            block.invoke(this)
        }
}