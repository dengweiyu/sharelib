package com.gdiwing.baselib

import android.app.Application
import android.content.Context
//import leakcanary.LeakCanary

abstract class TBaseApplication : Application(), TBaseApplicationImp {
    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }
}