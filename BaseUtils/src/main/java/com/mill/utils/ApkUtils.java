package com.mill.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import com.mill.utils.sdcard.MimeUtils;
import com.mill.utils.sdcard.PathUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ApkUtils {

    private final static String TAG = "ApkUtils";

    public static String getSelfInstallPath() {
        return getApkInstallPath(ContextUtils.getApplicationContext().getPackageName());
    }

    public static String getApkInstallPath(String packageName) {
        LogUtils.safeCheck(!TextUtils.isEmpty(packageName));
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }

        PackageInfo pi = null;
        try {
            PackageManager pm = ContextUtils.getApplicationContext().getPackageManager();
            pi = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pi != null && pi.applicationInfo != null && pi.applicationInfo.sourceDir != null) {
            return pi.applicationInfo.sourceDir;
        }
        return null;
    }

    public static String getPackageSignMd5(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            LogUtils.safeCheck(false);
            return null;
        }

        try {
            PackageManager packageManager = ContextUtils.getApplicationContext().getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (info != null && info.signatures != null && info.signatures.length > 0) {
                byte[] signature = info.signatures[0].toByteArray();
                return Md5Utils.md5LowerCase(Arrays.toString(signature));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static int getVersionCodeByPackageName(String packname, int flags) {
        int versionCode = -1;
        try {
            PackageManager pm = ContextUtils.getApplicationContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packname, flags);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static boolean isApkInstalled(String packageName) {
        PackageInfo pi = null;
        try {
            PackageManager pm = ContextUtils.getApplicationContext().getPackageManager();
            pi = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pi != null;
    }

    public static boolean install(Context context, String apkFilePath) {
        if (TextUtils.isEmpty(apkFilePath)) {
            return false;
        }
        File targetFile = new File(apkFilePath);
        if (targetFile.getAbsolutePath().startsWith(
                PathUtils.getCacheDir(context))) {
            FileUtils.changeFileMode(targetFile, "755");
        }
        Intent installIntent = new Intent("android.intent.action.VIEW");
        Uri localUri = Uri.parse("file://" + apkFilePath);
        installIntent.setDataAndType(localUri, MimeUtils.APK_MIMETPYE_PREFIX);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (!(context instanceof Activity)) {
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(installIntent);
            return true;
        }catch (Exception e){
        }
        return false;
    }

    private static HashMap<String, String> enumInstallerActivities(Context context, Intent installIntent) {
        HashMap<String, String> installerActivities = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        try {
            if (pm != null) {
                List<ResolveInfo> l = pm.queryIntentActivities(installIntent, 0);
                for (ResolveInfo resolveInfo : l) {
                    if (resolveInfo != null && resolveInfo.activityInfo != null && !TextUtils.isEmpty(resolveInfo.activityInfo.packageName)
                            && !TextUtils.isEmpty(resolveInfo.activityInfo.name)) {
                        installerActivities.put(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    }
                }
            }
        } catch (Exception e) {
        }
        return installerActivities;
    }
}
