package com.simpletour.library.caocao.model;

import java.io.Serializable;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：授权信息
 * 创建者：yankebin
 * 日期：2017/5/8
 */

public class AGAuthInfo implements Serializable {
    public long openId;
    public String token;
    public String domain;
    public String nickName;
    public String mobile;
    public AGAuthStatus status;

    public static enum AGAuthStatus {
        OFFLINE(0),
        ONLIEN(1);

        private int status;

        private AGAuthStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

}
