package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.AGProtocolType;
import com.simpletour.library.caocao.base.Marshal;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：服务器转发来的消息传输模型
 * 创建者：yankebin
 * 日期：2017/5/19
 */
public final class AGMessageModel implements Marshal {
    public int protocolType = AGProtocolType.TYPE_CHAT_FORWARD_MESSAGE;
    @FieldId(1)
    public AGBaseMessageModel baseMessage;
    @FieldId(2)
    public AGReceiverMessageStatusModel receiverMessageStatus;
    @FieldId(3)
    public AGSenderMessageStatusModel senderMessageStatus;

    public AGMessageModel() {
    }

    public void decode(int idx, Object value) {
        switch (idx) {
            case 1:
                this.baseMessage = (AGBaseMessageModel) value;
                break;
            case 2:
                this.receiverMessageStatus = (AGReceiverMessageStatusModel) value;
                break;
            case 3:
                this.senderMessageStatus = (AGSenderMessageStatusModel) value;
        }

    }
}