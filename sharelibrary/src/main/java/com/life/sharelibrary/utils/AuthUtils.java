package com.life.sharelibrary.utils;

import android.app.Activity;


import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;


/**
 * Created by simpletour on 2016/2/18.
 * 授权/解除授权
 */
public class AuthUtils {

    /**
     * 授权登录.注意发起授权的activity要注意重写onActivityResult()，UMShareAPI.onActivityResult(requestCode, resultCode, data);
     * 调用的原因不明确，umeng官方文档未见原因描述
     *
     * @param api      UMShareAPI实例(单例模式)
     * @param act      activity实例
     * @param type     必须是SHARE_MEDIA,表示授权的平台
     * @param listener 用于处理授权结果
     **/
    public static boolean auth(UMShareAPI api, Activity act, SHARE_MEDIA type, UMAuthListener listener) {
        if (api == null || act == null || listener == null)
            return false;
        try {
            api.doOauthVerify(act, type, listener);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 撤销授权.注意撤销授权的activity要注意重写onActivityResult()，UMShareAPI.onActivityResult(requestCode, resultCode, data);
     * 调用的原因不明确，umeng官方文档未见原因描述
     *
     * @param api      UMShareAPI实例(单例模式)
     * @param act      activity实例
     * @param type     必须是SHARE_MEDIA
     * @param listener 用于处理授权结果
     **/
    public static boolean deleteAuth(UMShareAPI api, Activity act, SHARE_MEDIA type, UMAuthListener listener) {
        if (api == null || act == null || listener == null)
            return false;
        try {
            api.deleteOauth(act, type, listener);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
