package com.simpletour.library.rxwebsocket;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import okhttp3.WebSocket;
import okio.ByteString;
import rx.Subscriber;

/**
 * 包名：com.simpletour.library.rxwebsocket
 * 描述：WebSocketSubscriber
 * 创建者：yankebin
 * 日期：2017/11/29
 */

public abstract class WebSocketSubscriber extends Subscriber<WebSocketInfo> {

    @CallSuper
    @Override
    public final void onNext(@NonNull WebSocketInfo webSocketInfo) {
        if (webSocketInfo.isOnOpen()) {
            onOpen(webSocketInfo.getWebSocket());
        } else if (!TextUtils.isEmpty(webSocketInfo.getString())) {
            onMessage(webSocketInfo.getString());
        } else if (null != webSocketInfo.getByteString()) {
            onMessage(webSocketInfo.getByteString());
        } else if (!TextUtils.isEmpty(webSocketInfo.getClosedReason())) {
            onClose(webSocketInfo.getClosedReason());
        }
    }

    public abstract void onOpen(@NonNull WebSocket webSocket);

    public abstract void onMessage(@NonNull String text);

    public abstract void onMessage(@NonNull ByteString bytes);

    public abstract void onClose(@NonNull String reason);

    public abstract void onError(@NonNull String error);

    @Override
    public final void onCompleted() {

    }

    @Override
    public final void onError(Throwable e) {
       e.printStackTrace();
    }
}
