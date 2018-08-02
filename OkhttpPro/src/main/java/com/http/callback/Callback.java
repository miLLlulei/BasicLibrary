package com.http.callback;

import com.http.BaseRequest;
import com.http.BaseRequestCall;
import com.http.BaseResponse;


/**
 * 统一说明下 CallBack回调里面的id，暂时没用，可忽略；
 */
public abstract class Callback<T> {
    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(BaseRequest request) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter() {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress, long total) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    public boolean validateReponse(BaseResponse response) {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(BaseResponse response) throws Exception;

    /**
     * UI Thread
     *
     * @param
     */
    public abstract void onError(BaseRequestCall call, Exception e);

    /**
     * UI Thread
     *
     * @param
     */
    public abstract void onResponse(T response);


    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(BaseResponse response) throws Exception {
            return null;
        }

        @Override
        public void onError(BaseRequestCall call, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }
    };

}