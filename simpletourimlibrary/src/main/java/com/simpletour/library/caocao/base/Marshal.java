package com.simpletour.library.caocao.base;
/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：消息模型解析接口
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public interface Marshal {
    void decode(int filedId, Object value);
}