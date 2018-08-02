package com.mill.lbs.bean;

import android.text.TextUtils;

/**
 * Created by lulei-ms on 2018/3/5.
 */

public class LbsLocationBean {
    public double latitude;
    public double longitude;
    public String city;
    public String address;

    public boolean isVoid() {
        if (!TextUtils.isEmpty(city)) {
//                && (latitude >= -180 && latitude <= 180)
//                    && (longitude >= -180 && longitude <= 180)
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LbsLocationBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
