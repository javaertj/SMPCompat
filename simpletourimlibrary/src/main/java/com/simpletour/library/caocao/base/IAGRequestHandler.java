package com.simpletour.library.caocao.base;

import android.os.Handler;
import android.os.Looper;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：发送消息请求的回调
 * 创建者：yankebin
 * 日期：2017/5/19
 */

public abstract class IAGRequestHandler<V, T> {
    protected static Handler handler = new Handler(Looper.getMainLooper());
    private IAGCallback<T> callback;
    private Runnable mRunner;


    public IAGRequestHandler(IAGCallback<T> callback) {
        this(callback, 30000L);
    }

    public IAGRequestHandler(IAGCallback<T> callback, long localTimeOut) {
        this.callback = callback;
        if (localTimeOut > 0) {
            mRunner = new Runnable() {
                @Override
                public void run() {
                    onError("4008", "REQUEST_TIME_OUT_ERR");
                }
            };
            handler.postDelayed(mRunner, localTimeOut);
        }
    }


    public abstract T convert(V v);

    public void onError(String errorCode, String reason) {
        handler.removeCallbacks(mRunner);
        if (null != callback) {
            callback.onError(errorCode, reason);
        }
    }

    public void onSuccess(V v) {
        handler.removeCallbacks(mRunner);
        if (null != callback) {
            callback.onSuccess(convert(v));
        }
    }
}
