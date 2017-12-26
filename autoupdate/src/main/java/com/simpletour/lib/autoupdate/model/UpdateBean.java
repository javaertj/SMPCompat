package com.simpletour.lib.autoupdate.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 包名：com.simpletour.lib.autoupdate.model
 * 描述：更新结构体
 * 创建者：yankebin
 * 日期：2016/7/27
 */

public class UpdateBean implements Serializable {
    private int showBox;// 1:显示消息框，0:不显示消息框（此时msgBox:null）
    private UpdateMsgBox msgBox;

    public int getShowBox() {
        return showBox;
    }

    public void setShowBox(int showBox) {
        this.showBox = showBox;
    }

    public UpdateMsgBox getMsgBox() {
        return msgBox;
    }

    public void setMsgBox(UpdateMsgBox msgBox) {
        this.msgBox = msgBox;
    }

    public UpdateResponse buildUpdateResponse(){
        if(null==msgBox){
            return null;
        }
        ArrayList<UpdateButton> updateButtons=msgBox.getButtons();
        boolean updateForce=null!=updateButtons&&updateButtons.size()==1;

        UpdateResponse response=new UpdateResponse();
        response.setHasUpdate(showBox==1);
        response.setNewVersion(msgBox.getClientVersion());
        response.setContent(msgBox.getContentText());
        response.setFileMd5(msgBox.getFileMd5());
        response.setUpdateForce(updateForce);
        response.setExtraBtns(updateButtons);
        response.setUpdateTitle(msgBox.getTitle());

        return response;
    }
}
