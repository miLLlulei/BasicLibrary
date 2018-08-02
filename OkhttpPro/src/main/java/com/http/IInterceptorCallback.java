package com.http;

import android.os.Message;

/**
 * Created by Administrator on 2018/4/10.
 */

public interface IInterceptorCallback {
    int RESULT_QT_EXPIRE = 1;
    void onInterceptorResult(Message message);
}
