package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：服务器回应发送消息返回的状态模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGSenderMessageStatusModel implements Marshal {
    @FieldId(1)
    public Integer unReadCount;
    @FieldId(2)
    public Integer totalCount;

    public AGSenderMessageStatusModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.unReadCount = (Integer)value;
            break;
        case 2:
            this.totalCount = (Integer)value;
        }

    }
}