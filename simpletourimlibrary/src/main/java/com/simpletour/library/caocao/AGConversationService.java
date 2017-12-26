package com.simpletour.library.caocao;


import com.simpletour.library.caocao.base.IAGConversationService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 包名：com.simpletour.library.caocao
 * 描述：会话服务
 * 创建者：yankebin
 * 日期：2017/5/8
 */

public final class AGConversationService implements IAGConversationService {
    private final Map<String,AGConversation> conversationCache=new ConcurrentHashMap<>();

    public Map<String, AGConversation> getConversationCache() {
        return conversationCache;
    }

    private AGConversationService() {

    }

    /**
     * 销毁
     */
    void destroy(){
        synchronized (conversationCache){
            conversationCache.clear();
        }
    }

    public static AGConversationService getInstance() {
        return AGConversationServiceInstance.instance;
    }

    private static class AGConversationServiceInstance {
        private static AGConversationService instance = new AGConversationService();
    }

}
