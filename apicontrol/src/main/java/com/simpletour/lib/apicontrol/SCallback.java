package com.simpletour.lib.apicontrol;

import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * 包名：com.simpletour.lib.apicontrol
 * 描述：服务请求回调
 * 创建者：yankebin
 * 日期：2017/5/22
 */

public abstract class SCallback<T> implements Callback<T> {
    protected boolean isCanceled;
    protected ContextHolder<Object> contextHolder;

    public SCallback(@NonNull Object host) {
        contextHolder = new ContextHolder<>(host);
    }

    /**
     * 检测是否符合回调服务器返回数据的条件
     *
     * @param call
     * @return
     */
    protected boolean checkCanceled(Call call) {
        return isCanceled || call.isCanceled() || !contextHolder.isAlive();
    }


    private void cancel() {
        isCanceled = true;
    }

    /**
     * 请求数据失败
     *
     * @param t
     */
    public abstract void success(T t);

    /**
     * 请求数据成功
     *
     * @param errorMessage
     */
    public abstract void failure(String errorMessage);
}
