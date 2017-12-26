package com.simpletour.lib.apicontrol.encrypt;

import com.simpletour.lib.apicontrol.sign.Base64;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：BASE	64加密
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public class Base64Encrypt implements EncryptInter {

	@Override
	public String getEncrypt(String url, String... param) {
		StringBuilder builder=new StringBuilder();
		builder.append(url);
		builder.append(STR_ASK);
		for (String string : param) {
			builder.append(string);
		}		 
		try {
			return Base64.encode(builder.toString().getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}		 
		return "";
	}

	@Override
	public EncryptType getType() {
		return EncryptType.BASE64;
	}

}