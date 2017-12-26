package com.simpletour.lib.autoupdate;


import com.simpletour.lib.autoupdate.model.UpdateResponse;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：更新回调
 * 创建者：yankebin
 * 日期：2016/5/18
 */
public interface UpdateListener {
    void onUpdateReturned(int updateStatus, UpdateResponse updateResponse);
}
