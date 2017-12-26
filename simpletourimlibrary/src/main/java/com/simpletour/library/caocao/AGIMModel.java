package com.simpletour.library.caocao;

import android.util.Log;

import com.simpletour.library.caocao.base.IAGAuthService;
import com.simpletour.library.caocao.base.IAGConversationService;
import com.simpletour.library.caocao.base.IAGMessageBuilder;
import com.simpletour.library.caocao.base.IAGMessageService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 包名：com.simpletour.library.caocao
 * 描述：模型存储类
 * 创建者：yankebin
 * 日期：2017/5/8
 */

final class AGIMModel {
    private static final String TAG = AGIMModel.class.getName();
    private final Map<Class<?>, Object> mServiceCache = new ConcurrentHashMap<>();
    private boolean isInitialized;

    private AGIMModel() {
    }

    public static AGIMModel getInstance() {
        return AGIMModelInstance.instance;
    }

    void init() {
        if (isInitialized) {
            Log.w(TAG, "ReInitialize AGIMModel");
            return;
        }
        isInitialized = true;
        mServiceCache.put(IAGAuthService.class, AGAuthService.getInstance());
        mServiceCache.put(IAGMessageService.class, AGMessageService.getInstance());
        mServiceCache.put(IAGConversationService.class, AGConversationService.getInstance());
        mServiceCache.put(IAGMessageBuilder.class, new AGMessageBuilder());
    }

    /**
     * 销毁
     */
    void destroy() {
        isInitialized = false;
        AGConversationService.getInstance().destroy();
        AGAuthService.getInstance().destroy();
        synchronized (mServiceCache) {
            mServiceCache.clear();
        }
    }

    <T> T getService(Class<?> clazz) {
        if (!isInitialized) {
            throw new IllegalStateException("AGIMModel not ready yet");
        }
        Object service = mServiceCache.get(clazz);
        if (null != service) {
            return (T) service;
        }

        return null;
    }

    private static class AGIMModelInstance {
        private static AGIMModel instance = new AGIMModel();
    }
}
