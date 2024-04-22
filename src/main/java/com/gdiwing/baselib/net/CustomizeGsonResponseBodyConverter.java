package com.gdiwing.baselib.net;

import android.util.Log;

import androidx.annotation.NonNull;

import com.gdiwing.baselib.net.callback.ApiResponse;
import com.gdiwing.baselib.utils.CheckUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by LtLei on 2019/6/2.
 * Email: fighting_our_life@foxmail.com
 * <p>
 * Description: CustomizeGsonResponseBodyConverter
 */
public class CustomizeGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final String TAG = CustomizeGsonResponseBodyConverter.class.getSimpleName();
    private final Gson mGson;
    private final TypeToken<T> mTypeToken;

    CustomizeGsonResponseBodyConverter(@NonNull final Gson gson, @NonNull final TypeToken<T> typeToken) {
        mGson = CheckUtils.checkNotNull(gson, "gson can not be null.");
        mTypeToken = CheckUtils.checkNotNull(typeToken, "typeToken can not be null.");
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        String response = value.string();
        Log.i(TAG, "convert: original response is : " + response);
        Log.e(TAG, "json数据最外一层在这儿解析:" + response);

        ApiResponse<T> apiResponse = null;
        ParameterizedType parameterizedType = new ParameterizedTypeImpl(null, BaseDefaultAPIResponse.class, mTypeToken.getType());
        try {
            apiResponse = mGson.fromJson(response, parameterizedType);
        } catch (Exception e) {
            try {
                return mGson.fromJson(response, mTypeToken.getType());
            } catch (Exception error) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject newEntity = new JSONObject();
                    newEntity.put("code", 200);
                    newEntity.put("message", "");
                    newEntity.put("data", jsonObject);
                    response = newEntity.toString();
                    return mGson.fromJson(response, mTypeToken.getType());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        } finally {
            value.close();
        }

        Log.i(TAG, "convert: convert response is : " + (apiResponse == null ? "" : apiResponse.toString()));

        if (apiResponse != null && !apiResponse.isSuccess()) {
            throw new IOException("apiResponse failed " + apiResponse.getCode() + " " + apiResponse.getMessage());
        }

        if (apiResponse == null || apiResponse.getData() == null) {
            throw new IOException("apiResponse null || apiResponse data null");
        }

        return apiResponse.getData();
    }
}
