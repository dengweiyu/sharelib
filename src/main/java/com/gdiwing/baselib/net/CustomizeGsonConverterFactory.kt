package com.gdiwing.baselib.net

import com.gdiwing.baselib.utils.CheckUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class CustomizeGsonConverterFactory(gson: Gson) : Converter.Factory() {
    private val gson: Gson
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return CustomizeGsonResponseBodyConverter(gson, TypeToken.get(type))
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter)
    }

    companion object {
        fun create(gson: Gson): CustomizeGsonConverterFactory {
            return CustomizeGsonConverterFactory(gson)
        }
    }

    init {
        this.gson = CheckUtils.checkNotNull(gson, "gson can not be null.")
    }
}