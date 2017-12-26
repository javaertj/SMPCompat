package com.simpletour.lib.autoupdate;

import com.simpletour.lib.autoupdate.model.SResponseBean;
import com.simpletour.lib.autoupdate.model.UpdateBean;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：检测更新的接口
 * 创建者：yankebin
 * 日期：2016/5/18
 */
public interface IUpdate {
    @POST(UpdateConfig.URL_FOR_CHECK_UPDATE)
    Call<SResponseBean<UpdateBean>> checkUpdate(@Body HashMap<String, Object> map);
}
