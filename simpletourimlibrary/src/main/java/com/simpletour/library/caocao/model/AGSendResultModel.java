package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.AGProtocolType;
import com.simpletour.library.caocao.base.Marshal;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：服务器回应发送消息的模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGSendResultModel implements Marshal {
    public int protocolType= AGProtocolType.TYPE_CHAT_RESPONSE_MESSAGE;
    @FieldId(1)
    public Long messageId;
    @FieldId(2)
    public Long createdAt;
    @FieldId(3)
    public AGSenderMessageStatusModel model;

    public AGSendResultModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.messageId = (Long)value;
            break;
        case 2:
            this.createdAt = (Long)value;
            break;
        case 3:
            this.model = (AGSenderMessageStatusModel)value;
        }

    }
}