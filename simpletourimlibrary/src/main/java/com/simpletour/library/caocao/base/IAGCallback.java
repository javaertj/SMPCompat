package com.simpletour.library.caocao.base;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：通用回调接口
 * 创建者：yankebin
 * 日期：2017/5/8
 */

public interface IAGCallback<T> {
    void onSuccess(T t);

    void onError(String errorCode, String reason);

    void onProgress(int progress, T t);
}
