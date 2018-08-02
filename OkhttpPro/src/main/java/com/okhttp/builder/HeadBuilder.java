package com.okhttp.builder;

import com.okhttp.OkHttpUtils;
import com.okhttp.request.OtherRequest;
import com.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder {
    @Override
    public RequestCall createRequest() {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url,
                tag, params, headers,id).build();
    }

}
