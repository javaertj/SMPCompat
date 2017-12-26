package com.simpletour.lib.apicontrol.encrypt;


import android.util.Log;

import com.simpletour.lib.apicontrol.RetrofitApi;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 包名： com.simpletour.lib.apicontrol.encrypt
 * 描述：<BR/>
 * 1.将接口需要的参数进行ASCII码从小到大排序（字典序）<BR/>
 * 2.使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串strA<BR/>
 * 3.在strA后拼接当前请求的token和全局定义的key， 既strA = strA + "&token=" + token + "&key=" + KEY<BR/>
 * 4.将第三步中得到的strA前面加上当前请求接口的全路径URL并以?连接，得到最终待签名的tempStr 签名字符串sign = MD5(tempStr)<BR/>
 * 5.将最终得到的签名字符串和请求参数一起放入到请求中，其key为sign <BR/>
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public class SignUtil {
    private static final String TAG = SignUtil.class.getName();

    /**
     * 为有效保证数据请求来源及有效性，服务端在接收客户端的资访问请求时，会对请求进行签名校验，固需要客户端对指定的请求进行签名
     *
     * @param url    请求的url地址 http://192.168.4.82:9080/rest/{version}/user/login （不带问号）
     * @param token  token，可以为空（null时，将自动补充空串进行占位）
     * @param params 请求参数(不要包含时间戳)
     **/
    private static String getMd5Sign(String url, String token,
                                     Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        //所有的请求都必须有时间戳
        params.put(EncryptInter.STR_TIMESTAP, Calendar.getInstance(Locale.CHINA).getTimeInMillis());
        ParamInterface sort = new ParamSort();
        ParamCombinationInter combination = new ParamCombination();
        String com = combination.combinationParams(sort.sortParam(params),
                token);
        Log.d(TAG, "sign : " + com);
        EncryptInter encry = new Md5Encrypt();
        return encry.getEncrypt(url, com).trim();

    }

    /**
     * 为有效保证数据请求来源及有效性，服务端在接收客户端的资访问请求时，会对请求进行签名校验，固需要客户端对指定的请求进行签名
     *
     * @param endPoint  请求的url地址指向 /user/login
     * @param needToken 是否需要token
     * @param params    请求参数(不需要包含时间戳)
     * @return
     */
    public static String getMd5Sign(String endPoint, boolean needToken, Map<String, Object> params) {
        String url = RetrofitApi.getInstance().getBaseUrl() + endPoint;
        String token = needToken ? RetrofitApi.getInstance().getToken() : null;
        return getMd5Sign(url, token, params);

    }

    /**
     * 为有效保证数据请求来源及有效性，服务端在接收客户端的资访问请求时，会对请求进行签名校验，固需要客户端对指定的请求进行签名
     * <h6>不再需要手动加入签名字符串</h6>
     *
     * @param endPoint  请求的url地址指向 /user/login
     * @param needToken 是否需要token
     * @param params    请求参数(不需要包含时间戳)
     * @return
     */
    public static void doMd5Sign(String endPoint, boolean needToken, Map<String, Object> params) {
        String url = RetrofitApi.getInstance().getBaseUrl() + endPoint;
        String token = needToken ? RetrofitApi.getInstance().getToken() : null;
        params.put("sign", getMd5Sign(url, token, params));
    }
}
