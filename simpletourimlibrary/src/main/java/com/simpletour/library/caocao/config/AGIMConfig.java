package com.simpletour.library.caocao.config;

/**
 * 包名：com.simpletour.library.caocao.config
 * 描述：IM常量类
 * 创建者：yankebin
 * 日期：2017/5/5
 */

public final class AGIMConfig {
    /**
     * 服务器正式地址
     */
    public static String IM_SERVICE_URL = "192.168.4.88:8887";

    /**
     * 服务器测试地址
     */
    public static String IM_SERVICE_DEBUG_URL = "192.168.4.88:8887";

    /**
     * 服务器api版本
     */
    public static String IM_SERVICE_VERSION = "1.0.0";

    /**
     * 日志开关
     */
    public static boolean ENABLE_LOG = true;

    /**
     * 最大心跳间隔时间
     */
    public static int MAX_HERAT = 2 * 60 * 60;
    /**
     * 最小心跳间隔时间
     */
    public static int MIN_HERAT = 10;
    /**
     * 稳定心跳间隔时间
     */
    public static int STABLE_HERAT = 4 * 60;
    /**
     * 心跳步进时间
     */
    public static int HERAT_STEP =15;

}
