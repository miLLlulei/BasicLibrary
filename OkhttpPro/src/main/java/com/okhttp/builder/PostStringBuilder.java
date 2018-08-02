package com.okhttp.builder;

import com.okhttp.request.PostStringRequest;
import com.okhttp.request.RequestCall;

import okhttp3.MediaType;

/**
 * Created by zhy on 15/12/14.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {
    private String content;
    private MediaType mediaType;

    @Override
    protected boolean appendParamsToUrl() {
        return false;
    }

    @Override
    public RequestCall createRequest() {
        return new PostStringRequest(url, tag, params, headers, content, mediaType,id).build();
    }

    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
