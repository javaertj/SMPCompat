package com.life.sharelibrary.wxapi;

import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * Created by simpletour on 2016/2/18.
 * 微信
 */
public class WXEntryActivity extends WXCallbackActivity {
    public WXEntryActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
    }

    @Override
    public void onReq(BaseReq req) {
        super.onReq(req);
    }
}
