package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

import java.util.Map;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：基础消息模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGBaseMessageModel implements Marshal {
    @FieldId(1)
    public Long messageId;
    @FieldId(2)
    public String conversationId;
    @FieldId(3)
    public AGOpenIdExModel openIdEx;
    @FieldId(4)
    public Integer type;
    @FieldId(5)
    public Integer creatorType;
    @FieldId(6)
    public Long createdAt;
    @FieldId(7)
    public AGContentModel content;
    @FieldId(8)
    public Long tag;
    @FieldId(9)
    public Map<String, String> extension;
    @FieldId(10)
    public Long memberTag;
    @FieldId(11)
    public Map<String, String> memberExtension;
    @FieldId(12)
    public Integer recallStatus;

    public AGBaseMessageModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.messageId = (Long)value;
            break;
        case 2:
            this.conversationId = (String)value;
            break;
        case 3:
            this.openIdEx = (AGOpenIdExModel)value;
            break;
        case 4:
            this.type = (Integer)value;
            break;
        case 5:
            this.creatorType = (Integer)value;
            break;
        case 6:
            this.createdAt = (Long)value;
            break;
        case 7:
            this.content = (AGContentModel)value;
            break;
        case 8:
            this.tag = (Long)value;
            break;
        case 9:
            this.extension = (Map)value;
            break;
        case 10:
            this.memberTag = (Long)value;
            break;
        case 11:
            this.memberExtension = (Map)value;
            break;
        case 12:
            this.recallStatus = (Integer)value;
        }

    }
}