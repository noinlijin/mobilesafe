package cn.itcast.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.mobilesafe.EnterPasswordActivity;
import cn.itcast.mobilesafe.IService;
import cn.itcast.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * 采用绑定服务的方式 调用服务里面的方法 让程序临时停止对某个应用程序的保护.
 * @author Administrator
 *
 */
public class WatchDogService2 extends Service {
	private ActivityManager am;
	private boolean flag = true;
	private AppLockDao dao;
	private Intent lockIntent;
	private List<String> tempStopProtectNames;
	private LockScreenReceiver lockScreenReceiver;
	private UnLockScreenReceiver unlockScreenReceiver;

	private List<String> lockedPacknames;

	private AppLockObserver observer;

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("IBinder","服务onbind");
		return new MyBinder();
	}
	/**
	 * 远程代理对象,继承Binder并实现远程接口
	 * @author superboy
	 *
	 */
	private class MyBinder extends Binder implements IService{

		public void callTempStopProtect(String packname) {
			tempStopProtect(packname);
		}

	}



	//服务里面提供的一个方法.
	public void tempStopProtect(String packname){
		tempStopProtectNames.add(packname);
	}


	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("LockScreenReceiver", "手机锁屏了");
			tempStopProtectNames.clear();
			flag = false;

		}

	}

	private class UnLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("LockScreenReceiver", "手机解锁了");
			flag = true;
			startWatchDog();
		}

	}
	/**
	 *  内容观察者Uri.parse("content://cn.itcast.mobile/applock")
	 * @author superboy
	 *
	 */
	private class AppLockObserver extends ContentObserver{

		public AppLockObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("AppLockObserver","数据库的内容变化了.");
			lockedPacknames = dao.findAll();
		}

	}

	@Override
	public void onCreate() {


		lockScreenReceiver = new LockScreenReceiver();
		IntentFilter lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(lockScreenReceiver, lockFilter);

		unlockScreenReceiver = new UnLockScreenReceiver();
		IntentFilter unlockFilter = new IntentFilter();
		unlockFilter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(unlockScreenReceiver, unlockFilter);


		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tempStopProtectNames = new ArrayList<String>();
		dao = new AppLockDao(this);
		//把所有锁定的应用程序的信息 放入到内存集合里面.

		lockedPacknames = dao.findAll();
		//注册一个内容变化的观察者. 观察数据的变化.
		observer = new AppLockObserver(new Handler());
		getContentResolver().registerContentObserver(AppLockDao.applockuri, true, observer);


		lockIntent = new Intent(this, EnterPasswordActivity.class);
		lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//服务中开启activity必须 添加 flag
		startWatchDog();

		super.onCreate();
	}

	private void startWatchDog() {
		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(1);// 获取最近打开的前1个任务栈
					RunningTaskInfo taskinfo = infos.get(0);// 获取最近打开的任务
					ComponentName activityinfo = taskinfo.topActivity; // 当前用户要看到的activity.
					String packname = activityinfo.getPackageName();
					Log.i("WATCHDOG",packname);
					// 判断 判断当前程序的包名是否要被锁定.
					//if (dao.find(activityinfo.getPackageName())) { //查询数据库
					if(lockedPacknames.contains(packname)){
						// 要保护.
						// 判断是否要临时停止保护.
						if (tempStopProtectNames.contains(packname)) {
							// 临时停止保护的...
						} else {
							lockIntent.putExtra("packname",
									packname);
							startActivity(lockIntent);
						}
					}
					//1. 别的应用程序杀死了. 进程管理器 杀死别的没用的进程.
					//2. 
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("Binder","服务被接触绑定");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.i("Binder","服务被销毁了");
		super.onDestroy();
		unregisterReceiver(lockScreenReceiver);//卸载广播接受者
		lockScreenReceiver = null;
		unregisterReceiver(unlockScreenReceiver);
		flag = false;
	}
}