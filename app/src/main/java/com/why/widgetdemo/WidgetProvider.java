package com.why.widgetdemo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by hongyun.wu@wm-holding.com.cn
 * on 2018/6/27.
 */

public class WidgetProvider extends AppWidgetProvider {
	private static final String TAG = "WidgetProvider";

	/**
	 *更新操作，每当用户添加一个当前widget到桌面就会回调此方法一次，
	 * 默认情况下根据widget_info中的updatePeriodMillis时间更新，如果updatePeriodMillis最小值不可小于30分钟
	 * 小于30系统也只会按30min/次更新
	 *
	 * @param context 当前应用所在的上下文环境
	 * @param appWidgetManager manager，如果不是需要实时更新widget，则可以直接在方法中使用manager进行更新
	 * @param appWidgetIds 一般此处都为1个,当应用更新版本时，当前数组中可能为多个
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i(TAG, "onUpdate: "+Arrays.toString(appWidgetIds)+", context:"+context.getPackageName());
		Intent intent = new Intent(context, UpdateWidgetService.class);
		//把更改的appWidgetIds
		intent.putExtra("appWidgetIds",appWidgetIds);
		intent.setAction(UpdateWidgetService.ACTION_UPDATE);
		context.startService(intent);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i(TAG, "onDeleted: "+ Arrays.toString(appWidgetIds));
		Intent intent = new Intent(context, UpdateWidgetService.class);
		//把更改的appWidgetIds
		intent.putExtra("appWidgetIds",appWidgetIds);
		intent.setAction(UpdateWidgetService.ACTION_DELETE);
		context.startService(intent);

	}


	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i(TAG, "onEnabled: ");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.i(TAG, "onDisabled: ");
	}
}
