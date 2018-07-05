package com.why.widgetdemo;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hongyun.wu@wm-holding.com.cn
 * on 2018/7/2.
 */

public class ThreadManager {

	private Context mContext;
	private HashMap<String,WidgetThread> mMap;
	private Object mThreadLock = new Object();
	private ThreadManager(Context context){
		this.mContext = context;
	}
	private static ThreadManager mManager = null;

	public static ThreadManager getInstance(Context context){
		if (mManager==null){
			synchronized (ThreadManager.class){
				if (mManager==null){
					mManager = new ThreadManager(context);
				}
			}
		}
		return mManager;
	}

	public  Handler getHandlerByName(String name){

		return getWidgetThread(name).getHandler();
	}

	private synchronized WidgetThread getWidgetThread(String name) {
		WidgetThread thread;HashMap map = getThreadMap();
		if (map.containsKey(name)){
			//取出
			thread = (WidgetThread) map.get(name);
		}else {
			//创建HandlerThread
			thread = new WidgetThread(name);
			thread.start();
			putWidgetThread(name,thread);

		}
		return thread;
	}

	private void putWidgetThread(String name, WidgetThread thread) {
		HashMap<String, WidgetThread> threadMap = getThreadMap();
		threadMap.put(name,thread);
	}

	private HashMap<String, WidgetThread> getThreadMap() {
		if (mMap==null){
			synchronized (mThreadLock){
				if (mMap==null){
					mMap = new HashMap<>();
				}
			}
		}
		return mMap;
	}

	public boolean stopWidgetThread(String name) {
		return getWidgetThread(name).quitSafely();
	}

	public class WidgetThread extends HandlerThread{
		private Handler mHandler;

		public WidgetThread(String name) {
			super(name);
		}
		@Override
		protected void onLooperPrepared() {
			getHandler();
		}

		public Handler getHandler() {
			if (mHandler==null){
				synchronized (WidgetThread.class){
					if (mHandler==null)
						mHandler = new Handler(getLooper());
				}
			}
			return mHandler;
		}
		@Override
		public boolean quitSafely() {
			getHandler().removeCallbacksAndMessages(null);
			return super.quitSafely();
		}

		@Override
		public boolean quit() {
			getHandler().removeCallbacksAndMessages(null);
			return super.quit();
		}
	}


}
