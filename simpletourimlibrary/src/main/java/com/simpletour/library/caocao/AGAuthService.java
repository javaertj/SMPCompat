package com.simpletour.library.caocao;


import android.text.TextUtils;

import com.simpletour.library.caocao.base.IAGAuthService;
import com.simpletour.library.caocao.base.IAGCallback;
import com.simpletour.library.caocao.model.AGAuthInfo;

/**
 * 包名：com.simpletour.library.caocao
 * 描述：鉴权服务
 * 创建者：yankebin
 * 日期：2017/5/5
 */

public final class AGAuthService implements IAGAuthService {
    private AGAuthInfo authInfo = new AGAuthInfo();

    private AGAuthService() {
    }

    AGAuthInfo getAuthInfo() {
        return authInfo;
    }

    public boolean isLogin() {
        return null != authInfo && authInfo.openId > 0L && !TextUtils.isEmpty(authInfo.token);
    }

    @Override
    public void setAuthInfo(long openId, String token, String nickName) {
        authInfo.openId = openId;
        authInfo.token = token;
        authInfo.nickName = nickName;
    }

    @Override
    public void clearAuthInfo() {
        authInfo.openId = -1;
        authInfo.token = "";
        authInfo.nickName = "";
    }

    @Override
    public void destroy() {
        clearAuthInfo();
        authInfo = null;
    }

    public static AGAuthService getInstance() {
        return AGAuthServiceInstance.instance;
    }

    @Override
    public void login(String account, String password, IAGCallback<AGAuthInfo> callback) {
//        AGClientEngine.getInstance().login(account, password, callback);
    }

    @Override
    public void login(String token, IAGCallback<AGAuthInfo> callback) {
//        AGClientEngine.getInstance().login(token, callback);
    }

    @Override
    public void logOut(String token, IAGCallback<Void> callback) {
//        AGClientEngine.getInstance().logOut(token, callback);
    }

    @Override
    public void getSmsCode(String mobile, IAGCallback<Void> callback) {
//        AGClientEngine.getInstance().getSmsCode(mobile, callback);

    }

    @Override
    public void register(String account, String password, String confirmPassword, String code, IAGCallback<AGAuthInfo> callback) {
//        AGClientEngine.getInstance().register(account, password, confirmPassword, code, callback);

    }

    private static class AGAuthServiceInstance {
        private static AGAuthService instance = new AGAuthService();
    }
}
