package com.simpletour.lib.autoupdate;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：更新回调
 * 创建者：yankebin
 * 日期：2017/5/22
 */

public interface  UCallback<T> {
    void onSuccess(T t);

    void onFailure(String errorMessage);
}
