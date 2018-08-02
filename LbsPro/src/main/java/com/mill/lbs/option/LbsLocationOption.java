package com.mill.lbs.option;

/**
 * Created by lulei-ms on 2018/3/5.
 */

public class LbsLocationOption {
    public static final int Hight_Accuracy = 1; //高精度；网络 | GPS
    public static final int Battery_Saving = 2; //低功耗；网络
    public static final int Device_Sensors = 3; //仅使用设备； GPS

    /**
     * 设置定位模式，默认高精度
     */
    public int mode = Hight_Accuracy;
    /**
     * 设置返回经纬度坐标类型
     * gcj02：国测局坐标；
     * bd09ll：百度经纬度坐标；
     * bd09：百度墨卡托坐标；
     */
    public String coorType = "gcj02";
    /**
     * 设置是否使用gps，默认false
     * 使用高精度和仅用设备两种定位模式的，参数必须设置为true
     */
    public boolean openGps = false;
    /**
     * 设置发起定位请求的间隔，单位ms
     * 如果设置为0，则代表单次定位，即仅定位一次
     */
    public int scanSpan = 0;
    /**
     * 设置是否当GPS有效时按照1S/1次频率输出GPS结果
     */
    public boolean location_change_notify = false;
    /**
     * 设置是否收集Crash信息
     */
    public boolean isIgnoreCacheException = false;
    /**
     * 是否在stop的时候杀死这个定位服务进程
     * 默认true：不杀死
     */
    public boolean isIgnoreKillProcess = true;
    /**
     * 首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
     */
    public int wifiCacheTimeOut = 2147483647;
    /**
     * 是否需要地址信息
     */
    public boolean isNeedAddress = true;
}
