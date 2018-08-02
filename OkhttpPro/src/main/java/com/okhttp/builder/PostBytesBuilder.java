package com.okhttp.builder;

import com.okhttp.request.PostBytesRequest;
import com.okhttp.request.RequestCall;

import okhttp3.MediaType;

/**
 * Created by lulei-ms on 15/12/14.
 */
public class PostBytesBuilder extends OkHttpRequestBuilder<PostBytesBuilder>{
    private byte[] content;
    private MediaType mediaType;

    @Override
    protected boolean appendParamsToUrl() {
        return false;
    }

    public PostBytesBuilder content(byte[] content) {
        this.content = content;
        return this;
    }

    public PostBytesBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall createRequest() {
        return new PostBytesRequest(url, tag, params, headers, content, mediaType,id).build();
    }
}
