package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.AGProtocolType;
import com.simpletour.library.caocao.base.Marshal;

import java.util.List;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：发送消息的模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGSendMessageModel implements Marshal {
    public int protocolType = AGProtocolType.TYPE_CHAT_SEND_MESSAGE;
    @FieldId(1)
    public String uuid;
    @FieldId(2)
    public String conversationId;
    @FieldId(3)
    public Integer type;
    @FieldId(4)
    public Integer creatorType;
    @FieldId(5)
    public AGContentModel content;
    @FieldId(6)
    public Long tag;
    @FieldId(7)
    public String nickName;
    @FieldId(8)
    public List<Long> receivers;
    @FieldId(9)
    public Long messageId;
    @FieldId(10)
    public Long senderId;


    public AGSendMessageModel() {
    }

    public void decode(int idx, Object value) {
        switch (idx) {
            case 1:
                this.uuid = (String) value;
                break;
            case 2:
                this.conversationId = (String) value;
                break;
            case 3:
                this.type = (Integer) value;
                break;
            case 4:
                this.creatorType = (Integer) value;
                break;
            case 5:
                this.content = (AGContentModel) value;
                break;
            case 6:
                this.tag = (Long) value;
                break;
            case 7:
                this.nickName = (String) value;
                break;
            case 8:
                this.receivers = (List<Long>) value;
                break;
            case 9:
                this.messageId = (Long) value;
                break;
            case 10:
                this.senderId=(Long)value;
                break;
        }

    }
}