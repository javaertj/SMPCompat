package com.life.sharelibrary.yxapi;

import com.umeng.socialize.activity.YXCallbackActivity;
import com.umeng.socialize.handler.UMYXHandler;

import im.yixin.sdk.api.BaseReq;
import im.yixin.sdk.api.BaseResp;
import im.yixin.sdk.api.IYXAPI;

/**
 * Created by simpletour on 2016/2/18.
 * 易信
 */
public class YXEntryActivity extends YXCallbackActivity{
    public YXEntryActivity() {
        super();
    }

    @Override
    public void onReq(BaseReq req) {
        super.onReq(req);
    }

    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
    }

    @Override
    protected IYXAPI getIYXAPI() {
        return super.getIYXAPI();
    }

    @Override
    protected UMYXHandler getHandler() {
        return super.getHandler();
    }
}
