package com.simpletour.library.caocao;

import android.text.TextUtils;

import com.simpletour.library.caocao.base.IAGMessageContent;
import com.simpletour.library.caocao.utils.AGUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.againstalone.lib.imkit
 * 描述：消息体实现类,目前先只支持4种
 * 创建者：yankebin
 * 日期：2017/5/17
 */

public class AGMessageContent implements IAGMessageContent {
    protected int type;

    public AGMessageContent(int type) {
        this.type = type;
    }

    @Override
    public int type() {
        return type;
    }

    protected JSONObject createJsonObject() {
        return null;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = createJsonObject();
        return null == jsonObject ? null : jsonObject.toString();
    }

    static String integerArrayToString(List<Integer> volumns) {
        if (volumns != null && !volumns.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(volumns.get(0));
            int size = volumns.size();

            for (int i = 1; i < size; ++i) {
                sb.append(",");
                Integer volumn = (Integer) volumns.get(i);
                sb.append(AGUtils.intValue(volumn, 0));
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    static List<Integer> stringToIntegerArry(String volumnString) {
        if (TextUtils.isEmpty(volumnString)) {
            return null;
        } else {
            ArrayList list = new ArrayList();
            String[] volumns = volumnString.split(",");
            String[] arr = volumns;
            int len$ = volumns.length;

            for (int i = 0; i < len$; ++i) {
                String volumn = arr[i];
                list.add(Integer.valueOf(AGUtils.toInt(volumn)));
            }

            return list;
        }
    }

    public static AGMessageContent createFromJsonString(int contentType, String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return new AGMessageContent(-1);
        }
        JSONObject object = null;
        try {
            object = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return createFromJson(contentType, object);
    }

    public static AGMessageContent createFromJson(int defaultContentType, JSONObject json) {
        if (null == json) {
            return new AGMessageContent(-1);
        }
        int type = json.optInt("tp", defaultContentType);
        Object content = null;
        switch (type) {
            case MessageContentType.TEXT:
                content = TextContentImpl.create(json);
                break;
            case MessageContentType.IMAGE:
                content = ImageContentImpl.create(json);
                break;
            case MessageContentType.AUDIO:
                content = AudioContentImpl.create(json);
                break;
            case MessageContentType.LINKED:
                content = LinkedContentImpl.create(json);
                break;
            default:
                break;
        }

        return (AGMessageContent) content;
    }

    public abstract static class MediaContentImpl extends AGMessageContent implements MediaContent {
        protected String mUrl;
        protected long mSize;

        public MediaContentImpl(int type, String url, long size) {
            super(type);
            this.mUrl = url;
            this.mSize = size;
        }

        public String url() {
            return this.mUrl;
        }

        public long size() {
            return this.mSize;
        }
    }

    public static class AudioContentImpl extends MediaContentImpl implements AudioContent {
        long duration;
        List<Integer> volumns;
        private byte[] data;

        public long duration() {
            return this.duration;
        }

        public List<Integer> volumns() {
            return this.volumns;
        }

        public byte[] getData() {
            return this.data;
        }

        public AudioContentImpl(String mediaUrl, long duration, List<Integer> volumns) {
            super(MessageContentType.AUDIO, mediaUrl, 0L);
            this.duration = duration;
            this.volumns = volumns;
        }

        public AudioContentImpl(String mediaUrl, long duration, List<Integer> volumns, byte[] data) {
            super(3, mediaUrl, 0L);
            this.duration = duration;
            this.volumns = volumns;
            this.data = data;
        }

        protected JSONObject createJsonObject() {
            JSONObject object = new JSONObject();

            try {
                object.put("url", this.mUrl);
                object.put("duration", this.duration);
                object.put("volumns", integerArrayToString(this.volumns));
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private static AudioContentImpl create(JSONObject object) {
            if (object == null) {
                return null;
            }
            String url = object.optString("url");
            long duration = object.optLong("duration");
            List volumns = stringToIntegerArry(object.optString("volumns"));
            return new AudioContentImpl(url, duration, volumns);
        }
    }


    public static class LinkedContentImpl extends MediaContentImpl implements LinkedContent {
        private String title;
        private String text;
        private String picUrl;
        private Map<String, String> mExtension;

        public LinkedContentImpl(String url, String title, String text, String picUrl, Map<String, String> extension) {
            super(MessageContentType.LINKED, url, 0L);
            this.title = title;
            this.text = text;
            this.picUrl = picUrl;
            this.mExtension = extension;
        }

        public String title() {
            return this.title;
        }

        public String text() {
            return this.text;
        }

        public String picUrl() {
            return this.picUrl;
        }

        public Map<String, String> extension() {
            return this.mExtension;
        }

        protected JSONObject createJsonObject() {
            JSONObject object = new JSONObject();
            try {
                object.put("tp", this.type);
                object.put("url", this.picUrl);
                object.put("txt", this.text);
                object.put("title", this.title);
                object.put("picUrl", this.picUrl);
                object.put("ext", AGUtils.toJson(this.mExtension));
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private static LinkedContentImpl create(JSONObject object) {
            if (object == null) {
                return null;
            }
            String url = object.optString("url");
            String title = object.optString("title");
            String text = object.optString("txt");
            String picUrl = object.optString("picUrl");
            Map extension = AGUtils.fromJson(object.optString("ext"));
            return new LinkedContentImpl(url, title, text, picUrl, extension);
        }
    }


    public static class ImageContentImpl extends MediaContentImpl implements ImageContent {
        private int picType;
        private int fileType;
        private byte[] data;
        private int picOrientation;

        public int picType() {
            return this.picType;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        public ImageContentImpl(String mediaUrl, long picSize, int picType, int orientation) {
            this(mediaUrl, picSize, picType, 0, (byte[]) null, orientation);
        }

        public ImageContentImpl(String mediaUrl, long picSize, int picType, int fileType, byte[] data, int orientation) {
            super(MessageContentType.IMAGE, mediaUrl, picSize);
            this.picType = picType;
            this.fileType = fileType;
            this.data = data;
            this.picOrientation = orientation;
        }

        protected JSONObject createJsonObject() {
            JSONObject object = new JSONObject();

            try {
                object.put("url", this.mUrl);
                object.put("size", this.mSize);
                object.put("picType", this.picType);
                object.put("fileType", this.fileType);
                object.put("picOrientation", this.picOrientation);
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private static ImageContentImpl create(JSONObject object) {
            if (object == null) {
                return null;
            }
            String url = object.optString("url");
            long picSize = object.optLong("size");
            int picType = object.optInt("picType");
            int fileType = object.optInt("fileType");
            int orientation = object.optInt("picOrientation");
            return new ImageContentImpl(url, picSize, picType, fileType, (byte[]) null, orientation);
        }

        public int fileType() {
            return this.fileType;
        }

        public byte[] getData() {
            return this.data;
        }

        public int getOrientation() {
            return this.picOrientation;
        }
    }


    public static class TextContentImpl extends AGMessageContent implements TextContent {
        private String text;
        private String templateId;
        private List<String> templateData;

        public TextContentImpl(String text, String templateId, List<String> templateData) {
            super(MessageContentType.TEXT);
            this.text = text;
            this.templateId = templateId;
            this.templateData = templateData;
        }

        public String text() {
            return this.text;
        }

        public String templateId() {
            return this.templateId;
        }

        public List<String> templateData() {
            return this.templateData;
        }

        protected JSONObject createJsonObject() {
            JSONObject object = new JSONObject();

            try {
                object.put("txt", this.text);
                if (!TextUtils.isEmpty(this.templateId)) {
                    object.put("tplId", this.templateId);
                }

                if (this.templateData != null) {
                    object.put("tplData", new JSONArray(this.templateData));
                }

                return object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private static TextContentImpl create(JSONObject object) {
            if (object == null) {
                return null;
            }
            String text = object.optString("txt", (String) null);
            String templateId = object.optString("tplId", (String) null);
            List templateData = AGUtils.toList(object.optJSONArray("tplData"));
            return new TextContentImpl(text, templateId, templateData);
        }
    }
}
