package com.simpletour.library.caocao.base;

import java.io.Serializable;
import java.util.List;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：会话
 * 创建者：yankebin
 * 日期：2017/4/28
 */
public interface IAGConversation extends Serializable, Comparable<IAGConversation> {
    /**
     * 会话id
     *
     * @return
     */
    String conversationId();

    /**
     * 会话类型
     * @return
     */
    int type();

    /**
     * 开启通知
     *
     * @return
     */
    boolean enableNotify();

    /**
     * 开启通知声音
     *
     * @return
     */
    boolean enableNotifySound();

    /**
     * 设置是否屏蔽消息
     * @param enableNotify
     * @param callback
     */
    void setEnableNotify(boolean enableNotify, IAGCallback<Void> callback);

    /**
     * 设置是否启动消息通知声音
     * @param enableNotifySound
     * @param callback
     */
    void setEnableNotifySound(boolean enableNotifySound, IAGCallback<Void> callback);

    /**
     * 创建时间
     *
     * @return
     */
    long createdAt();

    /**
     * 获取最后修改时间
     * @return
     */
    long getLastModify();

    /**
     * 未读消息数量
     * @return
     */
    int unReadMessageCount();

    /**
     * 成员列表
     *
     * @return
     */
    void getMembers(IAGCallback<List<IAGMember>> callback);

    /**
     * 名称
     *
     * @return
     */
    String conversationName();

    /**
     * 描述
     *
     * @return
     */
    String conversationDesc();

    /**
     * 成员数量
     * @return
     */
    int totalMembers();

    /**
     * 创建者
     *
     * @return
     */
    long owner();

    /**
     * 消息列表，服务器优先
     *
     * @return
     */
    void messageList();

    /**
     * 本地消息列表
     *
     * @return
     */
    List<IAGMessage> localMessageList();

    /**
     * 管理员
     *
     * @return
     */
    List<IAGMember> managers();

    /**
     * 最后一条消息
     *
     * @return
     */
    IAGMessage lastMessage();

    /**
     * 会话图标
     * @return
     */
    String icon();

    /**
     * 获取会话等级
     * @return
     */
    int getGroupLevel();

    /**
     * 更新会话等级
     * @param level
     * @param callback
     */
    void updateGrouLevel(int level, IAGCallback<Void> callback);

    /**
     * 获取会话最多人数限制
     * @return
     */
    int getMemberLimit();

    /**
     * 更新会话最多人数限制
     * @param limit
     * @param callback
     */
    void updateMemberLimit(int limit, IAGCallback<Void> callback);

    /**
     * 更新会话名字
     * @param name 新名字
     * @param message 消息
     * @param callback 回调器
     */
    void updateName(String name, IAGMessage message, IAGCallback<Void> callback);

    /**
     * 更新所有者
     * @param ownerId
     * @param message
     * @param callback
     */
    void updateWoner(long ownerId, IAGMessage message, IAGCallback<Void> callback);

    /**
     * 是否只有所有者可以编辑
     * @return
     */
    boolean getOnlyOwnerModifiable();

    /**
     * 设置仅所有者可以修改
     * @param OnlyOwnerModifiable
     * @param message
     * @param callback
     */
    void setOnlyOwnerModifiable(boolean OnlyOwnerModifiable, IAGMessage message, IAGCallback<Void> callback);

    /**
     * 更新图标
     * @param icon
     * @param message
     * @param callback
     */
    void updateIcon(String icon, IAGMessage message, IAGCallback<Void> callback);

    /**
     * 静默退出
     * @param message
     * @param callback
     */
    void quitSlient(IAGMessage message, IAGCallback<Void> callback);

    /**
     * 增加未读数量
     * @param count
     */
    void addUnreadCount(int count);

    /**
     * 更新描述
     * @param desc
     */
    void updateDesc(String desc, IAGMessage message, IAGCallback<Void> callback);
    /**
     * 重置未读数量
     */
    void resetUnreadCount();

    /**
     * 获取某一条消息
     * @param messageId
     * @param callback
     */
    void getMessage(long messageId, IAGCallback<IAGMessage> callback);

    /**
     * 清除数据
     * @param callback
     */
    void clear(IAGCallback<Void> callback);

    /**
     * 会话是否有效
     * @return
     */
    boolean isValid();

    ConversationStatus status();


    public static enum TypingCommand {
        TYPING(0),
        CANCEL(1),
        UNKNOWN(-1);

        public final int value;

        private TypingCommand(int value) {
            this.value = value;
        }

        public int typeValue() {
            return this.value;
        }

        public static TypingCommand fromValue(int value) {
            TypingCommand[] arr = values();
            int len = arr.length;

            for(int i = 0; i < len; ++i) {
                TypingCommand t = arr[i];
                if(t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }

    public static enum TypingType {
        TEXT(0),
        IMAGE(1),
        AUDIO(2),
        VIDEO(3),
        UNKNOWN(-1);

        public final int value;

        private TypingType(int value) {
            this.value = value;
        }

        public int typeValue() {
            return this.value;
        }

        public static TypingType fromValue(int value) {
            TypingType[] arr = values();
            int len = arr.length;

            for(int i = 0; i < len; ++i) {
                TypingType t = arr[i];
                if(t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }

    public static enum ConversationStatus {
        NORMAL(0),
        QUIT(1),
        KICKOUT(2),
        OFFLINE(3),
        HIDE(4),
        DISBAND(5);

        public final int value;

        private ConversationStatus(int value) {
            this.value = value;
        }

        public int typeValue() {
            return this.value;
        }

        public static ConversationStatus fromValue(int value) {
            ConversationStatus[] arr = values();
            int len = arr.length;

            for(int i= 0; i < len; ++i) {
                ConversationStatus t = arr[i];
                if(t.typeValue() == value) {
                    return t;
                }
            }

            return NORMAL;
        }
    }

    public static class ConversationType {
        public static final int UNKNOWN = 0;
        public static final int CHAT = 1;
        public static final int GROUP = 2;

        public ConversationType() {
        }
    }

}