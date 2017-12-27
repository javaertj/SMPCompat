package com.simpletour.app.sample.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.drivingassisstantHouse.library.tools.SLog;
import com.drivingassisstantHouse.library.tools.ToolNetwork;

/**
 * 包名：com.simpletour.app.sample.app
 * 描述：程序入口
 * 创建者：yankebin
 * 日期：2017/12/27
 */

public class SampleApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToolNetwork.getInstance().init(this);
        SLog.d("is network connected : " + ToolNetwork.getInstance().isConnected());
    }
}
