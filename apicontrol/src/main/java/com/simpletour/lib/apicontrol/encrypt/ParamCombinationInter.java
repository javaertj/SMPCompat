package com.simpletour.lib.apicontrol.encrypt;

import java.util.List;
import java.util.Map;

/**
 * 包名：com.simpletour.lib.apicontrol.encrypt
 * 描述：数据组合接口
 * 创建者：yankebin
 * 日期：2017/2/28
 */
public interface ParamCombinationInter {

    String combinationParams(Map<?, ?> map, String token);

    String combinationParams(List<Map.Entry<?, ?>> list, String token);


}