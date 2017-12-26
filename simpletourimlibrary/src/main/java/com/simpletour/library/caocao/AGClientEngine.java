package com.simpletour.library.caocao;

import android.util.Log;

import com.simpletour.library.caocao.base.IAGCallback;
import com.simpletour.library.caocao.base.IAGConnectionStatusCallback;
import com.simpletour.library.caocao.base.IAGMessage;
import com.simpletour.library.caocao.base.IAGOnReceiveChatMessageCallback;
import com.simpletour.library.caocao.base.IAGRequestHandler;
import com.simpletour.library.caocao.model.AGSendMessageModel;
import com.simpletour.library.caocao.model.AGSendResultModel;
import com.simpletour.library.caocao.model.AGSenderMessageStatusModel;
import com.simpletour.library.caocao.utils.AGUtils;

/**
 * 包名：com.simpletour.library.caocao
 * 描述：http与socket链接请求管理器
 * 创建者：yankebin
 * 日期：2017/5/15
 */

final class AGClientEngine {
    private static final String TAG = AGClientEngine.class.getName();

    private static volatile AGClientEngine clientEngine;
    private AGWebSocketClient socketClient;

    private AGClientEngine() {

    }

    public static AGClientEngine getInstance() {
        if (null == clientEngine) {
            synchronized (AGClientEngine.class) {
                if (null == clientEngine) {
                    clientEngine = new AGClientEngine();
                }
            }
        }
        return clientEngine;
    }

    /**
     * 初始化
     *
     */
    public void init() {
        if (null == socketClient) {
            socketClient = new AGWebSocketClient();
        } else {
            Log.w(TAG, "ReInit AGClientEngine");
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        checkConfiguration();
        socketClient.destroy();
    }

    /**
     * Checks if AGClientEngine's AGSocketClient was initialized
     *
     * @throws IllegalStateException if AGSocketClient wasn't initialized
     */
    private void checkConfiguration() {
        if (null == socketClient) {
            throw new IllegalStateException("AGClientEngine must be init with context before using");
        }
    }

    /**
     * 加入一个消息回调器
     *
     * @param agCallback 消息回调器
     */
    public void registerMessageCallback(IAGOnReceiveChatMessageCallback agCallback) {
        checkConfiguration();
        socketClient.addMessageCallback(agCallback);
    }

    /**
     * 移除一个消息回调器
     *
     * @param agCallback 消息回调器
     */
    public void unRegisterMessageCallback(IAGOnReceiveChatMessageCallback agCallback) {
        checkConfiguration();
        socketClient.removeMessageCallback(agCallback);
    }

    /**
     * 加入一个socket连接状态回调器
     *
     * @param connectionStatusCallback socket连接状态回调器
     */
    public void registerSocketStatusCallback(IAGConnectionStatusCallback connectionStatusCallback) {
        checkConfiguration();
        socketClient.addStatusCallback(connectionStatusCallback);
    }

    /**
     * 移除一个socket连接状态回调器
     *
     * @param connectionStatusCallback socket连接状态回调器
     */
    public void unRegisterSocketStatusCallback(IAGConnectionStatusCallback connectionStatusCallback) {
        checkConfiguration();
        socketClient.removeStatusCallback(connectionStatusCallback);
    }


    /**
     * 发送聊天消息
     *
     * @param message
     * @param messageIAGCallback
     */
    public void sendChatMessage(final AGMessage message, IAGCallback<IAGMessage> messageIAGCallback) {
        checkConfiguration();
        final AGSendMessageModel messageModel = AGMessageFactory.buildSendMessage(message,
                AGAuthService.getInstance().getAuthInfo().nickName);
        if (null == messageModel) {
            messageIAGCallback.onError("4001", "build sendChatMessage message failed");
            return;
        }
        AGConversationService.getInstance().getConversationCache().put(messageModel.conversationId, message.mConversation);
        socketClient.sendChatMessage(messageModel, new IAGRequestHandler<AGSendResultModel, IAGMessage>(messageIAGCallback) {
            @Override
            public IAGMessage convert(AGSendResultModel sendResultModel) {
                if (null == sendResultModel) {
                    return null;
                }
                message.mMid = AGUtils.longValue(sendResultModel.messageId);
                message.mCreatedAt = AGUtils.longValue(sendResultModel.createdAt);
                message.mLastModify = message.mCreatedAt;
                message.mMessageStatus = IAGMessage.MessageStatus.SENT;
                AGSenderMessageStatusModel senderModel = sendResultModel.model;
                if (null != senderModel) {
                    message.mUnreadCount = AGUtils.intValue(senderModel.unReadCount);
                    message.mTotalCount = AGUtils.intValue(senderModel.totalCount);
                }

                return message;
            }
        });
    }
}
