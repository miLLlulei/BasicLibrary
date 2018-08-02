package com.mill.thread;

import android.os.Looper;
import android.os.Process;


import com.mill.utils.LogUtils;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class LooperThread extends Thread {

    private final static String TAG = "LooperThread";
    private String threadName;
    private volatile boolean mQuit = false;

    private final BlockingDeque<Runnable> mQueue = new LinkedBlockingDeque<>();

    public LooperThread(String threadName) {
        this.threadName = threadName;
    }

    public void run() {
        ThreadUtils.setThreadName(threadName);

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Looper.prepare();

        Runnable runnable;

        while (true) {
            // release previous request object to avoid leaking request object when mQueue is drained.
            try {
                runnable = mQueue.take();
                if (runnable != null) {
                    LogUtils.d(TAG, "runTask: begin " + runnable.toString());
                    runnable.run();
                    LogUtils.d(TAG, "runTask: end " + runnable.toString());
                }
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }
        }
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }

    public void postTask(Runnable runnable) {

        LogUtils.d("LocalApkMgr", "postTask " + mQueue.size() + " " + runnable);

        mQueue.add(runnable);
    }
}
