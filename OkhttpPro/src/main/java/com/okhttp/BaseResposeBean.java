package com.okhttp;

/**
 * Created by lulei-ms on 2018/3/13.
 */

public class BaseResposeBean<D> {

    public int errno;

    public String errmsg;

    public int end_state = 1;

    public D data;
}
