package com.simpletour.lib.autoupdate.model;

import com.simpletour.lib.apicontrol.model.BaseResponseBean;

import java.io.Serializable;

/**
 * 包名：com.simpletour.lib.autoupdate.model
 * 描述：服务器返回的单体实体类
 * 创建者：yankebin
 * 日期：2017/2/27
 */

public class SResponseBean<T extends Serializable> extends BaseResponseBean {
    protected T data;

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
