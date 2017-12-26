package com.simpletour.library.rxwebsocket;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import okhttp3.WebSocket;
import okio.ByteString;
import rx.functions.Action1;


/**
 * 包名：com.simpletour.library.rxwebsocket
 * 描述：websocketRxAction
 * 创建者：yankebin
 * 日期：2017/11/29
 */
public abstract class WebSokcetAction1 implements Action1<WebSocketInfo> {
    @CallSuper
    @Override
    public void call(WebSocketInfo webSocketInfo) {
        if (webSocketInfo.isOnOpen()) {
            onOpen(webSocketInfo.getWebSocket());
        } else if (webSocketInfo.getString() != null) {
            onMessage(webSocketInfo.getString());
        } else if (webSocketInfo.getByteString() != null) {
            onMessage(webSocketInfo.getByteString());
        }
    }

    public abstract void onOpen(@NonNull WebSocket webSocket);

    public abstract void onMessage(@NonNull String text);

    public abstract void onMessage(@NonNull ByteString bytes);
}
