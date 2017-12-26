package com.simpletour.lib.autoupdate;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：下载回调
 * 创建者：yankebin
 * 日期：2016/5/18
 */
public interface DownLoadListener {
    void onDownLoading(int progress, int currentSize, int totalSize);

    void onDownLoadComplete();

    void onDownLoadError();
}
