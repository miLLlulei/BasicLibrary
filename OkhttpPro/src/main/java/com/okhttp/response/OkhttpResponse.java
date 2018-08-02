package com.okhttp.response;

import com.http.BaseResponse;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public class OkhttpResponse extends BaseResponse {
    private Response response;

    public OkhttpResponse(Response response) {
        this.response = response;
    }

    @Override
    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    @Override
    public InputStream byteStream() {
        return response.body().byteStream();
    }

    @Override
    public String string() throws IOException {
        return response.body().string();
    }

    @Override
    public long contentLength() {
        return response.body().contentLength();
    }

    @Override
    public void close() {
        response.body().close();
    }
}
