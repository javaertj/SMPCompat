package com.simpletour.library.caocao.base;

import com.simpletour.library.caocao.model.AGHeartResponseMessageModel;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：心跳回应消息回调
 * 创建者：yankebin
 * 日期：2017/11/30
 */

public interface IAGOnReceiveHeartMessageCallback {
    void onReceiveHeartMessage(AGHeartResponseMessageModel messageModel);
}
