package com.simpletour.lib.autoupdate.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 包名：com.simpletour.lib.autoupdate.model
 * 描述：更新消息体
 * 创建者：yankebin
 * 日期：2016/7/27
 */

public class UpdateMsgBox implements Serializable {


    /**
     * versionLE : v1.1.1
     * title : 版本升级
     * contentText : 算了， 你看着办吧 反正有新版本了！
     * 爱升不升！！！
     */

    private String versionLE;
    private String title;
    private String contentText;
    private String fileMd5;
    private String clientVersion;
    private ArrayList<UpdateButton> buttons;

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public ArrayList<UpdateButton> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<UpdateButton> buttons) {
        this.buttons = buttons;
    }

    public String getVersionLE() {
        return versionLE;
    }

    public void setVersionLE(String versionLE) {
        this.versionLE = versionLE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }
}
