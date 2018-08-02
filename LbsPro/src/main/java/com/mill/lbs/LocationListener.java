package com.mill.lbs;

import com.mill.lbs.bean.LbsLocationBean;

/**
 * Created by lulei-ms on 2018/3/5.
 */

public interface LocationListener {

    /**
     * @return 返回 true ，表示 只回调一次，会移除监听
     */
    public boolean onReceiveLocation(LbsLocationBean location);

}
