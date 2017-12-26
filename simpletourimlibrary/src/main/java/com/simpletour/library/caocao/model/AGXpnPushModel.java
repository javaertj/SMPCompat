package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

import java.util.Map;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：推送消息内容模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGXpnPushModel implements Marshal {
    @FieldId(1)
    public String sound;
    @FieldId(2)
    public AGXpnActionModel action;
    @FieldId(3)
    public Long timeTolive;
    @FieldId(4)
    public Integer incrbadge;
    @FieldId(5)
    public Map<String, String> params;
    @FieldId(6)
    public String alertContent;
    @FieldId(7)
    public Integer isXpnOff;
    @FieldId(8)
    public Integer templateId;

    public AGXpnPushModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.sound = (String)value;
            break;
        case 2:
            this.action = (AGXpnActionModel)value;
            break;
        case 3:
            this.timeTolive = (Long)value;
            break;
        case 4:
            this.incrbadge = (Integer)value;
            break;
        case 5:
            this.params = (Map)value;
            break;
        case 6:
            this.alertContent = (String)value;
            break;
        case 7:
            this.isXpnOff = (Integer)value;
            break;
        case 8:
            this.templateId = (Integer)value;
        }

    }
}