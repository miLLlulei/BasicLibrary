package com.mill.lbs;

import android.app.Activity;

import com.mill.lbs.bean.LbsLocationBean;
import com.mill.lbs.option.LbsLocationOption;


/**
 * Created by lulei-ms on 2018/3/2.
 */

public interface ILbs {

    /**
     * 定位，带上条件
     * @see LbsLocationOption
     */
    public void startMonitor(Activity activity, LbsLocationOption option);

    public void stopMonitor();

    /**
     * 上次定位 内存中的位置信息，可能为null
     * @return
     */
    public LbsLocationBean getCurrentLocation();

    /**
     * 只定位一次，回调
     * 记得 deleteListener
     * @param listener
     */
    public void getOnceLocation(Activity activity, LocationListener listener);

    public void addListener(LocationListener listener);

    public void deleteListener(LocationListener listener);
}
