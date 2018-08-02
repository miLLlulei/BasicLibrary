package com.mill.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mill.utils.SPUtils;

/**
 * TargetSDK 升级到23之后，可能需要用到
 * Created by lulei-ms on 2018/4/13.
 */
public class PermissionManager implements IPermissionMgr {
    public static final int REQUEST_TOTAL_COUNT = 1;

    private static final int REQUESTCODE_START = 66;

    private static IPermissionMgr mInstance;

    private int REQUESTCODE = REQUESTCODE_START;
    private PermissionListener mListener;

    private PermissionManager() {

    }

    public static IPermissionMgr getInstance() {
        if (mInstance == null) {
            synchronized (PermissionManager.class) {
                if (mInstance == null) {
                    mInstance = new PermissionManager();
                }
            }
        }
        return mInstance;
    }


    public void notifyObserver(String permission, int status) {
        if (mListener != null) {
            mListener.onPermissionCheck(permission, status);
        }
        mListener = null;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions != null && permissions.length > 0
                && grantResults != null && grantResults.length > 0) {
            notifyObserver(permissions[0], grantResults[0]);
        }
    }

    public void checkPermission(Activity activity, String permission, PermissionListener listener) {
        this.mListener = listener;
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    REQUESTCODE);
        } else {
            notifyObserver(permission, PackageManager.PERMISSION_GRANTED);
        }
    }

    public void checkPermissionsTimes(Activity activity, String[] permissions, int totalCount) {
        int count = SPUtils.getInt(null, activity, activity.getClass().getSimpleName(), 0);
        if (count < totalCount) {
            SPUtils.setInt(null, activity, activity.getClass().getSimpleName(), count + 1);
            ActivityCompat.requestPermissions(activity, permissions, REQUESTCODE_START - 1);
        }
    }
}
