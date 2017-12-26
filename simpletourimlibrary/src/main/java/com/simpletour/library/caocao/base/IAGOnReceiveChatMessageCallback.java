package com.simpletour.library.caocao.base;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：收到服务器返回消息的回调
 * 创建者：yankebin
 * 日期：2017/5/12
 */

public interface IAGOnReceiveChatMessageCallback {

    void onReceiveMessage(IAGMessage message);

}
