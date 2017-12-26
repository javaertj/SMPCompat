package com.simpletour.lib.apicontrol.encrypt;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：数据组合接口实现
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public class ParamCombination implements ParamCombinationInter {

	@Override
	public String combinationParams(Map<?, ?> map, String token) {
		StringBuilder builder = new StringBuilder();
		if (map != null) {

			Iterator<?> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<?, ?> en = (Map.Entry<?, ?>) iter.next();
				Object key = en.getKey();
				Object val = en.getValue();
				builder.append(key.toString());
				builder.append(EncryptInter.STR_ASSIGN);
				builder.append(val.toString());
				builder.append(EncryptInter.STR_CONN);

			}
		}
		builder.append(appendCommonStr(token));
		return builder.toString();
	}

	private String appendCommonStr(String token) {
		StringBuilder builder = new StringBuilder();
		builder.append(EncryptInter.STR_TOKEN);
		builder.append(EncryptInter.STR_ASSIGN);
		if (token == null) {
			builder.append("");
		} else {
			builder.append(token);
		}
		builder.append(EncryptInter.STR_CONN);
		builder.append(EncryptInter.STR_KEY);
		builder.append(EncryptInter.STR_ASSIGN);
		builder.append(EncryptInter.APPLICATION_KEY);
		return builder.toString();
	}

	@Override
	public String combinationParams(List<Map.Entry<?, ?>> list, String token) {
		StringBuilder builder = new StringBuilder();
		if (list != null) {
			for (Map.Entry<?, ?> entry : list) {
				builder.append(entry.getKey().toString());
				builder.append(EncryptInter.STR_ASSIGN);
				builder.append(null == entry.getValue()?"":entry.getValue().toString());
				builder.append(EncryptInter.STR_CONN);
			}
		}
		builder.append(appendCommonStr(token));
		return builder.toString();
	}

}