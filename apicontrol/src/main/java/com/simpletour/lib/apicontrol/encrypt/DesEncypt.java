package com.simpletour.lib.apicontrol.encrypt;


import com.simpletour.lib.apicontrol.sign.DES;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：DES加密
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public class DesEncypt implements EncryptInter {

	// 需要提供密钥,另外需要将url放入param中，且作为第一个参数，记得添加？来拼接成完整的url

	@Override
	public String getEncrypt(String key, String... param) {
		StringBuilder builder = new StringBuilder();
		for (String string : param) {
			builder.append(string);
		}
		try {
			return DES.encrypt(key, builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public EncryptType getType() {
		return EncryptType.DES;
	}

}