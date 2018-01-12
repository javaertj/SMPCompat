package com.simpletour.lib.autoupdate;

import com.simpletour.lib.autoupdate.model.UpdateBean;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：开始检查更新回调接口
 * 创建者：yankebin
 * 日期：2018/1/12
 */

public interface OnCheckUpdateStartCallback {
    void onCheckUpdateStart(UCallback<UpdateBean> call);
}
