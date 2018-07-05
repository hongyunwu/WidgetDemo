package com.why.widgetdemo;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UpdateWidgetService extends Service {
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_DELETE = "delete";
	private static final String TAG = "UpdateWidgetService";
	public static final String ACTION_WIDGET_CONFIGURE = "configure";
	private SimpleDateFormat mFormat;
	private HashMap<String,Calendar> mCalendarHashMap = new HashMap<>();
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mFormat = new SimpleDateFormat("MM月dd日HH时mm分ss秒");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action)){
			switch (action){
				case ACTION_UPDATE:
					//更新某个widget
					int[] updates = intent.getIntArrayExtra("appWidgetIds");
					if (updates!=null&&updates.length>0){
						for (int appWidgetId :
								updates) {
							updateWidget(appWidgetId, getCalendarFromMap(generateName(appWidgetId)));
						}
					}
					break;
				case ACTION_DELETE:
					//删除某个widget
					int[] deletes = intent.getIntArrayExtra("appWidgetIds");
					if (deletes!=null&&deletes.length>0){
						for (int appWidgetId :
								deletes) {
							deleteWidget(appWidgetId);
						}
					}
					break;
				case ACTION_WIDGET_CONFIGURE:
					int appWidgetId = intent.getIntExtra("appWidgetId",AppWidgetManager.INVALID_APPWIDGET_ID);
					if (appWidgetId!=AppWidgetManager.INVALID_APPWIDGET_ID){
						Calendar calendar = (Calendar) intent.getSerializableExtra("calendar");
						if (calendar!=null){
							putCalendarToMap(generateName(appWidgetId),calendar);
							updateWidget(appWidgetId,calendar);
						}
					}
					break;
			}
		}
		return START_NOT_STICKY;
	}

	private void putCalendarToMap(String name, Calendar calendar) {
		mCalendarHashMap.put(name,calendar);
	}

	private Calendar getCalendarFromMap(String name) {
		Calendar calendar;
		if (mCalendarHashMap.containsKey(name)){
			calendar = mCalendarHashMap.get(name);
		}else {
			calendar = Calendar.getInstance();
			putCalendarToMap(name,calendar);
		}
		return calendar;
	}

	private void deleteWidget(int appWidgetId) {
		ThreadManager
			.getInstance(getApplicationContext())
			.stopWidgetThread(generateName(appWidgetId));
	}

	private void updateWidget(final int appWidgetId, final Calendar calendar) {

		final Handler handler = ThreadManager
				.getInstance(getApplicationContext())
				.getHandlerByName(generateName(appWidgetId));
		handler.removeCallbacksAndMessages(null);
		handler
				.post(new Runnable() {
					@Override
					public void run() {
						handler.removeCallbacks(this);
						/**
						 * 两种获取Manager方式
						 * AppWidgetManager manager = (AppWidgetManager) getSystemService(Context.APPWIDGET_SERVICE);
						 */
						AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
						AppWidgetProviderInfo appWidgetInfo = manager.getAppWidgetInfo(appWidgetId);
						RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.widget_layout);
						calendar.setTimeInMillis(calendar.getTimeInMillis()+100);
						remoteViews.setTextViewText(R.id.time,generateName(appWidgetId)+"\n"+mFormat.format(calendar.getTime()));
						manager.updateAppWidget(appWidgetId,remoteViews);
						handler.postDelayed(this,100);
						Log.i(TAG,"updateWidget-"+appWidgetId);
					}
				});
	}

	@NonNull
	private String generateName(int appWidgetId) {
		return "appWidgetId-" + appWidgetId;
	}
}
