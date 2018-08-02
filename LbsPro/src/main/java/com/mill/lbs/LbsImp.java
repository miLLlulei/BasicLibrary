package com.mill.lbs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lbs.R;
import com.mill.permission.PermissionListener;
import com.mill.permission.PermissionManager;
import com.mill.utils.ContextUtils;
import com.mill.utils.LogUtils;
import com.mill.utils.ToastUtil;
import com.mill.lbs.bean.LbsLocationBean;
import com.mill.lbs.option.LbsLocationOption;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by lulei-ms on 2018/3/5.
 */
public class LbsImp implements ILbs {
    private final static String TAG = "LocationUtil";
    private static ILbs mInstance;

    private BDAbstractLocationListener myListener = new MyLocationListener();
    private LocationClient mLocationClient;

    private BDLocation mLocation = null;
    private LbsLocationBean mBaseLocation = new LbsLocationBean();

    private ArrayList<LocationListener> mOutListeners;

    public static ILbs getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LbsImp(context);
        }
        return mInstance;
    }

    private LbsImp(Context context) {
        mLocationClient = new LocationClient(context.getApplicationContext());
        mLocationClient.setLocOption(getDefaultOption());
        mLocationClient.registerLocationListener(myListener);
    }

    public void startMonitor(final Activity activity, final LbsLocationOption option) {
        LocationClientOption bdOption = parse2BdOption(option);
        startMonitorImp(activity, bdOption);
    }

    private LocationClientOption parse2BdOption(LbsLocationOption src) {
        LocationClientOption option = null;
        if (src != null) {
            option = new LocationClientOption();
            if (src.mode == LbsLocationOption.Hight_Accuracy) {
                option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            } else if (src.mode == LbsLocationOption.Battery_Saving) {
                option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            } else if (src.mode == LbsLocationOption.Device_Sensors) {
                option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
            }
            option.setOpenGps(src.openGps);
            option.setLocationNotify(src.location_change_notify);
            option.setScanSpan(src.scanSpan);
            option.setCoorType(src.coorType);
            option.setIsNeedAddress(src.isNeedAddress);
            option.setIgnoreKillProcess(src.isIgnoreKillProcess);
            option.SetIgnoreCacheException(src.isIgnoreCacheException);
            option.setWifiCacheTimeOut(src.wifiCacheTimeOut);
        }
        return option;
    }

    private void startMonitorImp(final Activity activity, LocationClientOption option) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "start monitor location");
        }
        if (option == null) {
            option = getDefaultOption();
        }
        mLocationClient.setLocOption(option);

        PermissionManager.getInstance().checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, new PermissionListener() {
            @Override
            public void onPermissionCheck(String permission, int status) {
                if (status != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.showShort(ContextUtils.getApplicationContext(), R.string.permission_lbs_no);
                }
                if (mLocationClient != null) {
                    if (mLocationClient.isStarted()) {
                        mLocationClient.requestLocation();
                    } else {
                        mLocationClient.restart();
                    }
                }
            }
        });
    }

    public void stopMonitor() {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "stop monitor location");
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    public LbsLocationBean getCurrentLocation() {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "get location");
        }
        return mBaseLocation;
    }

    /**
     * 异步的
     * 建议提前调用，有时候很慢
     *
     * @param listener
     */
    public void getOnceLocation(final Activity activity, final LocationListener listener) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "get once location");
        }
        addListener(listener);
        startMonitorImp(activity, getOnceOption());
    }

    public void addListener(LocationListener listener) {
        if (LogUtils.isDebug()) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                LogUtils.safeCheck(false, "addObserver not in main thread!");
            }
        }
        if (mOutListeners == null) {
            mOutListeners = new ArrayList<>();
        }
        if (listener != null) {
            if (!mOutListeners.contains(listener)) {
                this.mOutListeners.add(listener);
            }
        }
    }

    public void deleteListener(LocationListener listener) {
        if (LogUtils.isDebug()) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                throw new RuntimeException("deleteObserver not in main thread!");
            }
        }
        if (mOutListeners != null) {
            if (listener != null) {
                this.mOutListeners.remove(listener);
            }
        }
    }

    /**
     * 5秒定位一次
     *
     * @return
     */
    private LocationClientOption getDefaultOption() {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(false);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setScanSpan(5000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setCoorType("gcj02");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        return option;
    }

    /**
     * 定位一次
     *
     * @return
     */
    private LocationClientOption getOnceOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setLocationNotify(false);
        option.setScanSpan(0);
        option.setCoorType("gcj02");
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        return option;
    }


    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            mLocation = location;
            mBaseLocation.latitude = mLocation.getLatitude();
            mBaseLocation.longitude = mLocation.getLongitude();
            mBaseLocation.city = mLocation.getCity();
            mBaseLocation.address = mLocation.getAddrStr();

            if (mOutListeners != null) {
                Iterator<LocationListener> iterator = mOutListeners.iterator();
                while (iterator.hasNext()) {
                    LocationListener outListener = iterator.next();
                    if (outListener.onReceiveLocation(mBaseLocation)) {
                        iterator.remove();
                    }
                }
            }

            if (LogUtils.isDebug()) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("onReceiveLocation time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                }
                Log.d(TAG, "" + sb);
            }
        }
    }
}
