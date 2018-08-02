package com.mill.utils.hideapi;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HideApiHelper {

    // android.util.DisplayMetrics
    public static final int DENSITY_DEVICE;

    static {
        DENSITY_DEVICE = getDeviceDensity();
    }

    private static int getDeviceDensity() {
        return HideApiHelper.SystemProperties.getInt("qemu.sf.lcd_density", HideApiHelper.SystemProperties.getInt("ro.sf.lcd_density", 160));
    }

    public static class ServiceManager {

        public static void addService(String serviceName, IBinder service) {
            try {
                Class.forName("android.os.ServiceManager")
                        .getDeclaredMethod("addService", String.class, IBinder.class)
                        .invoke(null, serviceName, service);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static IBinder getService(String serviceName) {
            try {
                return (IBinder) Class.forName("android.os.ServiceManager")
                        .getDeclaredMethod("getService", String.class)
                        .invoke(null, serviceName);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static IBinder checkService(String serviceName) {
            try {
                return (IBinder) Class.forName("android.os.ServiceManager")
                        .getDeclaredMethod("checkService", String.class)
                        .invoke(null, serviceName);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class SystemProperties {

        public static int getInt(String key) {
            try {
                return (Integer) Class.forName("android.os.SystemProperties")
                        .getDeclaredMethod("get", String.class)
                        .invoke(null, key);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static int getInt(String key, int def) {
            try {
                return (Integer) Class.forName("android.os.SystemProperties")
                        .getDeclaredMethod("getInt", String.class, int.class)
                        .invoke(null, key, def);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static String get(String key) {
            try {
                return (String) Class.forName("android.os.SystemProperties")
                        .getDeclaredMethod("get", String.class)
                        .invoke(null, key);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static String get(String key, String def) {
            try {
                return (String) Class.forName("android.os.SystemProperties")
                        .getDeclaredMethod("get", String.class, String.class)
                        .invoke(null, key, def);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Threads {

        public static long getOrCreateThreadId(Context context, String address) {
            try {
                return (Long) Class.forName(ReflectUtils.CLASSNAME_THREADS)
                        .getDeclaredMethod("getOrCreateThreadId", Context.class, String.class)
                        .invoke(null, context, address);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ActivityManagerNative {

        public static Object getDefault() {
            try {
                return Class.forName("android.app.ActivityManagerNative")
                        .getMethod("getDefault")
                        .invoke(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 只是对API>=18以上rom有效
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static Object getContentProviderExternal(String providerName, int userId, IBinder token) {
            try {
                Class clazz = Class.forName("android.app.ActivityManagerNative");
                Method getContentProviderExternal = clazz.
                        getMethod("getContentProviderExternal", String.class, int.class, IBinder.class);
                return getContentProviderExternal.invoke(getDefault(), providerName, userId, token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 只是对API>=18以上rom有效
         */
        public static void removeContentProviderExternal(String providerName, IBinder token) {
            try {
                Class clazz = Class.forName("android.app.ActivityManagerNative");
                Method removeContentProviderExternal = clazz.
                        getMethod("removeContentProviderExternal", String.class, IBinder.class);
                removeContentProviderExternal.invoke(getDefault(), providerName, token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static class QuotedPrintableCodec {

        public static byte[] decodeQuotedPrintable(byte[] bytes) {
            try {
                return (byte[]) Class.forName("org.apache.commons.codec.net.QuotedPrintableCodec")
                        .getDeclaredMethod("decodeQuotedPrintable", byte[].class)
                        .invoke(null, (Object) bytes);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static File getSharedPrefsFile(Object context, String sharedPrefsFileName) {
        try {
            Method method = context.getClass().getMethod("getSharedPrefsFile", String.class);
            method.setAccessible(true);
            return (File) method.invoke(context, sharedPrefsFileName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object currentActivityThread() {
        try {
            return Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentActivityThread")
                    .invoke(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Context getSystemContext(Object activityThread) {
        try {
            return (Context) activityThread.getClass()
                    .getDeclaredMethod("getSystemContext")
                    .invoke(activityThread);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class getActivityThreadClass() {
        try {
            return Class.forName("android.app.ActivityThread");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Application currentApplication() {
        try {
            return (Application) Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication")
                    .invoke(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // android.content.pm.IPackageManager Helper Methods
    // 5.0.2 r1
    // public int checkUidPermission(java.lang.String permName, int uid) throws android.os.RemoteException;
    public static int checkUidPermission(Object pm, String permName, int uid) {
        try {
            return (Integer) Class.forName(ReflectUtils.CLASSNAME_IPACKAGEMANAGER)
                    .getMethod("checkUidPermission", String.class, int.class)
                    .invoke(pm, permName, uid);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //2.3 r1
    //public abstract String[] getPackagesForUid(int uid);
    //2.3.1
    //public abstract String[] getPackagesForUid(int uid);
    //2.3.3 r1
    //public abstract String[] getPackagesForUid(int uid);
    //2.3.6 r1
    //public abstract String[] getPackagesForUid(int uid);
    //4.4.4 r1
    //public abstract String[] getPackagesForUid(int uid);
    //5.0.1 r1
    //public abstract String[] getPackagesForUid(int uid);
    public static String[] getPackagesForUid(Object pm, int uid) {
        try {
            return (String[]) Class.forName(ReflectUtils.CLASSNAME_IPACKAGEMANAGER)
                    .getMethod("getPackagesForUid", int.class)
                    .invoke(pm, uid);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // android.conent.pm.PackagePaser Helper Methods
    // 此API仅适用于 API<21 的版本，助手的原本代码，没有改动过
    public static Object parsePackage(Object packageParser, File sourceFile, String destCodePath, DisplayMetrics metrics, int flags) {
        try {
            return packageParser.getClass()
                    .getDeclaredMethod("packageParser", File.class, String.class, DisplayMetrics.class, int.class)
                    .invoke(packageParser, sourceFile, destCodePath, metrics, flags);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // 此API仅适用于 API<21 的版本，助手的原本代码，没有改动过
    public static boolean collectCertificates(Object packageParser, Object pkg, int flags) {
        try {
            return (Boolean) packageParser.getClass()
                    .getDeclaredMethod("collectCertificates", pkg.getClass(), int.class)
                    .invoke(packageParser, pkg, flags);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // IActivityManager Helper Methods
    //TODO 需要做版本适配
    public static Object getServicesFromIActivityManager(Object tar, int arg0, int arg1) {
        try {
            return tar.getClass()
                    .getMethod("getServices", int.class, int.class)
                    .invoke(tar, arg0, arg1);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public static class Content {
        /**
         * 只是对API>=18以上rom有效
         */
        public static void call(String authority, String callingPkg, String method, String arg, Bundle extras) throws Exception {
            //android.content.IContentProvider
            Object provider = null;
            IBinder token = new Binder();
            try {
                Object holder = HideApiHelper.ActivityManagerNative.getContentProviderExternal(
                        authority, 0, token);
                if (holder == null) {
                    throw new IllegalStateException("Could not find provider: " + authority);
                }
                Field field = holder.getClass().getField("provider");
                provider = field.get(holder);
                //public Bundle call(String callingPkg, String method, String arg, Bundle extras)
                Method call = Class.forName("android.content.IContentProvider").getMethod("call", String.class, String.class, String.class, Bundle.class);
                call.invoke(provider, callingPkg, method, arg, extras);
            } finally {
                if (provider != null) {
                    HideApiHelper.ActivityManagerNative.removeContentProviderExternal(authority, token);
                }
            }

        }
    }
}
