package com.http.callback;

import com.http.BaseResponse;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by lulei-ms
 */
public abstract class JsonCallback extends Callback<JSONObject> {
    @Override
    public JSONObject parseNetworkResponse(BaseResponse response) throws Exception {
        return new JSONObject(response.string());
    }
}
