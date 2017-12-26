package com.simpletour.lib.apicontrol;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;

/**
 * 包名：com.simpletour.supplier.retrofit2
 * 描述：服务器api请求类
 * 创建者：yankebin
 * 日期：2017/2/23
 */

public final class RetrofitApi {
    private static final String TAG = RetrofitApi.class.getName();
    private static final String LOG_INIT_CONFIG = "Initialize RetrofitApi with configuration";
    private static final String WARNING_RE_INIT_CONFIG = "Try to initialize RetrofitApi which had already been initialized before. ";
    private static final String ERROR_NOT_INIT = "RetrofitApi must be init with configuration before using";
    private static final String ERROR_INIT_CONFIG_WITH_NULL = "RetrofitApi configuration can not be initialized with null";

    private String token;
    private static RetrofitApi api;
    private OkHttpClient.Builder clientBuilder;
    private Retrofit.Builder builder;
    private final LinkedHashMap<String, String> defaultHeaders = new LinkedHashMap<>();
    private RetrofitConfig configuration;
    private HashMap<String, WeakReference<HashMap<String, Subscriber>>> rxCallCache = new HashMap<>();
    private HashMap<String, WeakReference<HashMap<String, SCallback>>> callCache = new HashMap<>();


    public synchronized void putRxCall(String key, Subscriber subscriber) {
        if (!rxCallCache.containsKey(key) || null == rxCallCache.get(key)) {
            HashMap<String, Subscriber> rxCallList = new HashMap<>();
            rxCallList.put(subscriber.toString(), subscriber);
            rxCallCache.put(key, new WeakReference<>(rxCallList));
        } else if (null != rxCallCache.get(key)) {
            WeakReference<HashMap<String, Subscriber>> listWeakReference = rxCallCache.get(key);
            HashMap<String, Subscriber> rxCallList = listWeakReference.get();
            if (null != rxCallList && !rxCallList.containsKey(subscriber.toString())) {
                rxCallList.put(subscriber.toString(), subscriber);
            }
        }
    }

    public synchronized void deleteRxCall(String key) {
        WeakReference<HashMap<String, Subscriber>> listWeakReference = rxCallCache.get(key);
        if (null == listWeakReference) {
            return;
        }
        HashMap<String, Subscriber> rxCallList = listWeakReference.get();
        if (null == rxCallList) {
            return;
        }
        for (Map.Entry<String, Subscriber> entry : rxCallList.entrySet()) {
            if (entry.getValue().isUnsubscribed()) {
                continue;
            }
            entry.getValue().unsubscribe();
        }
    }

    public String getBaseUrl() {
        checkConfiguration();
        return configuration.baseUrl;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public synchronized void putCall(String key, SCallback call) {
        if (!callCache.containsKey(key) || null == callCache.get(key)) {
            HashMap<String, SCallback> callList = new HashMap<>();
            callList.put(call.toString(), call);
            callCache.put(key, new WeakReference<>(callList));
        } else if (null != callCache.get(key)) {
            WeakReference<HashMap<String, SCallback>> listWeakReference = callCache.get(key);
            HashMap<String, SCallback> callList = listWeakReference.get();
            if (null != callList && !callList.containsKey(call.toString())) {
                callList.put(call.toString(), call);
            }
        }
    }

    public synchronized void deleteCall(String key) {
        WeakReference<HashMap<String, SCallback>> listWeakReference = callCache.get(key);
        if (null == listWeakReference) {
            return;
        }
        HashMap<String, SCallback> callList = listWeakReference.get();
        if (null == callList) {
            return;
        }
        for (Map.Entry<String, SCallback> entry : callList.entrySet()) {
            if (entry.getValue().isCanceled()) {
                continue;
            }
            entry.getValue().cancel();
        }
    }

    public synchronized void clear() {
        defaultHeaders.clear();
        for (Map.Entry<String, WeakReference<HashMap<String, Subscriber>>> entry : rxCallCache.entrySet()) {
            deleteRxCall(entry.getKey());
        }
        rxCallCache.clear();
        for (Map.Entry<String, WeakReference<HashMap<String, SCallback>>> entry : callCache.entrySet()) {
            deleteCall(entry.getKey());
        }
        callCache.clear();
    }

    public synchronized void destroy() {
        clear();
        token = null;
        configuration = null;
        builder = null;
        if (null != clientBuilder) {
            clientBuilder.interceptors().clear();
            clientBuilder = null;
        }
        api = null;
    }


    public static synchronized RetrofitApi getInstance() {
        if (null == api) {
            synchronized (RetrofitApi.class) {
                if (null == api) {
                    api = new RetrofitApi();
                }
            }
        }
        return api;
    }

    private RetrofitApi() {
        //每个请求的header里面加上设备类型信息
        defaultHeaders.put("Req-Device", "Android");//
    }

    /**
     * 初始化API服务
     *
     * @param config API配置
     * @throws IllegalArgumentException if passed <b>config</b> is null
     */
    public synchronized void init(RetrofitConfig config) {
        if (null == config) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if (null == configuration) {
            Log.d(TAG, LOG_INIT_CONFIG);
            init(config.baseUrl, config.connectTimeOut, config.readTimeOut,
                    config.timeUnit, config.logLevel, config.resolveFactory, config.converterFactory);
            configuration = config;
        } else {
            Log.w(TAG, WARNING_RE_INIT_CONFIG);
        }
    }

    /**
     * 初始化API服务
     *
     * @param baseUrl          服务端基础地址
     * @param connectTimeOut   连接超时时间
     * @param readTimeOut      读取超时时间
     * @param timeUnit         超时时间单位
     * @param logLevel         log级别
     * @param resolveFactory   请求数据的工厂模式
     * @param converterFactory 解析数据的工厂模式
     */
    private synchronized void init(@NonNull String baseUrl, long connectTimeOut, long readTimeOut,
                                   @NonNull TimeUnit timeUnit, HttpLoggingInterceptor.Level logLevel,
                                   @NonNull CallAdapter.Factory resolveFactory,
                                   @NonNull Converter.Factory converterFactory) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(null == logLevel ? HttpLoggingInterceptor.Level.BODY : logLevel);
        clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeOut, timeUnit)
                .readTimeout(readTimeOut, timeUnit)
                .addNetworkInterceptor(logging);


        builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(resolveFactory)
                .addConverterFactory(converterFactory);
    }

    /**
     * Checks if RetrofitApi's configuration was initialized
     *
     * @throws IllegalStateException if configuration wasn't initialized
     */
    private void checkConfiguration() {
        if (null == configuration) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }
    }

    /**
     * 设置默认的headers,如果传入null，将清除所有默认headers
     *
     * @param headers 请求头
     */
    public void setDefaultHeaders(Map<String, String> headers) {
        if (null == headers) {
            defaultHeaders.clear();
            return;
        }
        synchronized (defaultHeaders) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                defaultHeaders.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 更新服务端基础测试地址
     *
     * @param url 服务端url地址
     */
    public void updateBaseUrl(String url) {
        checkConfiguration();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        builder = builder.baseUrl(url);
        configuration.baseUrl = url;
    }

    /**
     * 创建请求类
     *
     * @param serviceClass 服务接口
     * @param extraHeaders 额外的请求头
     * @param <S>          服务接口代理类
     * @return 服务接口代理类
     * @throws IllegalStateException if {@link #init(RetrofitConfig)} method wasn't called before
     */
    private synchronized <S> S create(final Class<S> serviceClass, final boolean needDefaultHeaders,
                                      final HashMap<String, String> extraHeaders) {
        checkConfiguration();
        clientBuilder.interceptors().clear();
        clientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                if (needDefaultHeaders && null != defaultHeaders && !defaultHeaders.isEmpty()) {
                    RetrofitApi.this.addHeaders(requestBuilder, defaultHeaders);
                }
                if (null != extraHeaders && !extraHeaders.isEmpty()) {
                    RetrofitApi.this.addHeaders(requestBuilder, extraHeaders);
                }
                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        });

        Retrofit retrofit = builder.client(clientBuilder.build()).build();

        return retrofit.create(serviceClass);
    }

    /**
     * 添加headers
     *
     * @param requestBuilder 请求拦截器builder
     * @param headers        请求头集合
     */
    private void addHeaders(final Request.Builder requestBuilder, final HashMap<String, String>
            headers) {
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            String value = headers.get(key);
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            requestBuilder.addHeader(key, value);
        }
    }

    /**
     * 不要任何请求头的服务类
     *
     * @param serviceClass 服务接口
     * @param <S>          服务接口代理类
     * @return 服务接口代理类
     */
    public <S> S createWithNothing(final Class<S> serviceClass) {
        return create(serviceClass, false, null);
    }

    /**
     * 只需要默认请求头的服务类
     *
     * @param serviceClass 服务接口
     * @param <S>          服务接口代理类
     * @return 服务接口代理类
     */
    public <S> S createWithDefaultHeaders(final Class<S> serviceClass) {
        return create(serviceClass, true, null);
    }

    /**
     * 只需要额外请求头的服务类
     *
     * @param serviceClass 服务接口
     * @param extraHeaders 额外的请求头
     * @param <S>          服务接口代理类
     * @return 服务接口代理类
     */
    public <S> S createWidthExtraHeaders(final Class<S> serviceClass, final HashMap<String,
            String> extraHeaders) {
        return create(serviceClass, false, extraHeaders);
    }

    /**
     * 默认请求头和额外请求头都需要的服务类
     *
     * @param serviceClass 服务接口
     * @param extraHeaders 额外的请求头
     * @param <S>          服务接口代理类
     * @return 服务接口代理类
     */
    public <S> S createMixDefaultHeadersAndExtraHeaders(final Class<S> serviceClass,
                                                        final HashMap<String, String> extraHeaders) {
        return create(serviceClass, true, extraHeaders);
    }

    /**
     * 创建不需要TOKEN的请求类
     *
     * @param serviceClass
     * @param <S>
     * @return
     */
    public <S> S create(Class<S> serviceClass) {
        return create(serviceClass, null);
    }

    /**
     * 创建自带TOKEN的请求类
     *
     * @param serviceClass
     * @param <S>
     * @return
     */
    public <S> S createWithToken(Class<S> serviceClass) {
        return create(serviceClass, token);
    }

    /**
     * 创建服务api类
     *
     * @param serviceClass
     * @param value
     * @param <S>
     * @return
     */
    public <S> S create(Class<S> serviceClass, String value) {
        HashMap<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("AccessToken", value);

        return createMixDefaultHeadersAndExtraHeaders(serviceClass, extraHeaders);
    }

    public static class RetrofitConfig {
        private String baseUrl;
        private long connectTimeOut;
        private long readTimeOut;
        private TimeUnit timeUnit;
        private HttpLoggingInterceptor.Level logLevel;
        private CallAdapter.Factory resolveFactory;
        private Converter.Factory converterFactory;

        private RetrofitConfig() {

        }

        private RetrofitConfig(RequestBuilder builder) {
            this();
            baseUrl = builder.baseUrl;
            connectTimeOut = builder.connectTimeOut;
            readTimeOut = builder.readTimeOut;
            timeUnit = builder.timeUnit;
            logLevel = builder.logLevel;
            resolveFactory = builder.resolveFactory;
            converterFactory = builder.converterFactory;
        }

        public static class RequestBuilder {
            private String baseUrl;
            private long connectTimeOut = 40L;
            private long readTimeOut = 60L;
            private TimeUnit timeUnit = TimeUnit.SECONDS;
            private HttpLoggingInterceptor.Level logLevel = BuildConfig.DEBUG ?
                    HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
            private CallAdapter.Factory resolveFactory = RxJavaCallAdapterFactory.create();
            private Converter.Factory converterFactory = GsonConverterFactory.create();

            public RequestBuilder baseUrl(@NonNull String baseUrl) {
                this.baseUrl = baseUrl;
                return this;
            }

            public RequestBuilder connectTimeOut(long connectTimeOut) {
                if (connectTimeOut > 0) {
                    this.connectTimeOut = connectTimeOut;
                }
                return this;
            }

            public RequestBuilder readTimeOut(long readTimeOut) {
                if (readTimeOut > 0) {
                    this.readTimeOut = readTimeOut;
                }
                return this;
            }

            public RequestBuilder timeUnit(TimeUnit timeUnit) {
                if (null != timeUnit) {
                    this.timeUnit = timeUnit;
                }
                return this;
            }

            public RequestBuilder logLevel(HttpLoggingInterceptor.Level logLevel) {
                if (null != logLevel) {
                    this.logLevel = logLevel;
                }
                return this;

            }

            public RequestBuilder resolveFactory(CallAdapter.Factory resolveFactory) {
                if (null != resolveFactory) {
                    this.resolveFactory = resolveFactory;
                }
                return this;
            }

            public RequestBuilder converterFactory(Converter.Factory converterFactory) {
                if (null != converterFactory) {
                    this.converterFactory = converterFactory;
                }
                return this;
            }

            public RetrofitConfig build() {
                return new RetrofitConfig(this);
            }

            /**
             * API默认配置builder<BR\>
             * <p>
             * {@link #connectTimeOut} 40s<BR\>
             * {@link #readTimeOut} 60s<BR\>
             * {@link #timeUnit} s<BR\>
             * {@link #logLevel} {@link HttpLoggingInterceptor.Level#BODY}<BR\>
             * {@link #resolveFactory} {@link RxJavaCallAdapterFactory}<BR\>
             * {@link #converterFactory} {@link GsonConverterFactory}<BR\>
             *
             * @param baseUrl 服务端基础地址
             * @return API默认配置builder
             */
            public RequestBuilder defaultConfig(@NonNull String baseUrl) {
                baseUrl(baseUrl);
                return this;
            }
        }
    }
}

