package com.simpletour.library.caocao.heart.base;

import com.simpletour.library.caocao.heart.enums.AGClientStateEnum;

/**
 * 包名：com.simpletour.library.caocao.heart.base
 * 描述：客户端活动状态改变回调接口
 * 创建者：yankebin
 * 日期：2017/11/30
 */

public interface IAGClientStateChangeCallback {

    void onClientStateChange(AGClientStateEnum clientStateEnum);

}
