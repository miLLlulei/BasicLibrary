package com.http.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.http.BaseResponse;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class BitmapCallback extends Callback<Bitmap> {
    @Override
    public Bitmap parseNetworkResponse(BaseResponse response) throws Exception {
        return BitmapFactory.decodeStream(response.byteStream());
    }

}
