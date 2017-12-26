package com.simpletour.library.caocao.base;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：IM服务引擎
 * 创建者：yankebin
 * 日期：2017/5/5
 */

public interface IAGIMEngine {

     <T> T getService(Class<?> clazz);

}
