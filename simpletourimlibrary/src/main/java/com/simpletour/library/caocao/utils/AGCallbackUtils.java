package com.simpletour.library.caocao.utils;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;

import com.simpletour.library.caocao.base.IAGCallback;


/**
 * 包名：com.simpletour.library.caocao.utils
 * 描述：切换回调到主线程的工具
 * 创建者：yankebin
 * 日期：2017/5/17
 */
public class AGCallbackUtils {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public AGCallbackUtils() {
    }

    @TargetApi(3)
    public static void runOnUiThread(Runnable runnable) {
        if(Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }

    }

    public static void onError(final IAGCallback<?> callback, final String code, final String reason) {
        if(callback != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    callback.onError(code, reason);
                }
            });
        }

    }

    public static <T> void onSuccess(final IAGCallback<T> callback, final T object) {
        if(callback != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    callback.onSuccess(object);
                }
            });
        }
    }
}