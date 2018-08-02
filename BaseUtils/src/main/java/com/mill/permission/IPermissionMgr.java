package com.mill.permission;

import android.app.Activity;

/**
 * TargetSDK 升级到23以上，才有效
 * Created by lulei-ms on 2018/3/10.
 */
public interface IPermissionMgr {

    /**
     * 检查 权限是否 允许
     */
    public void checkPermission(Activity activity, String permission, PermissionListener listener);

    /**
     * 申请权限 几次
     * 用于类似主页，一次性申请多个权限，为后续准备的
     */
    public void checkPermissionsTimes(Activity activity, String[] permissions, int totalCount);

    /**
     * 提供给外部 BaseActivity，onRequestPermissionsResult
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);
}
