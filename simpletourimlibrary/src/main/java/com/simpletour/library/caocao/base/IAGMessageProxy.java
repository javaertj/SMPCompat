package com.simpletour.library.caocao.base;

import com.simpletour.library.caocao.AGExtendedMessage;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：消息代理，可实现消息扩展
 * 创建者：yankebin
 * 日期：2017/5/17
 */
public abstract class IAGMessageProxy {
    public IAGMessageProxy() {
    }

    public abstract AGExtendedMessage newInstance();
}