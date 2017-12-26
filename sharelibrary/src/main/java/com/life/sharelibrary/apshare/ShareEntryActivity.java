package com.life.sharelibrary.apshare;

import android.content.Intent;
import android.os.Bundle;

import com.alipay.share.sdk.openapi.BaseReq;
import com.alipay.share.sdk.openapi.BaseResp;
import com.umeng.socialize.media.ShareCallbackActivity;

/**
 * Created by simpletour on 2016/2/18.
 *  支付宝
 */
public class ShareEntryActivity extends ShareCallbackActivity{

    public ShareEntryActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        super.onReq(baseReq);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        super.onResp(baseResp);
    }
}
