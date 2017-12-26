package com.simpletour.lib.autoupdate.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 包名：com.simpletour.lib.autoupdate.model
 * 描述：更新信息结果集
 * 创建者：yankebin
 * 日期：2016/5/18
 */
public class UpdateResponse implements Serializable {
    private String updateTitle;
    private String androidDown;
    private boolean updateForce;
    private String content;
    private String fileMd5;
    private boolean hasUpdate;
    private ArrayList<UpdateButton> extraBtns;
    private String newVersion;

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public ArrayList<UpdateButton> getExtraBtns() {
        if (null == extraBtns) {
            extraBtns = new ArrayList<>();
        }
        return extraBtns;
    }

    public void setExtraBtns(ArrayList<UpdateButton> extraBtns) {
        this.extraBtns = extraBtns;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getAndroidDown() {
        return androidDown;
    }

    public void setAndroidDown(String androidDown) {
        this.androidDown = androidDown;
    }

    public boolean isUpdateForce() {
        return updateForce;
    }

    public void setUpdateForce(boolean updateForce) {
        this.updateForce = updateForce;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public UpdateButton findBtnById(String bid) {
        for (UpdateButton updateButton : getExtraBtns()) {
            if (TextUtils.equals(bid, updateButton.getBId())) {
                return updateButton;
            }
        }
        return null;
    }
}
