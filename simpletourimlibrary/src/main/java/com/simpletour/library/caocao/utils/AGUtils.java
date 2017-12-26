package com.simpletour.library.caocao.utils;

import android.text.TextUtils;

import com.simpletour.library.caocao.AGAuthService;
import com.simpletour.library.caocao.base.IAGCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 包名：com.simpletour.library.caocao.utils
 * 描述：一般工具类
 * 创建者：yankebin
 * 日期：2017/5/5
 */

public class AGUtils {

    private static final Random random = new Random(System.currentTimeMillis());
    private static final AtomicInteger atomicInteger = new AtomicInteger();

    private static final int BASE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length();
    private static final char[] lz = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int[] lA = new int[123];

    public static boolean isValidMessageId(long messageId) {
        return messageId <= 0L;
    }

    public static boolean isLogin(IAGCallback<?> callback) {
        boolean isLogin = AGAuthService.getInstance().isLogin();
        if (!isLogin) {
            AGCallbackUtils.onError(callback, "4003", "NOT_LOGIN_ERR");
        }

        return isLogin;
    }


    public static String create(long number) {
        if (number < 0L) {
            throw new IllegalArgumentException("Number(Base62) must be positive: " + number);
        } else if (number == 0L) {
            return "0";
        } else {
            StringBuilder buf;
            for (buf = new StringBuilder(); number != 0L; number /= (long) BASE) {
                buf.append(lz[(int) (number % (long) BASE)]);
            }

            return buf.reverse().toString();
        }
    }

    static {
        int i;
        for (i = 0; i < 122; ++i) {
            lA[i] = -1;
        }

        for (i = 0; i < BASE; lA[lz[i]] = i++) {

        }

    }

    public static synchronized String createLocalId() {
        atomicInteger.compareAndSet(238328, 0);
        return create(System.currentTimeMillis()) + create((long) atomicInteger.incrementAndGet());
    }

    public static long createId() {
        return -(System.currentTimeMillis() * 1000L + (long) random.nextInt(1000));
    }


    public static boolean isLocalUrl(String url) {
        return url != null && (url.startsWith("/") || url.toLowerCase().startsWith("file:"));
    }

    public static List<String> toList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        } else {
            int len = jsonArray.length();
            ArrayList list = new ArrayList(len);

            for (int i = 0; i < len; ++i) {
                list.add(jsonArray.optString(i));
            }

            return list;
        }
    }

    public static <K, V> String toJson(Map<K, V> map) {
        if (map != null && !map.isEmpty()) {
            JSONObject object = new JSONObject();

            try {
                Iterator e = map.entrySet().iterator();

                while (e.hasNext()) {
                    Map.Entry entry = (Map.Entry) e.next();
                    Object key = entry.getKey();
                    if (key != null) {
                        object.put(key.toString(), entry.getValue());
                    }
                }
            } catch (JSONException var5) {
                var5.printStackTrace();
            }

            return object.toString();
        } else {
            return null;
        }
    }

    public static Map<String, String> fromJson(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        } else {
            HashMap map = new HashMap();

            try {
                JSONObject e = new JSONObject(jsonString);
                Iterator it = e.keys();

                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = e.getString(key);
                    map.put(key, value);
                }
            } catch (JSONException var6) {
                var6.printStackTrace();
            }

            return map;
        }
    }

    public static int toInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        } else {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException var2) {
                return 0;
            }
        }
    }

    public static long toLong(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0L;
        } else {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException var2) {
                return 0L;
            }
        }
    }

    public static int intValue(Integer i, int defaultValue) {
        return i == null ? defaultValue : i.intValue();
    }

    public static int intValue(Integer i) {
        return i == null ? 0 : i;
    }

    public static long longValue(Long value) {
        return null == value ? 0 : value;
    }

    /**
     * MD5算法
     *
     * @param input 字符串参数
     * @return
     */
    public static byte[] getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(stringToByte(input));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串转换成UTF-8字节数组,
     *
     * @param s 要转换的字符串
     * @return 转换后的字节数组，注意如果s为空或者空串，将返回null
     */
    public static byte[] stringToByte(String s) {
        byte[] result = null;
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        try {
            result = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
