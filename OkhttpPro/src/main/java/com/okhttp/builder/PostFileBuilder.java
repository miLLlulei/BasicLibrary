package com.okhttp.builder;

import com.okhttp.request.PostFileRequest;
import com.okhttp.request.RequestCall;

import java.io.File;

import okhttp3.MediaType;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder>
{
    private File file;
    private MediaType mediaType;

    @Override
    protected boolean appendParamsToUrl() {
        return false;
    }

    public OkHttpRequestBuilder file(File file) {
        this.file = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType){
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall createRequest() {
        return new PostFileRequest(url, tag, params, headers, file, mediaType,id).build();
    }

}
