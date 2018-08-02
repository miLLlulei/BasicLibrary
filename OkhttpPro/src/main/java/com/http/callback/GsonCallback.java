package com.http.callback;

import com.http.BaseResponse;
import com.mill.gson.GsonHelper;
import com.okhttp.BaseResposeBean;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Response;


/**
 * Created by lulei-ms on 2018/2/28.
 */
public abstract class GsonCallback<T> extends Callback<BaseResposeBean<T>> {

    public GsonCallback() {
    }

    /**
     * 该方法还是在 子线程里面
     */
    @Override
    public BaseResposeBean parseNetworkResponse(BaseResponse response) throws IOException {
        String string = response.string();
        Type tpye = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        BaseResposeBean bean = GsonHelper.fromJson(string, BaseResposeBean.class);
        if (tpye == String.class) {
            bean.data = GsonHelper.getGsonInstance().toJson(bean.data);
        } else if (tpye == ArrayList.class) {
            String dataStr = GsonHelper.getGsonInstance().toJson(bean.data);
            bean.data = GsonHelper.fromJson(dataStr, tpye);
        } else {
            String dataStr = GsonHelper.getGsonInstance().toJson(bean.data);
            bean.data = GsonHelper.fromJson(dataStr.replace("[]", "{}"), tpye);
        }
        return bean;
    }

}
