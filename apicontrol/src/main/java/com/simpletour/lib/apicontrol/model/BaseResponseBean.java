package com.simpletour.lib.apicontrol.model;

import android.text.TextUtils;

import com.simpletour.lib.apicontrol.ErrorCode;

import java.io.Serializable;

/**
 * 包名：com.simpletour.lib.apicontrol.model
 * 描述：服务器返回数据基础实体类
 * 创建者：yankebin
 * 日期：2017/2/23
 */

public class BaseResponseBean implements Serializable {
    private String errorCode;
    private String status;
    private String errorMessage;
    private String extMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExtMessage() {
        return extMessage;
    }

    public void setExtMessage(String extMessage) {
        this.extMessage = extMessage;
    }

    public boolean isAvailable() {
        return TextUtils.equals(ErrorCode.ERROR_CODE_OK, errorCode) &&
                TextUtils.equals(status, ErrorCode.STATUS_SUCCESS);
    }
}
