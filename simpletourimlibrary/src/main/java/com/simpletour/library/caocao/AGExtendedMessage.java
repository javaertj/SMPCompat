package com.simpletour.library.caocao;
/**
 * 包名：com.againstalone.lib.imkit
 * 描述：扩展消息
 * 创建者：yankebin
 * 日期：2017/5/17
 */
public class AGExtendedMessage extends AGMessage {

    public AGExtendedMessage() {
    }

    protected final void doAfter() {
        this.onCreateMessage();
    }

    public void onCreateMessage() {
    }
}