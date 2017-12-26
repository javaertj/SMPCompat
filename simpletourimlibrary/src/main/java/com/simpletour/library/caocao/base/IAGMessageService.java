package com.simpletour.library.caocao.base;



/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：消息服务接口
 * 创建者：yankebin
 * 日期：2017/5/17
 */

public interface IAGMessageService {

    /**
     * 加入一个消息回调器
     *
     * @param agCallback 消息回调器
     */
    void registerMessageCallback(IAGOnReceiveChatMessageCallback agCallback);

    /**
     * 加入一个消息回调器
     *
     * @param agCallback 消息回调器
     */
    void unRegisterMessageCallback(IAGOnReceiveChatMessageCallback agCallback);

    void removeMessages(IAGCallback<Void> callback, String conversationId, Long... messageIds);

    void removeLocalMessages(IAGCallback<Void> callback, String conversationId, Long... messageIds);

    void setMessageProxy(IAGMessageProxy proxy);
}
