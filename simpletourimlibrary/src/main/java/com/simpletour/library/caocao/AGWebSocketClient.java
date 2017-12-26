package com.simpletour.library.caocao;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.simpletour.library.caocao.base.AGProtocolType;
import com.simpletour.library.caocao.base.IAGConnectionStatusCallback;
import com.simpletour.library.caocao.base.IAGMessage;
import com.simpletour.library.caocao.base.IAGMessageSendStatusCallback;
import com.simpletour.library.caocao.base.IAGOnReceiveChatMessageCallback;
import com.simpletour.library.caocao.base.IAGRequestHandler;
import com.simpletour.library.caocao.config.AGIMConfig;
import com.simpletour.library.caocao.heart.AGHeartStateMachine;
import com.simpletour.library.caocao.model.AGHeartMessageModel;
import com.simpletour.library.caocao.model.AGHeartResponseMessageModel;
import com.simpletour.library.caocao.model.AGMessageModel;
import com.simpletour.library.caocao.model.AGSendMessageModel;
import com.simpletour.library.caocao.model.AGSendResultModel;
import com.simpletour.library.rxwebsocket.RXWebSocketProvider;
import com.simpletour.library.rxwebsocket.ReconnectStrategyAble;
import com.simpletour.library.rxwebsocket.WebSocketSubscriber;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.WebSocket;
import okio.ByteString;


/**
 * 包名：com.simpletour.library.caocao
 * 描述：webSocketClient
 * 创建者：yankebin
 * 日期：2017/11/28
 */

public final class AGWebSocketClient implements IAGMessageSendStatusCallback {

    private static final String TAG = AGWebSocketClient.class.getName();
    /**
     * 分发消息
     */
    private static final int ACTION_DISPATCH_RECEIVE_MESSAGE = 1000;
    /**
     * 分发连接状态
     */
    private static final int ACTION_DISPATCH_STATUS = ACTION_DISPATCH_RECEIVE_MESSAGE + 1;
    /**
     * 发送消息
     */
    private static final int ACTION_SEND_MESSAGE = ACTION_DISPATCH_RECEIVE_MESSAGE + 2;

    /**
     * 通知发送消息失败
     */
    private static final int ACTION_NOTIFY_SEND_MESSAGE_FAILED = ACTION_DISPATCH_RECEIVE_MESSAGE + 3;

    /**
     * 分发回应消息
     */
    private static final int ACTION_DISPATCH_RESPONSE_MESSAGE = ACTION_DISPATCH_RECEIVE_MESSAGE + 6;

    /**
     * 链接超时时间 (小时)
     */
    private static final int MIN_CONNECT_TIME_OUT = 240;//10天

    private final BlockingQueue<AGSendMessageModel> messageQueue = new LinkedBlockingQueue<>();
    private final List<IAGOnReceiveChatMessageCallback> messageCallbacks = new LinkedList<>();
    private final List<IAGConnectionStatusCallback> statusCallbacks = new LinkedList<>();
    private final Map<Long, AGMessageSession> messageSessions = new ConcurrentHashMap<>();
    private final Map<Long, IAGRequestHandler<AGSendResultModel, IAGMessage>> sendCallbacks
            = new ConcurrentHashMap<>();
    private final SocketHandler socketHandler;
    private boolean isDestroyed;
    private WebSocket mWebSocket;
    private AGHeartStateMachine heartStateMachine;


    AGWebSocketClient() {
        final String serviceUrl = "ws://".concat(AGIMConfig.IM_SERVICE_URL);
        RXWebSocketProvider.getInstance().setShowLog(AGIMConfig.ENABLE_LOG);
        RXWebSocketProvider.getInstance().addReconnectStrategyAble(serviceUrl, new DefaultReconnectStrategy(1000L));
        RXWebSocketProvider.getInstance().getWebSocketInfo(serviceUrl, MIN_CONNECT_TIME_OUT,
                TimeUnit.HOURS).subscribe(new WebSocketSubscriber() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket) {
                mWebSocket = webSocket;
                dispatchConnectStatus(IAGConnectionStatusCallback.AGConnectionStatus.ONLINE, 200, "");
                checkIfNeedRestartMessageQueen();
            }

            @Override
            public void onMessage(@NonNull String text) {
                resolveMessage(text);
            }

            @Override
            public void onMessage(@NonNull ByteString bytes) {
                resolveMessage(bytes.string(Charset.forName("UTF-8")));
            }

            @Override
            public void onClose(@NonNull String reason) {
                dispatchConnectStatus(IAGConnectionStatusCallback.AGConnectionStatus.OFF_LIEN, 201, reason);
            }

            @Override
            public void onError(@NonNull String error) {
                dispatchConnectStatus(IAGConnectionStatusCallback.AGConnectionStatus.ERROR, 202, error);
            }
        });
        HandlerThread handlerThread = new HandlerThread(getClass().getName());
        handlerThread.start();
        socketHandler = new SocketHandler(handlerThread.getLooper());
//        heartStateMachine = new AGHeartStateMachine(this);
    }

    /**
     * 加入一个消息回调器
     *
     * @param agCallback 消息回调器
     */
    void addMessageCallback(IAGOnReceiveChatMessageCallback agCallback) {
        synchronized (messageCallbacks) {
            if (!messageCallbacks.contains(agCallback)) {
                messageCallbacks.add(agCallback);
            }
        }
    }

    /**
     * 移除一个消息回调器
     *
     * @param agCallback 消息回调器
     */
    void removeMessageCallback(IAGOnReceiveChatMessageCallback agCallback) {
        synchronized (messageCallbacks) {
            if (messageCallbacks.contains(agCallback)) {
                messageCallbacks.remove(agCallback);
            }
        }
    }

    /**
     * 加入一个长连接状态回调器
     *
     * @param agCallback 长连接状态回调器
     */
    void addStatusCallback(IAGConnectionStatusCallback agCallback) {
        synchronized (statusCallbacks) {
            if (!statusCallbacks.contains(agCallback)) {
                statusCallbacks.add(agCallback);
            }
        }
    }

    /**
     * 移除一个长连接状态回调器
     *
     * @param agCallback 长连接状态回调器
     */
    void removeStatusCallback(IAGConnectionStatusCallback agCallback) {
        synchronized (statusCallbacks) {
            if (statusCallbacks.contains(agCallback)) {
                statusCallbacks.remove(agCallback);
            }
        }
    }

    /**
     * 解析消息
     *
     * @param message
     */
    private synchronized void resolveMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            throw new IllegalArgumentException("Invalid data ，stop resolve");
        }
        try {
            JSONObject jsonObject = new JSONObject(message);
            switch (jsonObject.optInt("protocolType")) {
                //服务器回应消息
                case AGProtocolType.TYPE_CHAT_RESPONSE_MESSAGE:
                    AGSendResultModel resultModel = new Gson().fromJson(message, AGSendResultModel.class);
                    uiHandler.obtainMessage(ACTION_DISPATCH_RESPONSE_MESSAGE, resultModel).sendToTarget();
                    break;
                //服务器转发消息
                case AGProtocolType.TYPE_CHAT_FORWARD_MESSAGE:
                    AGMessageModel AGMessageModel = new Gson().fromJson(message, AGMessageModel.class);
                    uiHandler.obtainMessage(ACTION_DISPATCH_RECEIVE_MESSAGE, AGMessageModel).sendToTarget();
                    break;
                default:
                    throw new RuntimeException("Invalid message received");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (AGIMConfig.ENABLE_LOG) {
                Log.e(TAG, "Convert message to json failed");
            }
        }
    }

    /**
     * 服务器回应消息
     *
     * @param resultModel
     */
    private synchronized void notifyResponseMessage(AGSendResultModel resultModel) {
        if (null == resultModel) {
            return;
        }
        synchronized (messageSessions) {
            final AGMessageSession messageSession = messageSessions.remove(resultModel.messageId);
            if (null == messageSession) {
                return;
            }
            messageSession.stopTask();
        }
        synchronized (sendCallbacks) {
            final IAGRequestHandler<AGSendResultModel, IAGMessage> callback = sendCallbacks.remove(resultModel.messageId);
            if (null == callback) {
                return;
            }
            callback.onSuccess(resultModel);
        }
    }

    /**
     * 服务器发送来聊天消息
     *
     * @param messageModel
     */
    private synchronized void notifyReceiveChatMessage(AGMessageModel messageModel) {
        if (null == messageModel || null == messageModel.baseMessage || null == messageModel.baseMessage.openIdEx) {
            return;
        }
        try {
            final AGMessage message = AGMessageFactory.buildMessage(messageModel,
                    messageModel.baseMessage.openIdEx.openId, AGConversationService.getInstance().
                            getConversationCache().get(messageModel.baseMessage.conversationId));
            synchronized (messageCallbacks) {
                for (IAGOnReceiveChatMessageCallback callback : messageCallbacks) {
                    callback.onReceiveMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 服务器发送来心跳消息
     *
     * @param messageModel
     */
    private synchronized void notifyReceiveHeartMessage(final AGHeartResponseMessageModel messageModel) {
        if (null == messageModel) {
            return;
        }
        if (null != heartStateMachine) {
        }
    }


    /**
     * 长连接状态回调分发
     *
     * @param status    状态
     * @param errorCode 错误码
     * @param reason    原因
     */
    private synchronized void dispatchConnectStatus(IAGConnectionStatusCallback.AGConnectionStatus
                                                            status, int errorCode, String reason) {
        ConnectStatus connectStatus = new ConnectStatus();
        connectStatus.errorCode = errorCode;
        connectStatus.reason = reason;
        connectStatus.status = status;
        uiHandler.obtainMessage(ACTION_DISPATCH_STATUS, connectStatus).sendToTarget();
    }

    /**
     * 分发消息发送状态
     *
     * @param sendMessage
     */
    private synchronized void dispatchSendMessageStatus(AGSendMessageModel sendMessage) {
        synchronized (sendCallbacks) {
            final IAGRequestHandler<AGSendResultModel, IAGMessage> callback = sendCallbacks.remove(sendMessage.messageId);
            if (null == callback) {
                return;
            }
            callback.onError("2009", "message sendChatMessage timeOut");
        }
    }

    /**
     * 长连接状态回调通知
     *
     * @param status 状态模型
     */
    private void notifyConnectStatus(ConnectStatus status) {
        synchronized (statusCallbacks) {
            if (statusCallbacks.isEmpty()) {
                return;
            }
            for (IAGConnectionStatusCallback callback : statusCallbacks) {
                callback.onStatusChanged(status.status, status.errorCode, status.reason);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param messageModel 消息体
     * @param callback     消息发送状态回调器
     */
    synchronized void sendChatMessage(AGSendMessageModel messageModel, IAGRequestHandler<AGSendResultModel, IAGMessage> callback) {
        synchronized (sendCallbacks) {
            if (!sendCallbacks.containsKey(messageModel.messageId)) {
                sendCallbacks.put(messageModel.messageId, callback);
            }
        }

        synchronized (messageQueue) {
            try {
                messageQueue.put(messageModel);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        socketHandler.obtainMessage(ACTION_SEND_MESSAGE).sendToTarget();
    }

    /**
     * 发送消息
     *
     * @param messageModel 消息体
     */
    private synchronized void sendChatMessage(AGSendMessageModel messageModel) {
        if (null == messageModel) {
            throw new IllegalArgumentException("Invalid AGSendMessageModel to sendChatMessage");
        }
        if (null == mWebSocket) {
            synchronized (sendCallbacks) {
                final IAGRequestHandler<AGSendResultModel, IAGMessage> callback = sendCallbacks.remove(messageModel.messageId);
                if (null != callback) {
                    callback.onError("2001", "Socket not connected");
                }
            }
            return;
        }

        String message = JSON.toJSONString(messageModel);
        mWebSocket.send(message);
        addSendSession(messageModel);
    }

    /**
     * 发送心跳消息
     *
     * @param messageModel 消息体
     */
    public synchronized void sendHeartMessage(AGHeartMessageModel messageModel,
                                               IAGRequestHandler<AGHeartMessageModel, AGHeartMessageModel> callback) {
        if (null == messageModel) {
            throw new IllegalArgumentException("Invalid AGHeartMessageModel to sendChatMessage");
        }
        if (null == mWebSocket) {
            if (null != callback) {
                callback.onError("2001", "Socket not connected");
            }
            return;
        }
        String message = JSON.toJSONString(messageModel);
        mWebSocket.send(message);
    }

    /**
     * 检测是否有需要重新发送的消息
     */
    private synchronized void checkIfNeedRestartMessageQueen() {
        synchronized (messageSessions) {
            if (messageSessions.isEmpty()) {
                return;
            }
            for (AGMessageSession messageSession : messageSessions.values()) {
                synchronized (messageQueue) {
                    try {
                        if (!messageQueue.contains(messageSession.getMessage())) {
                            messageQueue.put(messageSession.getMessage());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            socketHandler.obtainMessage(ACTION_SEND_MESSAGE).sendToTarget();
        }
    }

    /**
     * 添加重发的session
     *
     * @param messageModel
     */
    private synchronized void addSendSession(AGSendMessageModel messageModel) {
        synchronized (messageSessions) {
            AGMessageSession messageSession = messageSessions.get(messageModel.messageId);
            if (null == messageSession) {
                messageSession = new AGMessageSession(messageModel);
                messageSession.add(this);
                messageSession.startTask();
                messageSessions.put(messageModel.messageId, messageSession);
            } else {
                messageSession.restartTask();
            }
        }
    }

    /**
     * 停止重发session
     *
     * @param messageSession
     */
    private synchronized void stopSendSession(AGMessageSession messageSession) {
        messageSession.stopTask();
    }

    /**
     * 清除重发session
     */
    private synchronized void clearSendSession() {
        synchronized (messageSessions) {
            for (AGMessageSession messageSession : messageSessions.values()) {
                stopSendSession(messageSession);
            }
            messageSessions.clear();
        }
    }

    /**
     * 销毁
     */
    protected synchronized void destroy() {
        isDestroyed = true;
        synchronized (statusCallbacks) {
            statusCallbacks.clear();
        }
        synchronized (messageCallbacks) {
            messageCallbacks.clear();
        }
        synchronized (messageQueue) {
            messageQueue.clear();
        }
        synchronized (sendCallbacks) {
            sendCallbacks.clear();
        }
        synchronized (uiHandler) {
            uiHandler.removeCallbacksAndMessages(null);
            uiHandler.getLooper().quit();
        }
        synchronized (socketHandler) {
            socketHandler.removeCallbacksAndMessages(null);
            socketHandler.getLooper().quit();
        }
        clearSendSession();
        RXWebSocketProvider.getInstance().destroy();
    }

    @Override
    public void onFailure(AGSendMessageModel sendMessage) {
        uiHandler.obtainMessage(ACTION_NOTIFY_SEND_MESSAGE_FAILED, sendMessage).sendToTarget();
    }

    @Override
    public void onRetry(int retryCount, AGSendMessageModel sendMessage) {
        if (AGIMConfig.ENABLE_LOG) {
            Log.d(TAG, "onRetry, retryCount : " + retryCount + " messageId : " + sendMessage.messageId);
        }
    }

    private static class ConnectStatus {
        private IAGConnectionStatusCallback.AGConnectionStatus status;
        private int errorCode;
        private String reason;
    }

    /**
     * 切换到ui线程的handler
     */
    private final Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_DISPATCH_RECEIVE_MESSAGE:
                    notifyReceiveChatMessage((AGMessageModel) msg.obj);
                    break;
                case ACTION_DISPATCH_RESPONSE_MESSAGE:
                    notifyResponseMessage((AGSendResultModel) msg.obj);
                    break;
                case ACTION_DISPATCH_STATUS:
                    notifyConnectStatus((ConnectStatus) msg.obj);
                    break;
                case ACTION_NOTIFY_SEND_MESSAGE_FAILED:
                    dispatchSendMessageStatus((AGSendMessageModel) msg.obj);
                    break;
            }
        }
    };

    /**
     * 网络IO操作子线程
     */
    private class SocketHandler extends Handler {

        private SocketHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_SEND_MESSAGE:
                    synchronized (messageQueue) {
                        if (!isDestroyed && !messageQueue.isEmpty()) {
                            sendChatMessage(messageQueue.poll());
                            sendEmptyMessage(ACTION_SEND_MESSAGE);
                        }
                    }
                    break;
            }
        }
    }

    private class DefaultReconnectStrategy extends ReconnectStrategyAble {

        DefaultReconnectStrategy(long initTime) {
            super(initTime);
        }

        @Override
        public void doingStrategy(WebSocket socket) {
            if (null != socket) {
                if (mInitTime == mTime) {
                    mTime = mInitTime * 2;
                } else if (mTime == mInitTime * 2) {
                    mTime = mInitTime * 5;
                } else if (mTime == mInitTime * 5) {
                    mTime = mInitTime * 8;
                } else {
                    reset();
                }
            } else {
                reset();
            }
        }
    }
}
