package com.simpletour.library.caocao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.simpletour.library.caocao.base.IAGConnectionStatusCallback;
import com.simpletour.library.caocao.base.IAGIMEngine;

import java.lang.ref.WeakReference;

/**
 * 包名：com.simpletour.library.caocao
 * 描述：IM服务提供引擎
 * 创建者：yankebin
 * 日期：2017/5/8
 */

 public final class AGIMEngine implements IAGIMEngine {
    private static final String TAG = AGIMEngine.class.getName();
    private boolean isInitialized;
    private WeakReference<Context> context;

    private AGIMEngine() {

    }

    @Override
    public <T> T getService(Class<?> clazz) {
        checkConfiguration();
        return AGIMModel.getInstance().getService(clazz);
    }

    public void init(@NonNull Context context) {
        if (!isInitialized) {
            isInitialized = true;
            this.context = new WeakReference<>(context);
            AGClientEngine.getInstance().init();
            AGIMModel.getInstance().init();
        } else {
            Log.w(TAG, "reInit AGIMEngine");
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        AGIMModel.getInstance().destroy();
        AGClientEngine.getInstance().destroy();
    }

    /**
     * 加入一个socket连接状态回调器
     *
     * @param connectionStatusCallback socket连接状态回调器
     */
    public void registerSocketStatusCallback(IAGConnectionStatusCallback connectionStatusCallback) {
        checkConfiguration();
        AGClientEngine.getInstance().registerSocketStatusCallback(connectionStatusCallback);
    }

    /**
     * 移除一个socket连接状态回调器
     *
     * @param connectionStatusCallback socket连接状态回调器
     */
    public void unRegisterSocketStatusCallback(IAGConnectionStatusCallback connectionStatusCallback) {
        checkConfiguration();
        AGClientEngine.getInstance().unRegisterSocketStatusCallback(connectionStatusCallback);
    }

    /**
     * 获取上下文
     *
     * @return
     */
    protected Context getContext() {
        return context.get();
    }

    /**
     *
     */
    private void checkConfiguration() {
        if (!isInitialized) {
            throw new RuntimeException("please call method init(Context context) first");
        }
    }

    public static AGIMEngine getInstance() {
        return AGIMEngineInstance.instance;
    }

    private static class AGIMEngineInstance {
        private static AGIMEngine instance = new AGIMEngine();
    }
}
