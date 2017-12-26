package com.simpletour.lib.apicontrol;

/**
 * 包名：com.simpletour.supplier.bean
 * 描述：服务器返回码存储类
 * 创建者：yankebin
 * 日期：2017/3/7
 */

public final class ErrorCode {
    /**
     * 错误码前缀
     */
    public static final String ERROR_CODE_PREFIX = "APP.";
    /**
     * 无错误
     */
    public static final String ERROR_CODE_OK = ERROR_CODE_PREFIX + "0000";

    /**
     * token错误
     */
    public static final String ERROR_CODE_TOKEN_ERROR = ERROR_CODE_PREFIX + "0002";

    /**
     * 参数错误
     */
    public static final String ERROR_CODE_PARAMETER_ERROR = ERROR_CODE_PREFIX + "0001";

    /**
     * 签名错误
     */
    public static final String ERROR_CODE_SIGN_ERROR = ERROR_CODE_PREFIX + "0003";

    /**
     * 系统错误
     */
    public static final String ERROR_CODE_SYSTEM_ERROR = ERROR_CODE_PREFIX + "0004";

    /**
     * 成功状态
     */
    public static final String STATUS_SUCCESS = "SUCCEED";

    /**
     * 失败状态
     */
    public static final String STATUS_FAILED = "FAILED";

}
