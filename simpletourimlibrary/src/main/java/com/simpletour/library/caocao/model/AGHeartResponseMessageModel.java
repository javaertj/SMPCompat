package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.AGProtocolType;
import com.simpletour.library.caocao.base.Marshal;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：信心跳消息
 * 创建者：yankebin
 * 日期：2017/11/30
 */

public class AGHeartResponseMessageModel implements Marshal {
    public int protocolType = AGProtocolType.TYPE_HEART_RESPONSE_MESSAGE;
    @FieldId(1)
    public Long id;

    @Override
    public void decode(int filedId, Object value) {
        if (filedId == 1) {
            id = (Long) value;
        }
    }
}
