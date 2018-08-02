package com.http;

import android.content.Context;

import com.http.callback.Callback;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by lulei-ms on 2018/3/2.
 */

public interface IHttpClient {

    void init(Context context, boolean isDebug);

    void addInterceptor(IInterceptor iInterceptor);

    void setInterceptorCallback(IInterceptorCallback callback);

    IInterceptorCallback getInterceptorCallback();
    /**
     * 如果需要自己单独处理请求的cancel；注意tag的唯一性；
     *
     * 统一说明下 CallBack回调里面的id，暂时没用，可忽略；
     */
    void getAsnyc(Object tag, String url, Map<String, String> params, Callback callback);

    byte[] getSnyc(Object tag, String url, Map<String, String> params) throws IOException;

    void postAsnyc(Object tag, String url, Map<String, String> params, Callback callback);

    void postBytes(Object tag, String url, byte[] bytes, Callback callback);

    void postFile(Object tag, String url, String key, File file, Callback callback);

    void cancelAll(Object tag);

    void saveCookie(List<String> hosts, Map<String, String> cookies);

    void removeCookie(List<String> hosts, List<String> keys);
}
