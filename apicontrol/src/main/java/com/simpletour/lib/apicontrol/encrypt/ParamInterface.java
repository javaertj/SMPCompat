package com.simpletour.lib.apicontrol.encrypt;

import java.util.List;
import java.util.Map;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：数据排序接口
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public interface ParamInterface {

	public List<Map.Entry<?, ?>> sortParam(Map<String, Object> params);
}