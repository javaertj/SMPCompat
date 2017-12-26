package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：openId扩展消息模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGOpenIdExModel implements Marshal {
    @FieldId(1)
    public Long openId;
    @FieldId(2)
    public Long tag;

    public AGOpenIdExModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.openId = (Long)value;
            break;
        case 2:
            this.tag = (Long)value;
        }

    }
}