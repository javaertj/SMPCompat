package com.simpletour.lib.apicontrol;


import android.support.annotation.NonNull;

import rx.Subscriber;

/**
 * 包名：com.simpletour.lib.apicontrol
 * 描述：retrofit+RxJava异步请求返回结果抽象类
 * 创建者：yankebin
 * 日期：2017/7/20
 */
public abstract class SSubscriber<T> extends Subscriber<T> {
    protected boolean isCanceled;
    protected ContextHolder<Object> contextHolder;

    public SSubscriber(@NonNull Object host) {
        contextHolder = new ContextHolder<>(host);
    }

    /**
     * 检测是否符合回调服务器返回数据的条件
     *
     * @return
     */
    protected boolean checkCanceled() {
        return isCanceled || !contextHolder.isAlive();
    }


    private void cancel() {
        isCanceled = true;
    }

    public abstract void success(T bean);

    public abstract void failure(Throwable e);
}