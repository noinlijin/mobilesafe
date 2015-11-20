package cn.itcast.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.receiver.MyWidget;
import cn.itcast.mobilesafe.utils.TaskUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	private Timer timer;
	private TimerTask task;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				// 更新界面上的widget
				AppWidgetManager awm = AppWidgetManager
						.getInstance(getApplicationContext());
				RemoteViews remoteView = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				remoteView
						.setTextViewText(
								R.id.process_count,
								"正在运行进程数量:"
										+ TaskUtils
										.getRunningProcessCount(getApplicationContext()));
				remoteView
						.setTextViewText(
								R.id.process_memory,
								"可用内存:"
										+ Formatter
										.formatFileSize(
												getApplicationContext(),
												TaskUtils
														.getAvailMem(getApplicationContext())));

				Intent intent = new Intent();//定义一个自定义的广播事件
				intent.setAction("cn.itcast.killtask");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
				remoteView.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);//设置  点击事件 

				ComponentName widget = new ComponentName(getApplicationContext(), MyWidget.class);
				awm.updateAppWidget(widget, remoteView);

			}
		};
		timer.schedule(task, 1000, 2000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		timer .cancel();
		timer = null;
		task = null;
		super.onDestroy();
	}
}