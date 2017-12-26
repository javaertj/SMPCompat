package com.simpletour.library.rxwebsocket;

import okhttp3.WebSocket;
import rx.Subscriber;

/**
 * 包名：com.simpletour.library.rxwebsocket
 * 描述：重连策略接口
 * 创建者：yankebin
 * 日期：2017/11/29
 */

public abstract class ReconnectStrategyAble {
    protected long mTime;
    protected final long mInitTime;
    protected OnReConnectTimeReadyCallback connectTimeReadyCallback;

    public ReconnectStrategyAble(long initTime) {
        this.mInitTime = initTime;
        this.mTime = initTime;
    }

    public void reset() {
        mTime = mInitTime;
    }

    public void setConnectTimeReadyCallback(OnReConnectTimeReadyCallback connectTimeReadyCallback) {
        this.connectTimeReadyCallback = connectTimeReadyCallback;
    }

    public void onReconnect(WebSocket socket, final String url, final Subscriber<? super WebSocketInfo> subscriber) {
        if (null != connectTimeReadyCallback) {
            connectTimeReadyCallback.onReConnectTimeReady(mTime, url, subscriber);
        }
        doingStrategy(socket);
    }

    public abstract void doingStrategy(WebSocket socket);
}
