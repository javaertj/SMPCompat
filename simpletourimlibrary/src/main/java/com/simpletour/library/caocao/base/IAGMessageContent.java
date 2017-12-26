package com.simpletour.library.caocao.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.simpletour.library.caocao.base
 * 描述：消息具体数据
 * 创建者：yankebin
 * 日期：2017/5/12
 */
public interface IAGMessageContent extends Serializable {
     int type();

    public interface MultiMessageContent extends IAGMessageContent {
        List<IAGMessageContent> contents();

        int size();

        void add(IAGMessageContent var1);

        void remove(IAGMessageContent var1);
    }

    public interface CustomMessageContent extends IAGMessageContent.MediaContent {
        int customType();

        Map<String, String> extension();
    }

    public interface LinkedContent extends IAGMessageContent.MediaContent {
        String title();

        String text();

        String picUrl();

        Map<String, String> extension();
    }

    public interface FileContent extends IAGMessageContent.MediaContent {
        String fileName();

        String fileType();
    }

    public interface AudioContent extends IAGMessageContent.MediaContent {
        long duration();

        List<Integer> volumns();

        byte[] getData();
    }

    public interface ImageContent extends IAGMessageContent.MediaContent {
        int picType();

        int getWidth();

        int getHeight();

        int fileType();

        byte[] getData();

        int getOrientation();
    }

    public interface MediaContent extends IAGMessageContent {
        String url();

        long size();
    }

    public interface TextContent extends IAGMessageContent {
        String text();

        String templateId();

        List<String> templateData();
    }

    public static class MessageTemplate {
        public static final String CREATE_CONVERSATION = "im_start_chat";
        public static final String ADD_MEMBER = "im_add_member";
        public static final String REMOVE_MEMBER = "im_rm_member";
        public static final String UPDATE_CONVERSATION_TITLE = "im_update_group_title";
        public static final String QUIT_CONVERSATION = "im_quit_group";
        public static final String CREATE_SECURED_CONVERSATION = "im_start_secured_chat";
        public static final String CHANGE_GROUP_OWNER = "im_group_owner_changed";

        public MessageTemplate() {
        }
    }

    public static class FileType {
        public static final int UNKNOWN = -1;
        public static final int WEBP = 1;
        public static final int PNG = 2;
        public static final int JPG = 3;
        public static final int GIF = 4;

        public FileType() {
        }
    }

    public static class MessageContentType {
        public static final int UNKNOWN = -1;
        public static final int TEXT = 1;
        public static final int IMAGE = 2;
        public static final int AUDIO = 3;
        public static final int FILE = 4;
        public static final int LINKED = 102;

        public MessageContentType() {
        }
    }
}