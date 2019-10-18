package com.simpletour.library.caocao.heart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.simpletour.library.caocao.AGWebSocketClient;
import com.simpletour.library.caocao.base.IAGCallback;
import com.simpletour.library.caocao.base.IAGOnReceiveHeartMessageCallback;
import com.simpletour.library.caocao.base.IAGRequestHandler;
import com.simpletour.library.caocao.config.AGIMConfig;
import com.simpletour.library.caocao.heart.base.IAGClientStateChangeCallback;
import com.simpletour.library.caocao.heart.enums.AGClientStateEnum;
import com.simpletour.library.caocao.heart.enums.AGHeartMachineStateEnum;
import com.simpletour.library.caocao.model.AGHeartMessageModel;
import com.simpletour.library.caocao.model.AGHeartResponseMessageModel;
import com.simpletour.library.caocao.utils.AGNetUtil;
import com.simpletour.library.caocao.utils.AGUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static com.simpletour.library.caocao.heart.enums.AGHeartMachineStateEnum.ACTIVE;
import static com.simpletour.library.caocao.heart.enums.AGHeartMachineStateEnum.AUTO_ADAPT;
import static com.simpletour.library.caocao.heart.enums.AGHeartMachineStateEnum.STABLE;
import static com.simpletour.library.caocao.heart.enums.AGHeartMachineStateEnum.SUB_ACTIVE;

/**
 * 包名：com.simpletour.library.caocao.heart
 * 描述：心跳状态机
 * 创建者：yankebin
 * 日期：2017/11/30
 */

public class AGHeartStateMachine implements IAGClientStateChangeCallback, IAGOnReceiveHeartMessageCallback {
    private final BlockingQueue<AGHeartMessageModel> messageQueen = new LinkedBlockingDeque<>();
    private Context context;
    private int currentHeart = AGIMConfig.STABLE_HEART;
    private AGWebSocketClient webSocket;
    private AGHeartMachineStateEnum heartMachineStateEnum = ACTIVE;
    private int subActiveCount;
    private int stableFailedCount;
    private int failedCount;
    private int minSuccessCount;
    private int netWorkType;
    private AGHeartMessageModel heartMessageModel;
    private HeartBeatHandler heartBeatHandler;
    private int successHeart;
    private boolean doDelayHeartBeat;


    public AGHeartStateMachine(Context context, AGWebSocketClient webSocket) {
        this.context = context;
        this.webSocket = webSocket;
        context.registerReceiver(netWorkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        HandlerThread handlerThread = new HandlerThread("SOCKET_HEART_BEAT_HANDLER");
        handlerThread.start();
        heartBeatHandler = new HeartBeatHandler(handlerThread.getLooper());
    }


    private synchronized void send() {
        if (null != webSocket) {
            heartMessageModel = build();
            webSocket.sendHeartMessage(heartMessageModel, heartRequestHandler);
        }
    }

    private void active() {
        successHeart = currentHeart = AGIMConfig.STABLE_HEART;
        heartBeatHandler.post(new Runnable() {
            @Override
            public void run() {
                if (heartMachineStateEnum != ACTIVE) {
                    return;
                }
                send();
                heartBeatHandler.postDelayed(this, successHeart);
            }
        });
    }

    private void checkSubActive() {
        subActiveCount++;
        if (subActiveCount >= 3) {
            subActiveCount = 0;
            heartMachineStateEnum = AUTO_ADAPT;
            autoAdapt();
        }
    }

    private void subActive() {
        successHeart = currentHeart = AGIMConfig.MIN_HEART;
        heartBeatHandler.post(new Runnable() {
            @Override
            public void run() {
                if (heartMachineStateEnum != SUB_ACTIVE) {
                    return;
                }
                send();
                heartBeatHandler.postDelayed(this, successHeart);
            }
        });
    }

    private void autoAdapt() {
        successHeart = currentHeart = AGIMConfig.MIN_HEART;
        heartBeatHandler.post(new Runnable() {
            @Override
            public void run() {
                send();
            }
        });
    }

    private void circleAdapt() {
        heartBeatHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                send();
            }
        }, currentHeart);
    }

    private void checkStable() {
        stableFailedCount++;
        if (stableFailedCount >= 5) {
            stableFailedCount = 0;
            heartMachineStateEnum = AUTO_ADAPT;
            autoAdapt();
        } else {
            tryNotifyReConnect();
        }
    }

    private void stable() {
        heartBeatHandler.post(new Runnable() {
            @Override
            public void run() {
                if (heartMachineStateEnum != STABLE) {
                    return;
                }
                send();
                heartBeatHandler.postDelayed(this, successHeart);
            }
        });
    }


    private synchronized AGHeartMessageModel build() {
        AGHeartMessageModel heartMessageModel = new AGHeartMessageModel();
        heartMessageModel.id = AGUtils.createId();
        return heartMessageModel;
    }

    private void tryNotifyReConnect() {
        if (null != webSocket) {

        }
    }

    private void onSocketConnected() {
        if (heartMachineStateEnum == ACTIVE) {
            active();
        } else if (heartMachineStateEnum == SUB_ACTIVE) {
            subActive();
        } else if (heartMachineStateEnum == AUTO_ADAPT) {
            doDelayHeartBeat = true;
            minSuccessCount = 0;
            autoAdapt();
        } else {
            stable();
        }
    }

    @Override
    public void onReceiveHeartMessage(AGHeartResponseMessageModel messageModel) {
        if (heartMessageModel.id.equals(messageModel.id)) {
            //取消超时倒计时
            heartRequestHandler.onSuccess(null);
            if (doDelayHeartBeat && (heartMachineStateEnum == STABLE || heartMachineStateEnum == AUTO_ADAPT)) {
                minSuccessCount++;
                if (minSuccessCount >= 3) {
                    minSuccessCount = 0;
                    doDelayHeartBeat = false;
                    if (heartMachineStateEnum == STABLE) {
                        stable();
                    } else if (heartMachineStateEnum == AUTO_ADAPT) {
                        autoAdapt();
                    }
                }
            } else {
                if (heartMachineStateEnum == AUTO_ADAPT) {
                    successHeart = currentHeart;
                    currentHeart += AGIMConfig.HEART_STEP;
                    if (currentHeart > AGIMConfig.MAX_HEART) {
                        currentHeart = AGIMConfig.MAX_HEART;
                    }
                } else if (heartMachineStateEnum == STABLE) {

                }
            }

            if (heartMachineStateEnum == SUB_ACTIVE) {
                checkSubActive();
                return;
            }


        }
    }

    @Override
    public void onClientStateChange(AGClientStateEnum clientStateEnum) {
        switch (clientStateEnum) {
            case FOREGROUND:
                heartMachineStateEnum = ACTIVE;
                active();
                break;
            case BACKGROUND:
                heartMachineStateEnum = SUB_ACTIVE;
                subActive();
                break;
            case ON_SCREEN_ON:
                send();
                break;
            case ON_NETWORK_AVAILABLE:
                tryNotifyReConnect();
                break;
            default:
                break;
        }
    }

    private class HeartBeatHandler extends Handler {

        private HeartBeatHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            send();
            switch (heartMachineStateEnum) {
                case ACTIVE:
                    active();
                    break;
                case STABLE:
                    stable();
                    break;
            }
        }
    }

    private final IAGRequestHandler<AGHeartMessageModel, AGHeartMessageModel> heartRequestHandler =
            new IAGRequestHandler<AGHeartMessageModel, AGHeartMessageModel>(new IAGCallback<AGHeartMessageModel>() {
                @Override
                public void onSuccess(AGHeartMessageModel agHeartMessageModel) {
                }

                @Override
                public void onError(String errorCode, String reason) {

                    if (heartMachineStateEnum == SUB_ACTIVE) {
                        checkSubActive();
                        return;
                    }

                    if (heartMachineStateEnum == STABLE) {
                        checkStable();
                        return;
                    }

                    if (heartMachineStateEnum == AUTO_ADAPT) {
                        failedCount++;
                        if (failedCount >= 5) {
                            failedCount = 0;
                            successHeart = currentHeart - 6;
                            heartMachineStateEnum = STABLE;
                        } else {
                            tryNotifyReConnect();
                        }
                    }
                }

                @Override
                public void onProgress(int progress, AGHeartMessageModel agHeartMessageModel) {

                }
            }, 5000L) {
                @Override
                public AGHeartMessageModel convert(AGHeartMessageModel agHeartMessageModel) {
                    return agHeartMessageModel;
                }
            };

    private BroadcastReceiver netWorkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            netWorkType = AGNetUtil.getNetWorkType(context);
            onClientStateChange(AGClientStateEnum.ON_NETWORK_AVAILABLE);
        }
    };
}
