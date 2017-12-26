package com.simpletour.library.caocao;

import android.text.TextUtils;

import com.simpletour.library.caocao.base.IAGMessage;
import com.simpletour.library.caocao.base.IAGMessageBuilder;
import com.simpletour.library.caocao.base.IAGMessageContent;
import com.simpletour.library.caocao.base.IAGMessageContent.AudioContent;
import com.simpletour.library.caocao.base.IAGMessageContent.ImageContent;
import com.simpletour.library.caocao.base.IAGMessageContent.LinkedContent;
import com.simpletour.library.caocao.base.IAGMessageContent.TextContent;
import com.simpletour.library.caocao.utils.AGUtils;

import java.util.List;
import java.util.Map;

/**
 * 包名：com.againstalone.lib.imkit
 * 描述：消息构造器
 * 创建者：yankebin
 * 日期：2017/5/17
 */

public class AGMessageBuilder implements IAGMessageBuilder {
    @Override
    public IAGMessage buildTextMessage(String text) {
        return buildTextMessage(text, null);
    }

    @Override
    public IAGMessage buildTextMessage(String text, Map<Long, String> atOpenIds) {
        if (TextUtils.isEmpty(text)) {
            throw new RuntimeException("Text must not be empty for TextMessage");
        }
        TextContent content = buildTextContent(text, null, null);
        return buildMessage(content);
    }

    @Override
    public IAGMessage buildTextMessage(String content, String templateId, List<String> templateData) {
        return null;
    }

    private TextContent buildTextContent(String text, String templateId, List<String> templateData) {
        return new AGMessageContent.TextContentImpl(text, templateId, templateData);
    }

    @Override
    public IAGMessage buildImageMessage(String picPath) {
        return buildImageMessage(picPath, 0L, 0);
    }

    @Override
    public IAGMessage buildImageMessage(String picPath, long picSize, int picType) {
        return buildImageMessage(picPath, picSize, picType, 0);
    }

    @Override
    public IAGMessage buildImageMessage(String picPath, long picSize, int picType, int orientation) {
        ImageContent content = buildImageContent(picPath, picSize, picType, orientation);
        return buildMessage(content);
    }

    private ImageContent buildImageContent(String picPath, long picSize, int picType, int orientation) {
        if (TextUtils.isEmpty(picPath)) {
            throw new RuntimeException("Pic url must not be empty for ImageMessage");
        }
        return new AGMessageContent.ImageContentImpl(picPath, picSize, picType, orientation);
    }

    @Override
    public IAGMessage buildAudioMessage(String audioPath) {
        return buildAudioMessage(audioPath, 0L, null);
    }

    @Override
    public IAGMessage buildAudioMessage(String audioPath, boolean isStreamFile) {
        AudioContent content = buildAudioContent(audioPath, 0L, null);
        return buildMessage(content, null, isStreamFile);
    }

    @Override
    public IAGMessage buildAudioMessage(String audioPath, long duration, List<Integer> volumns) {
        AudioContent content = buildAudioContent(audioPath, duration, volumns);
        return buildMessage(content);
    }

    private AudioContent buildAudioContent(String audioPath, long duration, List<Integer> volumns) {
        if (TextUtils.isEmpty(audioPath)) {
            throw new RuntimeException("Audio url must not be empty for AudioMessage");
        }
        return new AGMessageContent.AudioContentImpl(audioPath, duration, volumns);
    }

    @Override
    public IAGMessage buildLinkedMessage(String url, String title, String text, String picUrl) {
        return buildLinkedMessage(url, title, text, picUrl, null);
    }

    @Override
    public IAGMessage buildLinkedMessage(String url, String title, String text, String picUrl, Map<String, String> extension) {
        LinkedContent content = buildLinkedContent(url, title, text, picUrl, extension);
        return buildMessage(content, null, false);
    }


    private LinkedContent buildLinkedContent(String url, String title, String text, String picUrl, Map<String, String> extension) {
        return new AGMessageContent.LinkedContentImpl(url, title, text, picUrl, extension);
    }

    @Override
    public IAGMessage buildMessage(IAGMessageContent messageContent) {
        return buildMessage(messageContent, null, false);
    }

    private IAGMessage buildMessage(IAGMessageContent content, Map<Long, String> atOpenIds, boolean isStreamFile) {
        if (null == content) {
            return null;
        }

        AGMessage message = AGMessage.newInstance();
        message.mMessageContent = content;
        message.mMessageType = IAGMessage.MessageType.COMMON;
        message.mCreatorType = IAGMessage.CreatorType.SELF;
        message.mMid = AGUtils.createId();
        message.mLocalId = AGUtils.createLocalId();
        message.mSenderId = AGAuthService.getInstance().getAuthInfo().openId;
        message.mCreatedAt = System.currentTimeMillis();
        message.mLastModify = message.createdAt();
        message.mIsRead = true;
        message.mAtOpenIds = atOpenIds;

        if (isStreamFile) {
            // TODO: 2017/5/17 流媒体策略
        }

        return message;
    }
}
