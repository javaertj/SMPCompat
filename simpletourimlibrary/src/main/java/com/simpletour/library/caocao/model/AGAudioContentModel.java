package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

import java.util.List;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：语音消息内容模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGAudioContentModel implements Marshal {
    @FieldId(1)
    public String mediaId;
    @FieldId(2)
    public Long duration;
    @FieldId(3)
    public List<Integer> audioVolumns;
    @FieldId(4)
    public byte[] audioBytes;

    public AGAudioContentModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.mediaId = (String)value;
            break;
        case 2:
            this.duration = (Long)value;
            break;
        case 3:
            this.audioVolumns = (List<Integer>)value;
            break;
        case 4:
            this.audioBytes = (byte[])value;
        }

    }
}