
package com.simpletour.library.caocao;

import android.util.Log;

import com.simpletour.library.caocao.base.IAGMessageContent;
import com.simpletour.library.caocao.base.IAGMessageSendStatusCallback;
import com.simpletour.library.caocao.model.AGSendMessageModel;
import com.simpletour.library.caocao.utils.AGNetUtil;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 包名：com.simpletour.library.caocao
 * 描述：发送一条消息的定时器，处理重发消息
 * 创建者：yankebin
 * 日期：2017/5/3
 */
final class AGMessageSession {
    protected static final int MAX_RETRY_COUNT = 3;
    /**
     * 任务的基本重试时间(s)
     **/
    private static final int SEND_MSG_RETRY_TIME = 10;
    /**
     * 根据语音和文本的长度不同和当前网络类型来设置超时时间
     **/
    private int reTryTime;
    /**
     * 记录消息重发次数
     **/
    private int retryCount;
    /**
     * 消息定时器的观察者
     **/
    private final LinkedList<IAGMessageSendStatusCallback> observers = new LinkedList<>();
    /**
     * 发送一条消息的计时器，规定时间内没等到服务器回应，提示用户发送失败
     **/
    private Timer sendTimer;
    /**
     * 消息发送任务的开关,作用类似录音任务开关
     **/
    private boolean isSendRun;
    /**
     * 消息发送耗时时间
     **/
    private int sendTime;
    /**
     * 当前任务的消息主体
     **/
    private AGSendMessageModel message;

    /**
     * 获取当前任务的消息主体
     *
     * @return
     */
    public AGSendMessageModel getMessage() {
        return message;
    }

    /**
     * 设置当前任务的消息主体
     *
     * @param message
     */
    public void setMessage(AGSendMessageModel message) {
        this.message = message;
    }

    /**
     * 获取当前任务消息已发送失败的次数
     *
     * @return
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * 任务构造器
     *
     * @param message
     */
    public AGMessageSession(AGSendMessageModel message) {
        this.message = message;
    }

    /**
     * 开始任务
     */
    public void startTask() {
        initRetryTime();
        isSendRun = true;
        sendTimer = new Timer();
        sendTimer.scheduleAtFixedRate(sendTimerTask, 0, 1000);
    }

    /**
     * 重置重试时间
     */
    private void resetRetryTime() {
        reTryTime = 0;
    }

    /**
     * 初始化消息的重试时间
     */
    private void initRetryTime() {
        resetRetryTime();
        final int networkType = AGNetUtil.getNetWorkType(AGIMEngine.getInstance().getContext());
        // 根据消息类型确定其重试时间（默认基于wifi）
        if (message.content.contentType == IAGMessageContent.MessageContentType.AUDIO
                || message.content.contentType == IAGMessageContent.MessageContentType.IMAGE ||
                message.content.contentType == IAGMessageContent.MessageContentType.FILE)
            reTryTime = SEND_MSG_RETRY_TIME << 2;// audio 20s
        else if (message.content.contentType == IAGMessageContent.MessageContentType.TEXT
                || message.content.contentType == IAGMessageContent.MessageContentType.LINKED)
            reTryTime = SEND_MSG_RETRY_TIME << 1;// text 10s
        // 根据网络类型来决定增加或减少重试时间
        // 2G
        if (networkType == AGNetUtil.NETWORKTYPE_2G) {
            reTryTime = reTryTime << 1;// audio 40s ,text 20s
            // 3G
        } else if (networkType == AGNetUtil.NETWORKTYPE_3G) {
            reTryTime += SEND_MSG_RETRY_TIME;// audio 30s,text 15s
        }

        Log.i(getClass().getName(), "retrytime : " + reTryTime);
    }

    /**
     * 发送一条消息的计时器任务
     **/
    private TimerTask sendTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (!isSendRun) {
                return;
            }
            sendTime++;
            if (sendTime >= reTryTime) {
                sendTime = 0;
                retryCount++;
                if (retryCount >= MAX_RETRY_COUNT) {
                    notifySendFailed();
                } else {
                    notifyRetry();
                }
            }
        }
    };

    /**
     * 获取当前已发送时间
     *
     * @return
     */
    public int getSendTime() {
        return sendTime;
    }

    /**
     * 获取定时器的开关
     *
     * @return
     */
    public boolean isSendRun() {
        return isSendRun;
    }

    public void add(IAGMessageSendStatusCallback observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }


    public void remove(IAGMessageSendStatusCallback observer) {
        if (observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    private void notifyRetry() {
        if (observers.isEmpty()) {
            return;
        }
        for (IAGMessageSendStatusCallback observer : observers) {
            observer.onRetry(retryCount, message);
        }
    }


    public void notifySendFailed() {
        pauseTask();
        if (observers.isEmpty()) {
            return;
        }
        for (IAGMessageSendStatusCallback observer : observers) {
            observer.onFailure(message);
        }
    }

    /**
     * 停止任务
     */
    public void stopTask() {
        recycle();
    }

    /**
     * 定时器和任务的回收
     **/
    private void recycle() {
        if (!observers.isEmpty()) {
            observers.clear();
        }
        pauseTask();
        retryCount = 0;
        if (sendTimerTask != null) {
            sendTimerTask.cancel();
            sendTimerTask = null;
        }
        if (sendTimer != null) {
            sendTimer.purge();
            sendTimer.cancel();
            sendTimer = null;
        }
    }

    /**
     * 重新开始任务（暂停后可用）
     */
    public void restartTask() {
        initRetryTime();
        retryCount = 0;
        if (sendTime != 0) {
            sendTime = 0;
        }
        isSendRun = true;
    }

    /**
     * 暂停任务(即不销毁定时器，只是暂停)
     */
    public void pauseTask() {
        isSendRun = false;
        sendTime = 0;
    }
}
