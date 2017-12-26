package com.simpletour.lib.autoupdate;

import android.text.TextUtils;
import android.util.Log;

import com.simpletour.lib.apicontrol.RetrofitApi;
import com.simpletour.lib.apicontrol.SCallback;
import com.simpletour.lib.autoupdate.model.SResponseBean;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：
 * 创建者：yankebin
 * 日期：2017/5/22
 */

public abstract class UCallback<T extends SResponseBean> extends SCallback<T> {

    public UCallback(String key) {
        if (TextUtils.isEmpty(key)) {
            key = toString();
        }
        RetrofitApi.getInstance().putCall(key, this);
    }

    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        //请求失败
        if (!response.isSuccessful()) {
            onFailure(call, new IllegalArgumentException("Request data failed"));
            return;
        }
        T baseBen = response.body();
        //数据为空
        if (null == baseBen) {
            onFailure(call, new IllegalArgumentException("Invalid data returned by the server"));
            return;
        }
        //请求的发起者是否还合适接收回调
        if (isCanceled |= checkCanceled(call)) {
            return;
        }
        //回调数据
        success(baseBen);
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, "onFailure", t);
        if (isCanceled |= checkCanceled(call)) {
            return;
        }
        String errorMessage = Log.getStackTraceString(t);
        if (errorMessage.contains("failed to connect")) {
            errorMessage = "服务器连接失败！";
        } else if (errorMessage.contains("System internal error")) {
            errorMessage = "服务器错误！";
        } else if (errorMessage.contains("java.net.SocketTimeoutException")) {
            errorMessage = "服务器连接超时！";
        }else {
            errorMessage = "系统错误";
        }
        failure(errorMessage);
    }
}
