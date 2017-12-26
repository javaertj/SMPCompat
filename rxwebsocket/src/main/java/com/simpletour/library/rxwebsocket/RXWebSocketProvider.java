package com.simpletour.library.rxwebsocket;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * 包名：com.simpletour.library.rxwebsocket
 * 描述：<p>
 * WebSocketProvider based on okhttp and RxJava
 * </p>
 * Core Feature : WebSocket will be auto reconnection onFailed.
 * 创建者：yankebin
 * 日期：2017/11/29
 */
public final class RXWebSocketProvider implements OnReConnectTimeReadyCallback {
    private static volatile RXWebSocketProvider instance;
    private final Map<String, Observable<WebSocketInfo>> observableMap;
    private final Map<String, WebSocket> webSocketMap;
    private final ReConnectHandler reConnectHandler;
    private final Map<String, ReconnectStrategyAble> reconnectStrategyAbleMap;

    private OkHttpClient client;
    private boolean showLog;

    private RXWebSocketProvider() {
        try {
            Class.forName("okhttp3.OkHttpClient");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Must be dependency okhttp3 !");
        }
        try {
            Class.forName("rx.Observable");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Must be dependency rxjava 1.x");
        }
        try {
            Class.forName("rx.android.schedulers.AndroidSchedulers");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Must be dependency rxandroid 1.x");
        }
        reconnectStrategyAbleMap = new LinkedHashMap<>();
        observableMap = new LinkedHashMap<>();
        webSocketMap = new LinkedHashMap<>();
        client = new OkHttpClient();

        HandlerThread handlerThread = new HandlerThread("RECONNECT_HANDLER");
        handlerThread.start();
        reConnectHandler = new ReConnectHandler(handlerThread.getLooper());
    }

    public static RXWebSocketProvider getInstance() {
        if (null == instance) {
            synchronized (RXWebSocketProvider.class) {
                if (null == instance) {
                    instance = new RXWebSocketProvider();
                }
            }
        }
        return instance;
    }

    /**
     * 添加自定义重连时间计算策略
     *
     * @param reconnectStrategyAble
     */
    public void addReconnectStrategyAble(String url, ReconnectStrategyAble reconnectStrategyAble) {
        if (null != reconnectStrategyAble) {
            reconnectStrategyAble.setConnectTimeReadyCallback(this);
        }
        if (null == reconnectStrategyAbleMap.get(url)) {
            reconnectStrategyAbleMap.put(url, reconnectStrategyAble);
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        synchronized (RXWebSocketProvider.class) {
            reconnectStrategyAbleMap.clear();
            observableMap.clear();
            webSocketMap.clear();
            client = null;
            if (null != reConnectHandler.webSocket) {
                reConnectHandler.webSocket.cancel();
                reConnectHandler.webSocket.close(1000, "destroy");
                reConnectHandler.webSocket = null;
            }
            reConnectHandler.removeCallbacksAndMessages(null);
            reConnectHandler.getLooper().quit();
            instance = null;
        }
    }

    /**
     * set your client
     *
     * @param client
     */
    public void setClient(@NonNull OkHttpClient client) {
        this.client = client;
    }

    /**
     * wss support
     *
     * @param sslSocketFactory
     * @param trustManager
     */
    public void setSSLSocketFactory(@NonNull SSLSocketFactory sslSocketFactory, @NonNull X509TrustManager trustManager) {
        client = client.newBuilder().sslSocketFactory(sslSocketFactory, trustManager).build();
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    /**
     * @param url      ws://127.0.0.1:8080/websocket
     * @param timeout  The WebSocket will be reconnected after the specified time interval is not "onMessage",
     *                 <p>
     *                 在指定时间间隔后没有收到消息就会重连WebSocket,为了适配小米平板,因为小米平板断网后,不会发送错误通知
     * @param timeUnit unit
     * @return
     */
    public Observable<WebSocketInfo> getWebSocketInfo(final String url, final long timeout, final TimeUnit timeUnit) {
        Observable<WebSocketInfo> observable = observableMap.get(url);
        if (observable == null) {
            observable = Observable.create(new WebSocketOnSubscribe(url))
                    //自动重连
                    .timeout(timeout, timeUnit)
                    .retry()
                    //共享
                    .doOnUnsubscribe(new Action0() {
                        @Override
                        public void call() {
                            observableMap.remove(url);
                            webSocketMap.remove(url);
                            if (showLog) {
                                Log.d("RXWebSocketProvider", "unSubscribe");
                            }
                        }
                    })
                    .doOnNext(new Action1<WebSocketInfo>() {
                        @Override
                        public void call(WebSocketInfo webSocketInfo) {
                            if (webSocketInfo.isOnOpen()) {
                                webSocketMap.put(url, webSocketInfo.getWebSocket());
                            }
                        }
                    })
                    .share()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observableMap.put(url, observable);
        } else {
            WebSocket webSocket = webSocketMap.get(url);
            if (null != webSocket) {
                observable = observable.startWith(new WebSocketInfo(webSocket, true));
            }
        }
        return observable;
    }

    /**
     * default timeout: 30 days
     * <p>
     * 若忽略小米平板,请调用这个方法
     * </p>
     */
    public Observable<WebSocketInfo> getWebSocketInfo(String url) {
        return getWebSocketInfo(url, 30, TimeUnit.DAYS);
    }

    public Observable<String> getWebSocketString(String url) {
        return getWebSocketInfo(url)
                .map(new Func1<WebSocketInfo, String>() {
                    @Override
                    public String call(WebSocketInfo webSocketInfo) {
                        return webSocketInfo.getString();
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return null != s;
                    }
                });
    }

    public Observable<ByteString> getWebSocketByteString(String url) {
        return getWebSocketInfo(url)
                .map(new Func1<WebSocketInfo, ByteString>() {
                    @Override
                    public ByteString call(WebSocketInfo webSocketInfo) {
                        return webSocketInfo.getByteString();
                    }
                })
                .filter(new Func1<ByteString, Boolean>() {
                    @Override
                    public Boolean call(ByteString byteString) {
                        return null != byteString;
                    }
                });
    }

    public Observable<WebSocket> getWebSocket(String url) {
        return getWebSocketInfo(url)
                .map(new Func1<WebSocketInfo, WebSocket>() {
                    @Override
                    public WebSocket call(WebSocketInfo webSocketInfo) {
                        return webSocketInfo.getWebSocket();
                    }
                });
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param msg
     */
    public void send(@NonNull String url, String msg) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(msg);
        } else {
            throw new IllegalStateException("The WebSocket not open yet");
        }
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param byteString
     */
    public void send(@NonNull String url, ByteString byteString) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(byteString);
        } else {
            throw new IllegalStateException("The WebSocket not open yet");
        }
    }

    /**
     * 不用关心url 的WebSocket是否打开,可以直接发送
     *
     * @param url
     * @param msg
     */
    public void asyncSend(@NonNull String url, final String msg) {
        getWebSocket(url)
                .first()
                .subscribe(new Action1<WebSocket>() {
                    @Override
                    public void call(WebSocket webSocket) {
                        webSocket.send(msg);
                    }
                });

    }

    /**
     * 不用关心url 的WebSocket是否打开,可以直接发送
     *
     * @param url
     * @param byteString
     */
    public void asyncSend(@NonNull String url, final ByteString byteString) {
        getWebSocket(url)
                .first()
                .subscribe(new Action1<WebSocket>() {
                    @Override
                    public void call(WebSocket webSocket) {
                        webSocket.send(byteString);
                    }
                });
    }

    private Request getRequest(String url) {
        return new Request.Builder().get().url(url).build();
    }

    @Override
    public void onReConnectTimeReady(long time, String url, Subscriber<? super WebSocketInfo> subscriber) {
        Message message = reConnectHandler.obtainMessage();
        message.obj = new ReConnectInfo(subscriber, url);
        reConnectHandler.sendMessageDelayed(message, time);
    }

    private final class ReConnectInfo {
        private Subscriber<? super WebSocketInfo> subscriber;
        private String url;

        private ReConnectInfo(Subscriber<? super WebSocketInfo> subscriber, String url) {
            this.subscriber = subscriber;
            this.url = url;
        }
    }

    private final class WebSocketOnSubscribe implements Observable.OnSubscribe<WebSocketInfo> {
        private String url;

        private WebSocketOnSubscribe(String url) {
            this.url = url;
        }

        @Override
        public void call(final Subscriber<? super WebSocketInfo> subscriber) {
            if (null != reconnectStrategyAbleMap.get(url)) {
                reconnectStrategyAbleMap.get(url).onReconnect(reConnectHandler.webSocket, url, subscriber);
            } else {
                Message message = reConnectHandler.obtainMessage();
                message.obj = new ReConnectInfo(subscriber, url);
                if (null != reConnectHandler.webSocket) {
                    reConnectHandler.sendMessageDelayed(message, 2000L);
                } else {
                    reConnectHandler.sendMessageDelayed(message, 0L);
                }
            }
        }
    }

    private final class ReConnectHandler extends Handler {

        private WebSocket webSocket;

        private ReConnectHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            ReConnectInfo reConnectInfo = (ReConnectInfo) msg.obj;
            initWebSocket(reConnectInfo.url, reConnectInfo.subscriber);
        }

        private void initWebSocket(final String url, final Subscriber<? super WebSocketInfo> subscriber) {
            webSocket = client.newWebSocket(getRequest(url), new WebSocketListener() {
                @Override
                public void onOpen(final WebSocket webSocket, Response response) {
                    if (showLog) {
                        Log.d("RXWebSocketProvider", url + " --> onOpen");
                    }
                    webSocketMap.put(url, webSocket);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(new WebSocketInfo(webSocket, true));
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(new WebSocketInfo(webSocket, text));
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(new WebSocketInfo(webSocket, bytes));
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    if (showLog) {
                        Log.e("RXWebSocketProvider", t.toString() + webSocket.request().url().uri().getPath());
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(t);
                    }
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    webSocket.close(1000, null);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    if (showLog) {
                        Log.d("RXWebSocketProvider", url + " --> onClosed:code= " + code + " reason " + reason);
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(new WebSocketInfo(reason, webSocket));
                    }
                }
            });
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    webSocket.close(3000, "closed by user");
                }
            });
        }
    }
}
