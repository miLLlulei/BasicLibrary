package com.mill.utils;

import android.content.Context;

public class ConfigUtils {

    private static boolean logable = false;

    public static boolean isLogable(Context context) {
        return false;
    }

    public static boolean isUseTestHost(Context context) {
        return false;
    }

    public static boolean isBetaVersion(Context context) {
        return false;
    }

    public static String getChannel(Context context) {
        return "";
    }

}
