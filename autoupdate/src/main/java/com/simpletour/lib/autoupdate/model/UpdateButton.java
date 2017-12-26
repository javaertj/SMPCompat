package com.simpletour.lib.autoupdate.model;

import java.io.Serializable;

/**
 * 包名：com.simpletour.lib.autoupdate.model
 * 描述：更新按钮
 * 创建者：yankebin
 * 日期：2016/7/27
 */

public class UpdateButton implements Serializable {

    /**
     * bId : cancel
     * name : 忽略
     * action :
     */

    private String bId;
    private String name;
    private String action;

    public String getBId() {
        return bId;
    }

    public void setBId(String bId) {
        this.bId = bId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
