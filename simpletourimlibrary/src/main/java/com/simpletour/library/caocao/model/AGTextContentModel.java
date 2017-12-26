package com.simpletour.library.caocao.model;

import com.simpletour.library.caocao.annoations.FieldId;
import com.simpletour.library.caocao.base.Marshal;

import java.util.List;

/**
 * 包名：com.simpletour.library.caocao.model
 * 描述：文本消息内容模型
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public final class AGTextContentModel implements Marshal {
    @FieldId(1)
    public String text;
    @FieldId(2)
    public String templateId;
    @FieldId(3)
    public List<String> templateData;

    public AGTextContentModel() {
    }

    public void decode(int idx, Object value) {
        switch(idx) {
        case 1:
            this.text = (String)value;
            break;
        case 2:
            this.templateId = (String)value;
            break;
        case 3:
            this.templateData = (List<String>)value;
        }

    }
}