package com.http.callback;

import com.http.BaseResponse;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(BaseResponse response) throws IOException {
        return response.string();
    }
}
