package com.life.sharelibrary;

import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.umeng.socialize.media.WBShareCallBackActivity;

/**
 * Created by simpletour on 2016/2/18.
 *
 * 新浪微博分享--（分享完成后，会触发到该activity） 已证实可以触发并回调过来
 */
public class WBShareActivity extends WBShareCallBackActivity {

    public WBShareActivity() {
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
    public void onResponse(BaseResponse baseResponse) {
        super.onResponse(baseResponse);
    }
}
