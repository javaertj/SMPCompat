package com.simpletour.library.caocao.base;

import com.simpletour.library.caocao.model.AGSendMessageModel;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：消息发送状态回调器
 * 创建者：yankebin
 * 日期：2017/5/12
 */

public interface IAGMessageSendStatusCallback {
    void onFailure(AGSendMessageModel sendMessage);
    void onRetry(int retryCount, AGSendMessageModel sendMessage);
}
