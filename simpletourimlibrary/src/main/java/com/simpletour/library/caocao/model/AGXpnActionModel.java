package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：推送消息操作模板模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGXpnActionModel implements Marshal {
    @FieldId(1)
    public String templateKey;
    @FieldId(2)
    public String templateValue;

    public AGXpnActionModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.templateKey = (String)value;
            break;
        case 2:
            this.templateValue = (String)value;
        }

    }
}