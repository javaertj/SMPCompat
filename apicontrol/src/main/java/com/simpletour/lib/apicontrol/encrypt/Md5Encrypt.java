package com.simpletour.lib.apicontrol.encrypt;

import android.util.Log;

import com.simpletour.lib.apicontrol.sign.MD5;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：MD5加密
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public class Md5Encrypt implements EncryptInter {

	@Override
	public String getEncrypt(String url,String... param) {
		
		StringBuilder builder=new StringBuilder();
		builder.append(url);
		builder.append(STR_ASK);
		for (String string : param) {
			builder.append(string);
		}		 
		try {
			Log.d("Md5Encrypt" , builder.toString());
			return MD5.encrypt32byte(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "";
	}

	@Override
	public EncryptType getType() {
		return EncryptType.MD5;
	}

	
}