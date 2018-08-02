package com.mill.utils.sdcard;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.text.TextUtils;

import com.mill.utils.ContextUtils;
import com.mill.utils.ConvertUtils;
import com.mill.utils.FileUtils;
import com.mill.utils.JsonUtils;
import com.mill.utils.hideapi.ReflectUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MemoryInfoUtils {
    private static long sTotalMemory;

    /**
     * 获取设备当前可用内存大小，单位：b
     * 从Android 2.0_r1开始支持
     *
     * @return
     */
    public static long getFreeMemory() {

        Long freeMemory = (Long) ReflectUtils.invokeStaticMethod("android.os.Process", "getFreeMemory", null); // 单位：b
        if (freeMemory != null) {
            return freeMemory;
        }
        return -1;
    }

    /**
     * 获取设备总内存大小，单位：b
     *
     * @return
     */
    public static long getTotalMemory() {
        if (sTotalMemory <= 0) {
            long totalMemory = getTotalMemoryForProcess();
            if (totalMemory >= 0) {
                sTotalMemory = totalMemory;
            }
            if (sTotalMemory <= 0) {
                sTotalMemory = getTotalMemoryForFile() * 1024; // 将单位换算为Byte
            }
        }
        return sTotalMemory;
    }

    /**
     * 从"android.os.Process"对象读取总内存大小，单位：b；
     *
     * @return
     */
    private static long getTotalMemoryForProcess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Long totalMemory = (Long) ReflectUtils.invokeStaticMethod("android.os.Process", "getTotalMemory", null); // 单位：b
            if (totalMemory != null) {
                return totalMemory;
            }
        }
        return -1;
    }


    /**
     * 从"/proc/meminfo"文件读取总内存大小，单位：kb；
     *
     * @return
     */
    private static long getTotalMemoryForFile() {
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!TextUtils.isEmpty(content)) {
            int start = content.indexOf(':') + 1;
            int end = content.indexOf('k');
            if (start >= 0 && end >= 0) {
                content = content.substring(start, end).trim();
                return Long.parseLong(content);
            }
        }
        return -1;
    }

    /**
     * 获得手机剩余内存百分比；
     *
     * @return
     */
    public static int getFreeMemoryPercent() {

        long availMemory = MemoryInfoUtils.getFreeMemory();
        long totalMemory = MemoryInfoUtils.getTotalMemory();
        if (totalMemory == 0 || totalMemory < availMemory)
            return -1;

        long lRet = availMemory * 100 / totalMemory;
        return (int) lRet;
    }

    /**
     * 获得手机已使用内存百分比；
     *
     * @return -1或者一个1到100的整数
     */
    public static int getUsedMemoryPercent() {
        int free = getFreeMemoryPercent();
        if (free == -1) {
            return -1;
        } else {
            return 100 - free;
        }
    }

    static ActivityManager mActivityManager = null;

    public static int getCurProcUssMem() {

        int nRet = 0;

        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) ContextUtils.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        }
        if (mActivityManager != null) {
            int[] pids = new int[]{android.os.Process.myPid()};
            Debug.MemoryInfo[] procMemoryInfo = mActivityManager.getProcessMemoryInfo(pids);
            nRet = procMemoryInfo[0].getTotalPrivateDirty();

            /*
            *  Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].dalvikPrivateDirty / 1024); // rss
                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].dalvikSharedDirty / 1024); // rss
                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].dalvikPss / 1024); // rss

                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].nativePrivateDirty / 1024); // r
                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].nativeSharedDirty / 1024); // r
                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].nativePss / 1024); // r

                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].otherPrivateDirty / 1024); // r
                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].otherSharedDirty / 1024); // r
                    Log.e("debugtest", "curProcess mem: " + procMemoryInfo[0].otherPss / 1024); // rss

                    Log.e("debugtest", " " ); // rss

                    Log.e("debugtest", "curProcess guess: " + (procMemoryInfo[0].dalvikPrivateDirty + procMemoryInfo[0].nativePrivateDirty) / 1024); // r

                    Log.e("debugtest", "curProcess rss: " + procMemoryInfo[0].getTotalSharedDirty() / 1024); // rss
                    Log.e("debugtest", "curProcess pss: " + procMemoryInfo[0].getTotalPss() / 1024);   // pss dalvikPss + nativePss + otherPss
                    Log.e("debugtest", "curProcess uss " + procMemoryInfo[0].getTotalPrivateDirty() / 1024); // uss  dalvikPrivateDirty + nativePrivateDirty + otherPrivateDirty*/
        }

        return nRet;

    }

    public static void saveMemInfo(String procName, int ussMem) {
        if (!TextUtils.isEmpty(procName)) {
            List<String> list = getProcMemInfo();
            Map<String, Integer> map = listToMap(list);
            Integer val = map.get(procName.toLowerCase());
            if (val == null || val < ussMem) {
                map.put(procName.toLowerCase(), ussMem);
                saveInvalidTask(mapToList(map));
            }
        }
    }

    public static Map<String, Integer> listToMap(List<String> list) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < list.size(); ++i) {
            String val = list.get(i);
            String[] array = val.split("\\|");
            if (array != null && array.length == 2 && !TextUtils.isEmpty(array[0]) && !TextUtils.isEmpty(array[1])) {
                map.put(array[0], ConvertUtils.string2Int(array[1]));
            }
        }
        return map;
    }

    private static List<String> mapToList(Map<String, Integer> map) {
        List<String> list = new ArrayList<>();
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry != null && !TextUtils.isEmpty(entry.getKey()) && entry.getValue() > 0) {
                list.add(entry.getKey().toLowerCase() + "|" + entry.getValue());
            }
        }
        return list;
    }

    public static void saveInvalidTask(List<String> list) {
        JsonUtils.jsonArrayToFile(list, getProcMemInfoFile());
    }

    public static void deleteFile() {
        FileUtils.deleteFile(getProcMemInfoFile());
    }



    public static List<String> getProcMemInfo() {
        return JsonUtils.jsonArrayFileToList(getProcMemInfoFile());
    }

    private static String getProcMemInfoFile() {
        return PathUtils.getDataDataProductPath() + "/meminfoMonitor.json";
    }
}