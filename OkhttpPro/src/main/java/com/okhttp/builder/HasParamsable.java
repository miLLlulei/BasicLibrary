package com.okhttp.builder;

import java.util.Map;

/**
 * Created by zhy on 16/3/1.
 */
public interface HasParamsable<T extends OkHttpRequestBuilder> {
    T params(Map<String, String> params);
    T addParams(String key, String val);
}
