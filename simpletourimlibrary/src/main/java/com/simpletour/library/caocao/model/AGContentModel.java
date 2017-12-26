package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

import java.util.List;
import java.util.Map;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：发送消息具体数据模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGContentModel implements Marshal {
    @FieldId(1)
    public Integer contentType;
    @FieldId(2)
    public AGTextContentModel textContent;
    @FieldId(3)
    public AGPhotoContentModel photoContent;
    @FieldId(4)
    public AGAudioContentModel audioContent;
    @FieldId(5)
    public AGFileContentModel fileContent;
    @FieldId(6)
    public Map<String, String> extension;
    @FieldId(7)
    public List<AGAttachmentModel> attachments;
    @FieldId(8)
    public Map<String, String> atOpenIds;

    public AGContentModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.contentType = (Integer)value;
            break;
        case 2:
            this.textContent = (AGTextContentModel)value;
            break;
        case 3:
            this.photoContent = (AGPhotoContentModel)value;
            break;
        case 4:
            this.audioContent = (AGAudioContentModel)value;
            break;
        case 5:
            this.fileContent = (AGFileContentModel)value;
            break;
        case 6:
            this.extension = (Map)value;
            break;
        case 7:
            this.attachments = (List)value;
            break;
        case 8:
            this.atOpenIds = (Map)value;
        }

    }
}