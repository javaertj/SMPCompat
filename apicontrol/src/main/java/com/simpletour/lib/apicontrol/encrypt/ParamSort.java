package com.simpletour.lib.apicontrol.encrypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：加密数据排序实现
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public class ParamSort implements ParamInterface {

	@Override
	public List<Map.Entry<?, ?>> sortParam(Map<String, Object> params) {

		List<Map.Entry<?, ?>> list = new ArrayList<Map.Entry<?, ?>>(
				params.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<?, ?>>() {
			@Override
			public int compare(Map.Entry<?, ?> o1, Map.Entry<?, ?> o2) {

				return o1.getKey().toString().compareTo(o2.getKey().toString());
			}
		});
		return list;
	}

}