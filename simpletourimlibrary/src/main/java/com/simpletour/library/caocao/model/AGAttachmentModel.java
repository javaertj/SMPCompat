package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

import java.util.Map;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：附件模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGAttachmentModel implements Marshal {
    @FieldId(1)
    public Integer type;
    @FieldId(2)
    public String url;
    @FieldId(3)
    public Long size;
    @FieldId(4)
    public byte[] data;
    @FieldId(5)
    public Boolean isPreload;
    @FieldId(6)
    public Map<String, String> extension;

    public AGAttachmentModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.type = (Integer)value;
            break;
        case 2:
            this.url = (String)value;
            break;
        case 3:
            this.size = (Long)value;
            break;
        case 4:
            this.data = (byte[])((byte[])value);
            break;
        case 5:
            this.isPreload = (Boolean)value;
            break;
        case 6:
            this.extension = (Map)value;
        }

    }
}