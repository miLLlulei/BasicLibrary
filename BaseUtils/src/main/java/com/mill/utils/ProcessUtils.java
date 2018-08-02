package com.mill.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;


import com.mill.utils.hideapi.ReflectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {

    private static String sCurrentProcessName;

    public static String getProcessName(Context context, int pid) {
        String processName = null;
        StringBuilder errorMsg = new StringBuilder();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Object objActivityThread = null;
            Object objAppBindData = null;
            try {
                objActivityThread = ReflectUtils.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", null);
                objAppBindData = ReflectUtils.getFieldValue(objActivityThread, "mBoundApplication");
                processName = (String) ReflectUtils.getFieldValue(objAppBindData, "processName");
            } catch (Throwable e) {
                if (objActivityThread == null) {
                    try {
                        errorMsg.append(" methods = ").append(Class.forName("android.app.ActivityThread").getDeclaredMethods());
                    } catch (ClassNotFoundException ignore) {
                    }
                } else {
                    if (objAppBindData == null) {
                        errorMsg.append(" fields = ").append(objActivityThread.getClass().getDeclaredFields());
                    } else {
                        errorMsg.append(" fields2 = ").append(objAppBindData.getClass().getDeclaredFields());
                    }
                }
            }
        } else {
            try {
                processName = (String) ReflectUtils.invokeStaticMethod("android.app.ActivityThread", "currentProcessName", null);
            } catch (Throwable e) {
                try {
                    errorMsg.append(" methods = ").append(Class.forName("android.app.ActivityThread").getDeclaredMethods());
                } catch (ClassNotFoundException ignore) {
                }
            }
        }
        if (TextUtils.isEmpty(processName)) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                runningAppProcessInfo = activityManager.getRunningAppProcesses();
                if (runningAppProcessInfo != null) {
                    for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfo) {
                        if (appProcess != null && appProcess.pid == pid) {
                            processName = appProcess.processName;
                            break;
                        }
                    }
                }
            }
            if (TextUtils.isEmpty(processName)) {
                errorMsg.append(" runningAppProcessInfo = ").append(runningAppProcessInfo);
                processName = getCurProcessNameByProcInfo(pid);
                if (TextUtils.isEmpty(processName)) {
                    try {
                        errorMsg.append(" cmdlineBase64 = ").append(Base64.encodeToString(FileUtils.readFileToBytes(new File("/proc/" + pid + "/cmdline")), Base64.DEFAULT));
                    } catch (Throwable ignore) {
                    }
//                    CrashHandler.getInstance().tryCatch(new RuntimeException("getProcessName"), errorMsg.toString());
                }
            }
        }
        return processName;
    }

    public static String getCurrentProcessName() {
        if (TextUtils.isEmpty(sCurrentProcessName)) {
            sCurrentProcessName = getProcessName(ContextUtils.getApplicationContext(), android.os.Process.myPid());
        }
        return sCurrentProcessName;
    }

    public static boolean isCurrentMainProcess() {
        return ContextUtils.getApplicationContext().getPackageName().equals(getCurrentProcessName());
    }

    private static String getCurProcessNameByProcInfo(long pid) {

        File file = new File("/proc/" + pid + "/cmdline");

        byte[] bytesData = FileUtils.readFileToBytes(file);
        int i = 0;
        for (; i < bytesData.length; ++i) {
            if ((bytesData[i] >= 'a' && bytesData[i] <= 'z')
                    || (bytesData[i] >= 'A' && bytesData[i] <= 'Z')
                    || (bytesData[i] >= '0' && bytesData[i] <= '9')
                    || (bytesData[i] == '.')
                    || (bytesData[i] == ':')
                    ) {
                continue;
            } else {
                bytesData[i] = '\0';
                break;
            }
        }

        try {
            return new String(bytesData).substring(0, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获得进程名以processName开始的进程
    public static List<String> getPidsStartWithProcessName(String processName) {
        return getPidsByProcessNameImpl(processName, true);
    }

    // 获得进程名是processName的进程
    public static List<String> getPidsByProcessName(String processName) {
        return getPidsByProcessNameImpl(processName, false);
    }

    private static List<String> getPidsByProcessNameImpl(String processName, boolean matchStart) {
        File[] files = new File("/proc").listFiles(sFilter);
        ArrayList<String> pids = new ArrayList<String>();
        if (files != null && files.length >= 1) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(new File(file.getPath(),
                            "cmdline")));
                    String cmdline = reader.readLine();
                    if (cmdline != null) {
                        cmdline = cmdline.trim();
                    }

                    if (matchStart) {
                        if (cmdline != null && cmdline.startsWith(processName)) {
                            pids.add(file.getName());
                        }
                    } else {
                        if (cmdline != null && cmdline.equalsIgnoreCase(processName)) {
                            pids.add(file.getName());
                        }
                    }

                } catch (Exception e) {

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable e) {
                        }
                    }
                }
            }
        }
        return pids;
    }

    public static boolean isAppRunning(Context context, String pkgName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
            if (infos != null) {
                for (ActivityManager.RunningAppProcessInfo process : infos) {
                    if (process.pkgList != null && !TextUtils.isEmpty(pkgName)) {
                        for (String pName : process.pkgList) {
                            if (TextUtils.equals(pName, pkgName)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static final FilenameFilter sFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.matches("\\d+");
        }
    };

    private static boolean isProcessRunningL21(Context context, String processName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos == null) {
            return false;
        }
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(processName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isProcessRunningB21(String processName) {
        BufferedReader input = null;
        Process p = null;
        try {
            String line;
            p = Runtime.getRuntime().exec("ps");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.trim().endsWith(processName)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (p != null) {
                p.destroy();
            }
        }

        return false;
    }

    /**
     * jinmeng
     * 兼容方式判断进程是否存在
     */
    public static boolean isProcessRunning(Context context, String processName) {
        int curSdkVersion = Build.VERSION.SDK_INT;
        if (curSdkVersion < 21) {
            return isProcessRunningL21(context, processName);
        } else {
            return isProcessRunningB21(processName);
        }
    }
}
