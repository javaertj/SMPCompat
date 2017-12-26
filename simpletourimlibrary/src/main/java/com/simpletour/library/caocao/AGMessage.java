package com.simpletour.library.caocao;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.simpletour.library.caocao.base.IAGCallback;
import com.simpletour.library.caocao.base.IAGConversation;
import com.simpletour.library.caocao.base.IAGMessage;
import com.simpletour.library.caocao.base.IAGMessageContent;
import com.simpletour.library.caocao.base.IAGMessageProxy;
import com.simpletour.library.caocao.utils.AGUtils;
import com.simpletour.library.caocao.utils.AGCallbackUtils;

import java.util.Map;

/**
 * 包名：com.againstalone.lib.imkit
 * 描述：消息实体
 * 创建者：yankebin
 * 日期：2017/5/17
 */

public class AGMessage implements IAGMessage {
    AGConversation mConversation;
    long mMid;
    long mSenderId;
    IAGMessage.MessageType mMessageType;
    CreatorType mCreatorType;
    long mCreatedAt;
    long mLastModify;
    MessageStatus mMessageStatus;
    int mUnreadCount;
    int mTotalCount;
    IAGMessageContent mMessageContent;
    long mSentLocalTime;
    boolean mIsRead;
    Map<Long, String> mAtOpenIds;
    int mTemplateId;
    String mLocalId;
    static IAGMessageProxy mProxy;

    public AGMessage() {
        this.mMessageStatus = MessageStatus.OFFLINE;
    }

    @Override
    public AGConversation conversation() {
        return this.mConversation;
    }

    @Override
    public long messageId() {
        return mMid;
    }

    @Override
    public String localId() {
        return mLocalId;
    }

    @Override
    public long senderId() {
        return mSenderId;
    }

    @Override
    public MessageType messageType() {
        return mMessageType;
    }

    @Override
    public CreatorType creatorType() {
        return mCreatorType;
    }

    @Override
    public long createdAt() {
        return mCreatedAt;
    }

    @Override
    public int unReadCount() {
        return mUnreadCount;
    }

    @Override
    public int receiverCount() {
        return mTotalCount;
    }

    @Override
    public boolean allReceiversRead() {
        return mUnreadCount == 0;
    }

    @Override
    public boolean iHaveRead() {
        return mIsRead;
    }

    @Override
    public MessageStatus status() {
        return mMessageStatus;
    }

    @Override
    public IAGMessageContent messageContent() {
        return mMessageContent;
    }

    @Override
    public long lastModify() {
        return mLastModify;
    }

    @Override
    public long sentLocalTime() {
        return mSentLocalTime;
    }

    @Override
    public boolean isAt() {
        return null != mAtOpenIds && mAtOpenIds.containsKey(AGAuthService.getInstance().getAuthInfo().openId);
    }

    @Override
    public Map<Long, String> atOpenIds() {
        return mAtOpenIds;
    }

    @Override
    public final synchronized void sendTo(IAGConversation conversation, IAGCallback<IAGMessage> messageIAGCallback) {
        if (null == conversation) {
            AGCallbackUtils.onError(messageIAGCallback, "4001", "conversation is empty");
            return;
        }

        final String cid = conversation.conversationId();
        if (!TextUtils.isEmpty(cid) && conversation.status() !=
                IAGConversation.ConversationStatus.QUIT && conversation.status()
                != IAGConversation.ConversationStatus.KICKOUT) {
            if (null == mMessageContent) {
                AGCallbackUtils.onError(messageIAGCallback, "4002", "message has no messageContent");
                return;
            }

            if (!AGUtils.isLogin(messageIAGCallback)) {
                return;
            }

            final long oldMid = this.mMid;
            if (!AGUtils.isValidMessageId(oldMid)) {
                AGCallbackUtils.onError(messageIAGCallback, "4004", "messageId is invalid");
                return;
            }
            mConversation=(AGConversation) conversation;
            AGClientEngine.getInstance().sendChatMessage(this, messageIAGCallback);
            return;
        }

        AGCallbackUtils.onError(messageIAGCallback, "4005", "conversation is invalid");
    }

    @Override
    public void read() {

    }

    @Override
    public void delete(IAGCallback<Void> callback) {

    }

    @Override
    public int compareTo(@NonNull IAGMessage another) {
        long ret = mCreatedAt - another.createdAt();
        return ret < 0L ? -1 : (ret > 0L ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (null != obj && obj instanceof AGMessage) {
            if (obj == this) {
                return true;
            }
            AGMessage message = (AGMessage) obj;
            return null != mConversation && mConversation.equals(message.conversation()) && (mMid == message.mMid
                    || message.senderId() == mSenderId && !TextUtils.isEmpty(mLocalId) && TextUtils.equals(mLocalId, message.localId()));

        }
        return false;
    }

    protected void doAfter() {

    }

    public static AGMessage newInstance() {
        return null == mProxy ? new AGMessage() : (AGMessage) mProxy.newInstance();
    }
}
