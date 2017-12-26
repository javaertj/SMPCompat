package com.simpletour.lib.autoupdate;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：更新配置文件
 * 创建者：yankebin
 * 日期：2016/5/19
 */
public final class UpdateConfig {
    /**
     * 传递数据到更新提示页的intent key
     */
    protected static final String UPDATE_RESPONSE_KEY = "com.simpletour.lib.update.KEY.UpdateResponse";
    /**
     * 下载APK文件的目录,比如：com.simpletour.app/download
     */
    public static String DOWN_LOAD_CACHE_DIR_NAME = "com.simpletour.app/download";
    /**
     * APK文件的命名，比如：simpletour_update.apk
     */
    public static String APK_SAVE_NAME = "simpletour_update_supplier.apk";

    public static final String URL_FOR_CHECK_UPDATE="home/msgBox";
}
