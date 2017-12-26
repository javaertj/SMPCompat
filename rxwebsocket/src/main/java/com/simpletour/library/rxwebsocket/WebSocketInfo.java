package com.simpletour.library.rxwebsocket;

import android.support.annotation.Nullable;

import okhttp3.WebSocket;
import okio.ByteString;

/**
 * 包名：com.simpletour.library.rxwebsocket
 * 描述：封装webSocket信息
 * 创建者：yankebin
 * 日期：2017/11/29
 */

public class WebSocketInfo {
    private WebSocket mWebSocket;
    private String mString;
    private ByteString mByteString;
    private boolean onOpen;
    private String closedReason;

    public WebSocketInfo() {
    }

    public WebSocketInfo(boolean onOpen) {
        this.onOpen = onOpen;
    }

    public WebSocketInfo(WebSocket webSocket, boolean onOpen) {
        mWebSocket = webSocket;
        this.onOpen = onOpen;
    }

    public WebSocketInfo(WebSocket webSocket, String mString) {
        mWebSocket = webSocket;
        this.mString = mString;
    }

    public WebSocketInfo(WebSocket webSocket, ByteString byteString) {
        mWebSocket = webSocket;
        mByteString = byteString;
    }

    public WebSocketInfo(String closedReason,WebSocket webSocket) {
        mWebSocket = webSocket;
        this.closedReason = closedReason;
    }

    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        mWebSocket = webSocket;
    }

    @Nullable
    public String getString() {
        return mString;
    }

    public void setString(String string) {
        this.mString = string;
    }

    @Nullable
    public ByteString getByteString() {
        return mByteString;
    }

    public void setByteString(ByteString byteString) {
        mByteString = byteString;
    }

    @Nullable
    public String getClosedReason() {
        return closedReason;
    }

    public void setClosedReason(String closedReason) {
        this.closedReason = closedReason;
    }

    public boolean isOnOpen() {
        return onOpen;
    }
}
