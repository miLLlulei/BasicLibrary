package com.mill.permission;

/**
 * Created by lulei-ms on 2018/3/10.
 */

public interface PermissionListener {

    /**
     * status: PackageManager.PERMISSION_GRANTED
     */
    public void onPermissionCheck(String permission, int status);

}
