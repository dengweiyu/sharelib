package com.gdiwing.baselib.net

import android.app.Application
import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.gdiwing.baselib.TBaseApplication
import com.gdiwing.baselib.net.callback.BaseApiService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson().apply {

        }
    }

    @Provides
    @Singleton
    fun provideConfig(gson: Gson, application: Application): NetworkConfig {
        return NetworkConfig().apply {
            baseUrl = (application as TBaseApplication?)?.getBaseUrl() ?: ""
            Log.d("NetModule","baseUrl ${baseUrl}")
            readTimeout = 60000
            connectTimeout = 60000
            writeTimeout = 60000
            addInterceptor(HeaderInterceptor())
            addInterceptor(StatusCodeInterceptor())
            //日志显示级别
            val level: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
            //新建log拦截器
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d("NetworkConfig", "OkHttp====Message:$message")
            }
            loggingInterceptor.level = level
            //OkHttp进行添加拦截器loggingInterceptor
            addNetworkInterceptor(StethoInterceptor())
            addConverterFactory(CustomizeGsonConverterFactory.create(gson))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            addInterceptor(loggingInterceptor)
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(config: NetworkConfig): Retrofit {
        val builder = OkHttpClient.Builder()
            .readTimeout(config.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.MILLISECONDS)
            .connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(config.isRetryOnConnectionFailure)
            .cache(config.cache)
            .proxy(config.proxy)
        if (config.cookieJar != null) {
            builder.cookieJar(config.cookieJar)
        }
        if (config.eventListener != null) {
            builder.eventListener(config.eventListener)
        }
        builder.interceptors().addAll(config.interceptors)
        builder.networkInterceptors().addAll(config.networkInterceptors)
        val mOkHttpClient = builder.build()
        val b = Retrofit.Builder()
            .client(mOkHttpClient)
            .baseUrl(config.baseUrl)
        b.converterFactories().addAll(config.converterFactories)
        b.callAdapterFactories().addAll(config.callAdapterFactories)
        return b.build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): BaseApiService {
        return retrofit.create(BaseApiService::class.java)
    }

}