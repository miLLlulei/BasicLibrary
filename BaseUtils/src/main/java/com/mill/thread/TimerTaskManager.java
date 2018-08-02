/*
 * 创建日期：2012-11-16
 */
package com.mill.thread;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 版权所有 (c) 2012 北京新媒传信科技有限公司。 保留所有权利。<br>
 * 项目名：飞信 - Android客户端<br>
 * 描述：<br>
 * @author zhangguojunwx
 * @version 1.0
 * @since JDK1.5
 */
public final class TimerTaskManager {
	private final Handler mHandler;

	public TimerTaskManager(String tag) {
		HandlerThread handlerThread = new HandlerThread(tag, PriorityThreadFactory.THREAD_PRIORITY_DEFAULT);
		handlerThread.start();
		mHandler = new Handler(handlerThread.getLooper());
	}

	public void addTask(Runnable task, long delay) {
		mHandler.postDelayed(task, delay);
	}

	public void removeTask(Runnable task) {
		mHandler.removeCallbacks(task);
	}

	public void destroy() {
		mHandler.getLooper().quit();
	}
}