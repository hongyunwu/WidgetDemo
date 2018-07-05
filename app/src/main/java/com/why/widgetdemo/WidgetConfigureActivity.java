package com.why.widgetdemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WidgetConfigureActivity extends AppCompatActivity implements View.OnClickListener {
	private static final String TAG = "WidgetConfigureActivity";
	private int appWidgetId;
	private Button btn_choose_date;
	private Button btn_choose_time;
	private Button btn_confirm;
	private Calendar calendar;
	private TextView txt_time;
	private SimpleDateFormat format;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_configure);
		checkWidgetId();
		bindViews();

	}

	private void checkWidgetId() {
		Bundle extras = getIntent().getExtras();
		if (extras!=null){
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		if (appWidgetId==AppWidgetManager.INVALID_APPWIDGET_ID){
			//当前没有传递appWidgetId
			Log.i(TAG, "onCreate: appWidgetId is invalid");
			finish();
		}
	}

	private void bindViews() {
		btn_choose_date = findViewById(R.id.btn_choose_date);
		btn_choose_time = findViewById(R.id.btn_choose_time);
		btn_confirm = findViewById(R.id.btn_confirm);
		txt_time = findViewById(R.id.txt_time);
		btn_choose_date.setOnClickListener(this);
		btn_choose_time.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
		calendar = Calendar.getInstance();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		txt_time.setText(format.format(calendar.getTime()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_choose_date:
				DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

						Log.i(TAG,"year:"+year+",month:"+month+",day:"+dayOfMonth);
						calendar.set(year,month,dayOfMonth);
						txt_time.setText(format.format(calendar.getTime()));
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
				break;
			case R.id.btn_choose_time:
				TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						Log.i(TAG,"hour:"+hourOfDay+",minute:"+minute);
						calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
						calendar.set(Calendar.MINUTE,minute);
						txt_time.setText(format.format(calendar.getTime()));
					}
				}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
				timePickerDialog.show();
				break;
			case R.id.btn_confirm:
				Intent intent = new Intent(getApplicationContext(), UpdateWidgetService.class);
				intent.setAction(UpdateWidgetService.ACTION_WIDGET_CONFIGURE);
				intent.putExtra("calendar",calendar);
				intent.putExtra("appWidgetId",appWidgetId);
				startService(intent);
				setResult(RESULT_OK);
				finish();
				break;
		}

	}
}
