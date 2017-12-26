package com.simpletour.library.caocao;

import com.simpletour.library.caocao.base.IAGCallback;
import com.simpletour.library.caocao.base.IAGMessageProxy;
import com.simpletour.library.caocao.base.IAGMessageService;
import com.simpletour.library.caocao.base.IAGOnReceiveChatMessageCallback;

/**
 * 包名：com.simpletour.library.caocao
 * 描述：消息服务
 * 创建者：yankebin
 * 日期：2017/5/5
 */

public final class AGMessageService implements IAGMessageService {
    public static AGMessageService getInstance() {
        return AGIMServiceInstance.instance;
    }

    private static class AGIMServiceInstance {
        private static AGMessageService instance = new AGMessageService();
    }

    @Override
    public void registerMessageCallback(IAGOnReceiveChatMessageCallback agCallback) {
        AGClientEngine.getInstance().registerMessageCallback(agCallback);
    }

    @Override
    public void unRegisterMessageCallback(IAGOnReceiveChatMessageCallback agCallback) {
        AGClientEngine.getInstance().unRegisterMessageCallback(agCallback);
    }

    @Override
    public void removeMessages(IAGCallback<Void> callback, String cid, Long... messageIds) {

    }

    @Override
    public void removeLocalMessages(IAGCallback<Void> callback, String conversationId, Long... messageIds) {

    }

    @Override
    public void setMessageProxy(IAGMessageProxy proxy) {

    }
}
