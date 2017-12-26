package com.simpletour.lib.autoupdate;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：用户取消更新操作回调
 * 创建者：yankebin
 * 日期：2017/5/22
 */

public interface UpdateCancelListener {

    void onCancel(boolean isForceUpdate);
}
