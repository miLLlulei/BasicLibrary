package com.mill.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class DeviceUtils {

    public static boolean isDebuggable() {
        try {
            PackageManager pm = ContextUtils.getApplicationContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(ContextUtils.getApplicationContext().getPackageName(), 0);
            return (0 != (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}