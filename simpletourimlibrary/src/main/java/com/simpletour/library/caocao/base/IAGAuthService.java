package com.simpletour.library.caocao.base;

import com.simpletour.library.caocao.model.AGAuthInfo;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：鉴权相关接口
 * 创建者：yankebin
 * 日期：2017/5/8
 */

public interface IAGAuthService {
    void clearAuthInfo();

    void destroy();

    void setAuthInfo(long openId, String token, String nickName);

    void login(String account, String password, IAGCallback<AGAuthInfo> callback);

    void login(String token, IAGCallback<AGAuthInfo> callback);

    void logOut(String token, IAGCallback<Void> callback);

    void getSmsCode(String mobile, IAGCallback<Void> callback);

    void register(String account, String password, String confirmPassword, String code, IAGCallback<AGAuthInfo> callback);
}
