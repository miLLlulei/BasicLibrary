package com.mill.thread;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.mill.utils.AndroidUtilsCompat;
import com.mill.utils.LogUtils;
import com.mill.utils.hideapi.ReflectUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by fengda on 2015/8/10.
 * 使用Asynctask的线程池进行后台操作，特别耗时操作如下载请自己开线程管理，以防止多个特别耗时操作把Asynctask的线程阻塞掉
 */
public class ThreadUtils {
    private static final String TAG = "ThreadUtils";

    public static class RunnableCallback implements Cancelable {
        private AsyncTask mTask;

        private RunnableCallback(AsyncTask task) {
            mTask = task;
        }

        @Override
        public void cancel() {
            if (mTask != null) {
                mTask.cancel(true);
            }
        }
    }
    /**
     * 参数runnable名称不要修改，用于AsyncTask通过该引用获取调用类；
     * 需要添加混淆配置：-keep class com.mill.utils.thread.ThreadUtils$* { *** val$runnable; }
     *
     * {@link ThreadUtils#getAsyncTaskNamesProxyOnRejectedExecution()}
     */
    public static RunnableCallback postRunnable(final Runnable runnable) {
        if (runnable != null) {
            AsyncTask<?, ?, ?> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    runnable.run();
                    return null;
                }
            }.execute();
            return new RunnableCallback(task);
        }
        return null;
    }

    public static AndroidUtilsCompat.GetAsyncTaskNamesProxyOnRejectedExecution getAsyncTaskNamesProxyOnRejectedExecution() {
        return new AndroidUtilsCompat.GetAsyncTaskNamesProxyOnRejectedExecution() {

            @Override
            public String getAsyncTaskNames(Runnable r, ThreadPoolExecutor executor) {
                StringBuilder result = new StringBuilder();
                Object outerClass = AndroidUtilsCompat.getOuterClass(r);
                if (outerClass.getClass().getName().startsWith(ThreadUtils.class.getName())) { // outerClass：com.mill.utils.thread.ThreadUtils$1
                    outerClass = ReflectUtils.getFieldValue(outerClass, "val$runnable"); // val$前缀：匿名内部类final参数名
                }
                result.append("r.OuterClass = " + outerClass);
                result.append("\nqueue.runnable.OuterClasses = [");
                BlockingQueue<Runnable> queue = executor.getQueue();
                boolean isFirst = false;
                for (Runnable task : queue) {
                    if (!isFirst) {
                        isFirst = true;
                    } else {
                        result.append("\n");
                    }
                    Object outerClassTemp = AndroidUtilsCompat.getOuterClass(task);
                    if (outerClassTemp.getClass().getName().startsWith(ThreadUtils.class.getName())) {
                        outerClassTemp = ReflectUtils.getFieldValue(outerClassTemp, "val$runnable");
                    }
                    result.append(outerClassTemp);
                }
                result.append("]");
                return result.toString();
            }
        };
    }

    public static long getCurThreadId() {
        return Thread.currentThread().getId();
    }

    public static long getMainThreadId() {
        return Looper.getMainLooper().getThread().getId();
    }

    public static String getCurThreadName() {
        String strThreadName;
        strThreadName = Thread.currentThread().getName();
        if (strThreadName == null) {
            strThreadName = "";
        }
        return strThreadName;
    }

    public static void setThreadName(String threadName) {
        Thread.currentThread().setName(threadName);
    }

    /**
     * @deprecated 用{@link {@link #getValueAtUIThread(GetValue, Object, long)}代替
     *
     * 参数 timerOut： 单位是秒
     * 参看 TransitService 用法
     * countDownLatch 的锁技术必须是1.即使，mCountDownLatch = new CountDownLatch(1);
     *
     * @param handler
     * @param countDownLatch
     * @param runnable
     */
    public static void execRunnableOnThread(Handler handler, final CountDownLatch countDownLatch, final Runnable runnable) {
        execRunnableOnThread(handler, countDownLatch, false, 0, runnable);
    }

    /**
     * @deprecated 用{@link {@link #getValueAtUIThread(GetValue, Object, long)}代替
     *
     * @param handler
     * @param countDownLatch
     * @param timerOutSecond
     * @param runnable
     */
    public static void execRunnableOnThread(Handler handler, final CountDownLatch countDownLatch, long timerOutSecond, final Runnable runnable) {
        execRunnableOnThread(handler, countDownLatch, true, timerOutSecond, runnable);
    }

    private static void execRunnableOnThread(Handler handler, final CountDownLatch countDownLatch, boolean needTimerOut, long timerOutSecond, final Runnable runnable) {
        if (getCurThreadId() == handler.getLooper().getThread().getId()) {
            runnable.run();
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    countDownLatch.countDown();
                }
            }
        });
        try {
            if (needTimerOut) {
                countDownLatch.await(timerOutSecond, TimeUnit.SECONDS);
            } else {
                countDownLatch.await();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <ReturnType> ReturnType getValueAtUIThread(final GetValue<ReturnType> returnValue, ReturnType defValue) {
        return getValueAtUIThread(returnValue, defValue, 3000);
    }

    /**
     * 在子线程调用，切换到在UI线程获取方法返回值给子线程；
     *
     * 使用场景：在子线程调用下载模块的逻辑；
     *
     * @param returnValue
     * @param <ReturnType>
     * * @param timeout 毫秒
     * @return
     */
    public static <ReturnType> ReturnType getValueAtUIThread(final GetValue<ReturnType> returnValue, ReturnType defValue, long timeout) {
        if (isMainThread()) {
            return returnValue.getValue();
        } else {
            final AtomicReference<ReturnType> atomicReference = new AtomicReference(defValue);
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        atomicReference.set(returnValue.getValue());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
            try {
                countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LogUtils.e(TAG, "getValueAtUIThread", e);
            }
            return atomicReference.get();
        }
    }

    public static <ReturnType> ReturnType getValueAtSubThread(GetValue<ReturnType> returnValue, ReturnType defValue) {
        return getValueAtSubThread(returnValue, defValue, 3000);
    }

    /**
     * 在主线程调用，切换到在子线程获取方法返回值给主线程；
     *
     * 使用场景：StrictMode严格模式不允许在UI线程调用java.io.File.exists()，可以在主线程用线程wait切换到子线程获取方法返回值，绕过系统检查；
     * 系统源码中的相同实现：android.app.SharedPreferencesImpl#awaitLoadedLocked()；
     *
     * @param returnValue
     * @param <ReturnType>
     * @param timeout 毫秒
     * @return
     */
    public static <ReturnType> ReturnType getValueAtSubThread(final GetValue<ReturnType> returnValue, ReturnType defValue, long timeout) {
        if (isMainThread()) {
            final AtomicReference<ReturnType> atomicReference = new AtomicReference(defValue);
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            PriorityThreadFactory.newThread(TAG, Process.THREAD_PRIORITY_DEFAULT, new Runnable() {
                @Override
                public void run() {
                    try {
                        atomicReference.set(returnValue.getValue());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            }).start();
            try {
                countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LogUtils.e(TAG, "getValueAtSubThread", e);
            }
            return atomicReference.get();
        } else {
            return returnValue.getValue();
        }
    }

    public interface GetValue<ReturnType> {
        ReturnType getValue();
    }

    /**
     * 在UI线程执行Runnable
     *
     * @param action
     */
    public static void runOnUiThread(Runnable action) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }

    /**
     * 在UI线程执行Runnable
     *
     * @param action
     */
    public static void postOnUiThread(Runnable action, long delayTime) {
        new Handler(Looper.getMainLooper()).postDelayed(action, delayTime);
    }

    /**
     * 在非UI线程中执行Runnable
     *
     * @param runnable
     */
    public static void runOnSubThread(Runnable runnable) {
        if (!isMainThread()) {
            runnable.run();
        } else {
            postRunnable(runnable);
        }
    }

    public static int getThreadCount() {
        return Thread.getAllStackTraces().size();
    }

    public static boolean isMainThread() {
        return getCurThreadId() == getMainThreadId();
    }

    public static void sleep(long duration){
        try{
            Thread.sleep(duration);
        }catch (Exception e){
        }
    }
}