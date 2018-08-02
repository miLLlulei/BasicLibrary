package com.mill.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

public class LooperHandlerThread extends HandlerThread {

    private final static String TAG = "LooperHandlerThread";

    private Handler mHandler;

    public LooperHandlerThread(String threadName) {
        super(threadName, Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public synchronized void start() {
        super.start();
        mHandler = new Handler(getLooper());
    }

    public void postTask(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    public void postTaskDelayed(Runnable runnable, long delayMillis) {
        if (mHandler != null) {
            mHandler.postDelayed(runnable, delayMillis);
        }
    }
}
