package com.okhttp.builder;


import com.okhttp.request.GetRequest;
import com.okhttp.request.RequestCall;


/**
 * Created by zhy on 15/12/14.
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> {

    @Override
    protected boolean appendParamsToUrl() {
        return true;
    }

    @Override
    public RequestCall createRequest() {
        return new GetRequest(url, tag, params, headers,id).build();
    }
}
