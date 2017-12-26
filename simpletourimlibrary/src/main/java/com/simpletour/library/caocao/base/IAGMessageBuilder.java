package com.simpletour.library.caocao.base;

import java.util.List;
import java.util.Map;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：消息构造器
 * 创建者：yankebin
 * 日期：2017/5/17
 */

public interface IAGMessageBuilder {
    IAGMessage buildTextMessage(String text);

    IAGMessage buildTextMessage(String text, Map<Long, String> atOpenIds);

    IAGMessage buildTextMessage(String text, String templateId, List<String> templateData);

    IAGMessage buildImageMessage(String picPath);

    IAGMessage buildImageMessage(String picPath, long picSize, int picType);

    IAGMessage buildImageMessage(String picPath, long picSize, int picType, int orientation);

    IAGMessage buildAudioMessage(String audioPath);

    IAGMessage buildAudioMessage(String audioPath, boolean isStreamFile);

    IAGMessage buildAudioMessage(String audioPath, long duration, List<Integer> volumns);

    IAGMessage buildLinkedMessage(String url, String title, String text, String picUrl);

    IAGMessage buildLinkedMessage(String url, String title, String text, String picUrl, Map<String, String> extension);

    IAGMessage buildMessage(IAGMessageContent messageContent);
}
