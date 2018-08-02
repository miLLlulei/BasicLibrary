package com.http;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import com.mill.utils.ContextUtils;
import com.mill.utils.LogUtils;
import com.http.callback.Callback;
import com.okhttp.OkHttpUtils;
import com.okhttp.cookie.CookieJarImpl;
import com.okhttp.cookie.store.PersistentCookieStore;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by lulei-ms on 2018/3/5.
 */

public class HttpClientImp implements IHttpClient {

    private static final String USER_AGENT_POSTFIX = ";mill";
    private static final String UA = "User-Agent";
    private static IHttpClient mInstance;
    private String mUserAgent;
    private AtomicBoolean isInit = new AtomicBoolean(false);
    private final Set<IInterceptor<Interceptor>> interceptors = new HashSet<>();
    private IInterceptorCallback interceptorCallback;

    private HttpClientImp() {

    }

    public static IHttpClient getInstance() {
        if (mInstance == null) {
            synchronized (HttpClientImp.class) {
                if (mInstance == null) {
                    mInstance = new HttpClientImp();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context, boolean isDebug) {
        if (!isInit.get()) {
            CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context.getApplicationContext()));
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS);
            addIntercptors(builder);
            OkHttpClient okHttpClient =builder.build();
            OkHttpUtils.initClient(okHttpClient);
            isInit.set(true);
        }
    }

    @Override
    public void addInterceptor(IInterceptor iInterceptor) {
        interceptors.add(iInterceptor);
    }

    @Override
    public void setInterceptorCallback(IInterceptorCallback callback) {
        this.interceptorCallback = callback;
    }

    @Override
    public IInterceptorCallback getInterceptorCallback() {
        return this.interceptorCallback;
    }

    @Override
    public void getAsnyc(Object tag, String url, Map<String, String> params, Callback callback) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        safeMap(params);
        OkHttpUtils.get().url(url).params(params).addHeader(UA, getUserAgent()).tag(tag).build().execute(callback);
    }

    @Override
    public byte[] getSnyc(Object tag, String url, Map<String, String> params) throws IOException {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        safeMap(params);
        Response response = OkHttpUtils.get().url(url).params(params).addHeader(UA, getUserAgent()).tag(tag).build().execute();
        return response.body().bytes();
    }

    @Override
    public void postAsnyc(Object tag, String url, Map<String, String> params, Callback callback) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        safeMap(params);
        OkHttpUtils.post().url(url).params(params).addHeader(UA, getUserAgent()).tag(tag).build().execute(callback);
    }

    public void postFile(Object tag, String url, String key, File file, Callback callback) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        OkHttpUtils.post().url(url).addFile(key, file.getName(), file)
                .addHeader(UA, getUserAgent()).tag(tag).build().execute(callback);
    }

    public void postBytes(Object tag, String url, byte[] bytes, Callback callback) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        OkHttpUtils.postBytes().content(bytes).url(url).addHeader(UA, getUserAgent()).tag(tag).build().execute(callback);
    }

    public void cancelAll(Object tag) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        OkHttpUtils.getInstance().cancelTag(tag);
    }


    public void saveCookie(List<String> hosts, Map<String, String> cookies) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (hosts != null && !hosts.isEmpty()
                && cookies != null && !cookies.isEmpty()) {
            CookieJarImpl cookieJar = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
            for (String host : hosts) {
                HttpUrl httpUrl = HttpUrl.parse(host);
                List<Cookie> cookieList = new ArrayList<>();
                for (Map.Entry<String, String> entry : cookies.entrySet()) {
                    Cookie.Builder ckBuilder = new Cookie.Builder().expiresAt(253402300799999L).domain(httpUrl.host()).name(entry.getKey()).value(entry.getValue());
                    cookieList.add(ckBuilder.build());
                }
                cookieJar.saveFromResponse(httpUrl, cookieList);
            }
        }
    }

    public void removeCookie(List<String> hosts, List<String> keys) {
        init(ContextUtils.getApplicationContext(), LogUtils.isDebug());
        if (hosts != null && !hosts.isEmpty()) {
            CookieJarImpl cookieJar = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
            for (String host : hosts) {
                HttpUrl httpUrl = HttpUrl.parse(host);
                if (keys != null && !keys.isEmpty()) {
                    for (String key : keys) {
                        Cookie.Builder ckBuilder = new Cookie.Builder().domain(httpUrl.host()).name(key).value("");
                        cookieJar.getCookieStore().remove(httpUrl, ckBuilder.build());
                    }
                } else {
                    List<Cookie> cookies = cookieJar.loadForRequest(httpUrl);
                    if (cookies != null && !cookies.isEmpty()) {
                        for (Cookie cookie : cookies) {
                            cookieJar.getCookieStore().remove(httpUrl, cookie);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    public String getUserAgent() {
        if (TextUtils.isEmpty(mUserAgent)) {
            String userAgent = getDefaultUserAgent(ContextUtils.getApplicationContext());
            if (!TextUtils.isEmpty(userAgent)) {
                userAgent += USER_AGENT_POSTFIX;
            }
            mUserAgent = userAgent;
        }
        return mUserAgent;
    }

    /**
     * 获取默认UserAgent（读取系统WebView的UserAgent设置）
     */
    public String getDefaultUserAgent(Context context) {
        String result;
        // http://androidxref.com/4.1.1/xref/frameworks/base/core/java/android/webkit/WebSettingsClassic.java#getCurrentUserAgent
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            StringBuffer buffer = new StringBuffer();
            // Add version
            final String version = Build.VERSION.RELEASE;
            if (version.length() > 0) {
                buffer.append(version);
            } else {
                // default to "1.0"
                buffer.append("1.0");
            }
            buffer.append("; ");
            final String language = locale.getLanguage();
            if (language != null) {
                buffer.append(language.toLowerCase());
                final String country = locale.getCountry();
                if (country != null) {
                    buffer.append("-");
                    buffer.append(country.toLowerCase());
                }
            } else {
                // default to "en"
                buffer.append("en");
            }
            // add the model for the release build
            if ("REL".equals(Build.VERSION.CODENAME)) {
                final String model = Build.MODEL;
                if (model.length() > 0) {
                    buffer.append("; ");
                    // ok3, header不支持中文， encode一下
                    buffer.append(URLEncoder.encode(model, "utf-8"));
                }
            }
            final String id = Build.ID;
            if (id.length() > 0) {
                buffer.append(" Build/");
                buffer.append(id);
            }
            final String base = context.getResources().getText(
                    Resources.getSystem().getIdentifier("web_user_agent", android.R.string.class.getSimpleName(), "android")).toString();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                result = String.format(base, buffer);
            } else {
                String mobile = context.getResources().getText(
                        Resources.getSystem().getIdentifier("web_user_agent_target_content", android.R.string.class.getSimpleName(), "android")).toString();
                result = String.format(base, buffer, mobile);
            }
        } catch (Exception | Error e) {
            if (LogUtils.isDebug()) {
                LogUtils.d("HttpClientImp", "getDefaultUserAgent");
            }
            result = System.getProperty("http.agent");
        }
        if (LogUtils.isDebug()) {
            LogUtils.d("HttpClientImp", "getDefaultUserAgent.result = " + result);
        }
        return result;
    }

    private void safeMap(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    entry.setValue("");
                }
            }
        }
    }

    private void addIntercptors(OkHttpClient.Builder builder){
        List<IInterceptor<Interceptor>> list = new ArrayList<>(interceptors);
        for(IInterceptor<Interceptor> iInterceptor : list){
            Interceptor interceptor;
            if(iInterceptor != null && (interceptor = iInterceptor.getInterceptor()) != null){
                builder.addInterceptor(interceptor);
            }
        }
    }
}
