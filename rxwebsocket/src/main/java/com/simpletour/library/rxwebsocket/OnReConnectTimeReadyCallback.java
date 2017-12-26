package com.simpletour.library.rxwebsocket;

import rx.Subscriber;

/**
 * 包名：com.simpletour.library.rxwebsocket
 * 描述：重连时间计算好的回调
 * 创建者：yankebin
 * 日期：2017/11/29
 */

public interface OnReConnectTimeReadyCallback {
    void onReConnectTimeReady(long time,String url, Subscriber<? super WebSocketInfo> subscriber);
}
