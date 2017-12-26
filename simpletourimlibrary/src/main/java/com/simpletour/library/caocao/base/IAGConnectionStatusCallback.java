package com.simpletour.library.caocao.base;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：长连接状态回调接口
 * 创建者：yankebin
 * 日期：2017/5/12
 */

public interface IAGConnectionStatusCallback {

    void onStatusChanged(AGConnectionStatus status, int errorCode, String reason);


    public enum  AGConnectionStatus{
        /**
         * 在线
         */
        ONLINE,
        /**
         * 关闭中
         */
        CLOSING,
        /**
         * 离线
         */
        OFF_LIEN,
        /**
         * 错误
         */
        ERROR;
    }
}
