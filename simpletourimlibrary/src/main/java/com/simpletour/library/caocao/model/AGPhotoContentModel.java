package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：图片消息内容模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGPhotoContentModel implements Marshal {
    @FieldId(1)
    public String mediaId;
    @FieldId(2)
    public Long picSize;
    @FieldId(3)
    public Integer type;
    @FieldId(4)
    public byte[] picBytes;
    @FieldId(5)
    public Integer fileType;
    @FieldId(6)
    public Integer orientation;

    public AGPhotoContentModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.mediaId = (String)value;
            break;
        case 2:
            this.picSize = (Long)value;
            break;
        case 3:
            this.type = (Integer)value;
            break;
        case 4:
            this.picBytes = (byte[])((byte[])value);
            break;
        case 5:
            this.fileType = (Integer)value;
            break;
        case 6:
            this.orientation = (Integer)value;
        }

    }
}