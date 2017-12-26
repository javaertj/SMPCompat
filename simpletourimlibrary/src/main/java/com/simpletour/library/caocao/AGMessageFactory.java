package com.simpletour.library.caocao;


import com.simpletour.library.caocao.base.IAGMessage;
import com.simpletour.library.caocao.base.IAGMessageContent;
import com.simpletour.library.caocao.model.AGAttachmentModel;
import com.simpletour.library.caocao.model.AGAudioContentModel;
import com.simpletour.library.caocao.model.AGBaseMessageModel;
import com.simpletour.library.caocao.model.AGContentModel;
import com.simpletour.library.caocao.model.AGMessageModel;
import com.simpletour.library.caocao.model.AGPhotoContentModel;
import com.simpletour.library.caocao.model.AGReceiverMessageStatusModel;
import com.simpletour.library.caocao.model.AGSendMessageModel;
import com.simpletour.library.caocao.model.AGSenderMessageStatusModel;
import com.simpletour.library.caocao.model.AGTextContentModel;
import com.simpletour.library.caocao.utils.AGUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.againstalone.lib.imkit
 * 描述：发送消息构造器
 * 创建者：yankebin
 * 日期：2017/5/19
 */

public class AGMessageFactory {

    public AGMessageFactory() {

    }

    public static AGSendMessageModel buildSendMessage(AGMessage message, String nickName) {
        if (null == message) {
            return null;
        }
        AGSendMessageModel model = new AGSendMessageModel();
        model.uuid = message.localId();
        model.messageId = message.messageId();
        AGConversation conversation = message.mConversation;
        model.conversationId = null == conversation ? null : conversation.conversationId();
        model.type = message.mMessageType.typeValue();
        model.creatorType = message.mCreatorType.typeValue();
        model.content = buildContentModel(message.mMessageContent, flatLongToString(message.mAtOpenIds));
        model.nickName = nickName;
        model.senderId = message.senderId();

        return model;
    }


    private static AGContentModel buildContentModel(IAGMessageContent messageContent, Map<String, String> atOpenIds) {
        if (null == messageContent) {
            return null;
        }

        AGContentModel content = new AGContentModel();
        content.contentType = messageContent.type();
        content.atOpenIds = atOpenIds;
        switch (messageContent.type()) {
            //文本
            case IAGMessageContent.MessageContentType.TEXT:
                IAGMessageContent.TextContent textContent = (IAGMessageContent.TextContent) messageContent;
                AGTextContentModel textModel = new AGTextContentModel();
                textModel.text = textContent.text();
                textModel.templateId = textContent.templateId();
                textModel.templateData = textContent.templateData();
                content.textContent = textModel;
                break;
            //图片
            case IAGMessageContent.MessageContentType.IMAGE:
                IAGMessageContent.ImageContent imageContent = (IAGMessageContent.ImageContent) messageContent;
                AGPhotoContentModel photoModel = new AGPhotoContentModel();
                photoModel.picSize = imageContent.size();
                photoModel.type = imageContent.picType();
                photoModel.orientation = imageContent.getOrientation();
                content.photoContent = photoModel;
                break;
            //语音
            case IAGMessageContent.MessageContentType.AUDIO:
                IAGMessageContent.AudioContent audioContent = (IAGMessageContent.AudioContent) messageContent;
                AGAudioContentModel audioModel = new AGAudioContentModel();
                audioModel.duration = audioContent.duration();
                audioModel.audioVolumns = audioContent.volumns();
                content.audioContent = audioModel;
                break;
            //连接
            case IAGMessageContent.MessageContentType.LINKED:
                IAGMessageContent.LinkedContent linkedContent = (IAGMessageContent.LinkedContent) messageContent;
                ArrayList<AGAttachmentModel> linkedList = new ArrayList<>();
                HashMap<String, String> linkedMap = new HashMap<>();
                if (null != linkedContent.extension()) {
                    linkedMap.putAll(linkedContent.extension());
                }
                linkedMap.put("title", linkedContent.title());
                linkedMap.put("text", linkedContent.text());
                linkedMap.put("picUrl", linkedContent.picUrl());
                AGAttachmentModel attachModel = buildAttachmentModel(16, linkedContent.url(), linkedContent.size(), false, linkedMap);
                linkedList.add(attachModel);
                content.attachments = linkedList;
                break;


        }
        return content;

    }

    private static AGAttachmentModel buildAttachmentModel(int type, String url, long size, boolean isPreload, Map<String, String> extension) {
        AGAttachmentModel attachmentModel = new AGAttachmentModel();
        attachmentModel.type = type;
        attachmentModel.url = url;
        attachmentModel.size = size;
        attachmentModel.isPreload = isPreload;
        attachmentModel.extension = extension;

        return attachmentModel;

    }


    private static Map<String, String> flatLongToString(Map<Long, String> source) {
        if (null == source) {
            return null;
        }
        HashMap<String, String> ret = new HashMap<>();
        for (Map.Entry<Long, String> entry : source.entrySet()) {
            Long key = entry.getKey();
            if (null != key) {
                ret.put(key.toString(), entry.getValue());
            }
        }

        return ret;
    }

    private static Map<Long, String> flatStringToLong(Map<String, String> source) {
        if (null == source) {
            return null;
        }
        HashMap<Long, String> ret = new HashMap<>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            String key = entry.getKey();
            if (null != key) {
                ret.put(AGUtils.toLong(key), entry.getValue());
            }
        }

        return ret;
    }


    private static IAGMessageContent buildMessageContent(AGContentModel model) {
        if (model == null) {
            return new AGMessageContent(-1);
        }
        Object messageContent = null;
        int contentType = AGUtils.intValue(model.contentType);
        if (contentType == IAGMessageContent.MessageContentType.TEXT) {
            AGTextContentModel modelList = model.textContent;
            if (modelList != null) {
                messageContent = new AGMessageContent.TextContentImpl(modelList.text, modelList.templateId, modelList.templateData);
            }
        } else {
            String multiContent;
            if (contentType == IAGMessageContent.MessageContentType.IMAGE) {
                AGPhotoContentModel AGPhotoContentModel = model.photoContent;
                if (AGPhotoContentModel != null) {
                    multiContent = AGPhotoContentModel.mediaId;
                    messageContent = new AGMessageContent.ImageContentImpl(multiContent,
                            AGUtils.longValue(AGPhotoContentModel.picSize), AGUtils.intValue(AGPhotoContentModel.type),
                            AGUtils.intValue(AGPhotoContentModel.fileType), AGPhotoContentModel.picBytes, AGUtils.intValue(AGPhotoContentModel.orientation));
                }
            } else if (contentType == IAGMessageContent.MessageContentType.AUDIO) {
                AGAudioContentModel audioContentModel = model.audioContent;
                if (audioContentModel != null) {
                    multiContent = audioContentModel.mediaId;
                    messageContent = new AGMessageContent.AudioContentImpl(multiContent,
                            AGUtils.longValue(audioContentModel.duration), audioContentModel.audioVolumns, audioContentModel.audioBytes);
                }
            } else if (contentType == IAGMessageContent.MessageContentType.LINKED) {
                List<AGAttachmentModel> modelList = model.attachments;
                if (modelList != null && !modelList.isEmpty()) {
                    AGAttachmentModel attachmentModel = modelList.get(0);
                    if (attachmentModel != null) {
                        String attachUrl = attachmentModel.url;
                        Map<String, String> extension = attachmentModel.extension;
                        String a = extension == null ? null : extension.remove("title");
                        String customType = extension == null ? null : extension.remove("text");
                        String mediaId = extension == null ? null : extension.remove("picUrl");
                        messageContent = new AGMessageContent.LinkedContentImpl(attachUrl, a,
                                customType, mediaId, extension);
                    }
                }
            }
        }
        return (IAGMessageContent) messageContent;
    }


    public static AGMessage buildMessage(AGMessageModel model, long openId, AGConversation conversation) {
        if (model != null && model.baseMessage != null) {
            AGBaseMessageModel baseModel = model.baseMessage;
            AGMessage message = AGMessage.newInstance();
            message.mConversation = conversation;
            message.mMid = AGUtils.longValue(baseModel.messageId);
            message.mMessageType = IAGMessage.MessageType.fromValue(AGUtils.intValue(baseModel.type));
            message.mCreatorType = IAGMessage.CreatorType.fromValue(AGUtils.intValue(baseModel.creatorType));
            message.mMessageStatus = IAGMessage.MessageStatus.SENT;

            if (baseModel.createdAt != null) {
                message.mCreatedAt = AGUtils.longValue(baseModel.createdAt);
                message.mLastModify = AGUtils.longValue(baseModel.createdAt);
            } else {
                message.mLastModify = System.currentTimeMillis();
                message.mCreatedAt = message.mLastModify;
            }

            if (baseModel.openIdEx != null) {
                message.mSenderId = AGUtils.longValue(baseModel.openIdEx.openId);
            }

            message.mMessageContent = buildMessageContent(baseModel.content);
            AGReceiverMessageStatusModel receiverModel = model.receiverMessageStatus;
            if (receiverModel != null) {
                int senderModel = AGUtils.intValue(receiverModel.readStatus);
                if (openId != message.mSenderId) {
                    message.mIsRead = senderModel == IAGMessage.ReadStatus.READ.typeValue();
                } else {
                    message.mIsRead = true;
                }
            }

            AGSenderMessageStatusModel senderModel1 = model.senderMessageStatus;
            if (senderModel1 != null && openId == message.mSenderId) {
                message.mUnreadCount = AGUtils.intValue(senderModel1.unReadCount);
                message.mTotalCount = AGUtils.intValue(senderModel1.totalCount);
                message.mSentLocalTime = message.mCreatedAt;
            }
            message.mAtOpenIds = flatStringToLong(baseModel.content.atOpenIds);
            message.doAfter();
            return message;
        }
        return null;
    }

    public static void deepCopy(AGMessage from, AGMessage to) {
        if (from != null && to != null) {
            to.mConversation = from.mConversation;
            to.mLocalId = from.mLocalId;
            to.mMid = from.mMid;
            to.mSenderId = from.mSenderId;
            to.mMessageType = from.mMessageType;
            to.mCreatorType = from.mCreatorType;
            to.mCreatedAt = from.mCreatedAt;
            to.mLastModify = from.mLastModify;
            to.mMessageStatus = from.mMessageStatus;
            to.mUnreadCount = from.mUnreadCount;
            to.mTotalCount = from.mTotalCount;
            to.mMessageContent = from.mMessageContent;
            to.mIsRead = from.mIsRead;
            to.mAtOpenIds = from.mAtOpenIds;
            to.mTemplateId = from.mTemplateId;
            to.mSentLocalTime = from.mSentLocalTime;
        }
    }

    public static void setConversation(AGMessage message, AGConversation conversation) {
        if (message != null) {
            message.mConversation = conversation;
        }

    }

    public static void setCreatorType(AGMessage message, IAGMessage.CreatorType creatorType) {
        if (message != null) {
            message.mCreatorType = creatorType;
        }
    }
}
