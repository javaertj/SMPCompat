package com.simpletour.lib.apicontrol.encrypt;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：加密接口
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public interface EncryptInter {

    /**
     * 请求参数加密key
     **/
    String APPLICATION_KEY = "@S#I$M*P$T#";
    String STR_CONN = "&";
    String STR_ASSIGN = "=";
    String STR_ASK = "?";
    String STR_TOKEN = "token";
    String STR_KEY = "key";
    String STR_TIMESTAP = "timeStamp";

    String getEncrypt(String url, String... param);

    EncryptType getType();
}