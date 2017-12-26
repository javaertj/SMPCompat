package com.simpletour.library.caocao.base;

import java.io.Serializable;
import java.util.Map;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：消息
 * 创建者：yankebin
 * 日期：2017/5/12
 */

public interface IAGMessage extends Serializable, Comparable<IAGMessage> {
    /**
     * 会话id
     *
     * @return
     */
    IAGConversation conversation();

    /**
     * 发送消息
     *
     * @param conversation      会话
     * @param messageIAGCallback 消息回调
     */
    void sendTo(IAGConversation conversation, IAGCallback<IAGMessage> messageIAGCallback);

    /**
     * 消息id
     *
     * @return
     */
    long messageId();

    /**
     * 发送者id
     *
     * @return
     */
    long senderId();

    /**
     * 消息类型
     *
     * @return
     */
    MessageType messageType();

    /**
     * 创建者
     *
     * @return
     */
    CreatorType creatorType();

    /**
     * 创建时间
     *
     * @return
     */
    long createdAt();

    /**
     * 未读人数
     *
     * @return
     */
    int unReadCount();

    /**
     * 已读人数
     *
     * @return
     */
    int receiverCount();

    /**
     * 是否全部已读
     *
     * @return
     */
    boolean allReceiversRead();

    /**
     * 我是否已读
     *
     * @return
     */
    boolean iHaveRead();

    /**
     * 消息状态
     *
     * @return
     */
    MessageStatus status();

    /**
     * 消息体
     *
     * @return
     */
    IAGMessageContent messageContent();

    /**
     * 是否是@消息
     *
     * @return
     */
    boolean isAt();

    /**
     * [@]的人员系信息
     *
     * @return
     */
    Map<Long, String> atOpenIds();

    /**
     * 上报消息已读
     */
    void read();

    /**
     * 删除消息
     *
     * @param callback
     */
    void delete(IAGCallback<Void> callback);

    String localId();

    long lastModify();

    long sentLocalTime();

    public static enum ReadStatus {
        UNDELIVERED(0),
        UNREAD(1),
        READ(2),
        UNKNOWN(-1);

        private int type;

        private ReadStatus(int type) {
            this.type = type;
        }

        public int typeValue() {
            return this.type;
        }

        public static ReadStatus fromValue(int value) {
            ReadStatus[] arr = values();
            int len = arr.length;

            for (int i = 0; i < len; ++i) {
                ReadStatus t = arr[i];
                if (t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }

    public static enum MessageType {
        COMMON(1),
        SILENT(3),
        UNKNOWN(-1);

        private int type;

        private MessageType(int type) {
            this.type = type;
        }

        public int typeValue() {
            return this.type;
        }

        public static MessageType fromValue(int value) {
            MessageType[] arr = values();
            int len = arr.length;

            for (int i = 0; i < len; ++i) {
                MessageType t = arr[i];
                if (t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }

    public static enum MessageStatus {
        OFFLINE(1),
        SENDING(2),
        SENT(3),
        DELETED(4),
        UNKNOWN(-1);

        private int type;

        private MessageStatus(int type) {
            this.type = type;
        }

        public int typeValue() {
            return this.type;
        }

        public static MessageStatus fromValue(int value) {
            MessageStatus[] arr = values();
            int len = arr.length;

            for (int i = 0; i < len; ++i) {
                MessageStatus t = arr[i];
                if (t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }

    public static enum CreatorType {
        SELF(1),
        SYSTEM(2),
        UNKNOWN(-1);

        private int type;

        private CreatorType(int type) {
            this.type = type;
        }

        public int typeValue() {
            return this.type;
        }

        public static CreatorType fromValue(int value) {
            CreatorType[] arr = values();
            int len = arr.length;

            for (int i = 0; i < len; ++i) {
                CreatorType t = arr[i];
                if (t.typeValue() == value) {
                    return t;
                }
            }

            return UNKNOWN;
        }
    }
}
