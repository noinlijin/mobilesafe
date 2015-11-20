package cn.itcast.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.itcast.mobilesafe.CallSmsSafeActivity;
import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.db.dao.BlackNumberDao;

import com.android.internal.telephony.ITelephony;
/**
 * 黑名单拦截服务 
 * @author superboy
 *
 */
public class CallSmsFirewallService extends Service {

	public static final String TAG = "CallSmsFirewallService";
	private BlackNumberDao dao;
	private InnerSmsReceiver receiver;
	private TelephonyManager tm;
	private CallStatusBlockListener listener;

	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();//服务启动广播接受者
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
//		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//`
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new CallStatusBlockListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/**
	 * 电话监听者
	 * @author superboy
	 *
	 */
	private class CallStatusBlockListener extends PhoneStateListener {
		long startTime = 0;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
					startTime = System.currentTimeMillis();
					String mode = dao.findMode(incomingNumber);
					if ("0".equals(mode) || "1".equals(mode)) {
						// 挂断电话.
						endCall();
					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:// 电话挂断后的空闲状态.
					if(dao.find(incomingNumber)){
						break;
					}
					long endtime = System.currentTimeMillis();
					long ringingtime = endtime - startTime;
					if (ringingtime < 3000) {
						// 提示用户骚扰电话.
						// notification的方式提示用户.
						showNotification(incomingNumber);
						// TODO:从呼叫记录里面移除信息
						// 观察系统的呼叫记录的信息,如果发现内容变化了.
//					 deleteFromCallLog(incomingNumber);

						getContentResolver().registerContentObserver(
								CallLog.Calls.CONTENT_URI, true,
								new CallLogObserver(new Handler(),incomingNumber));

					}
					break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}

	}
	/**
	 * 通过内容观察者来删除，来电一声响的呼叫记录
	 * @author superboy
	 *
	 */
	private class CallLogObserver extends ContentObserver {
		private String incomingNumber;

		public CallLogObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		// 当观察到内容发生改变的时候 调用的方法.
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteFromCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);
		}

	}
	/**
	 * 短信拦截
	 * @author superboy
	 *
	 */
	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String body = smsMessage.getMessageBody();
				String sender = smsMessage.getOriginatingAddress();
				if (body.contains("fapiao")) {
					Log.i(TAG, "发票短信,拦截");
					abortBroadcast();//短信是个 有序广播 
				}
				String mode = dao.findMode(sender);
				//数据回显 0 全部拦截 1电话拦截 2短信拦截
				if ("0".equals(mode) || "2".equals(mode)) {
					Log.i(TAG, "黑名单号码,拦截");
					abortBroadcast();
				}
			}

		}

	}

	/**
	 *  挂断电话
	 */
	public void endCall() {
		try {
			Method method = Class.forName("android.os.ServiceManager")//通过 反射方式
					.getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null,
					new Object[] { TELEPHONY_SERVICE });
			ITelephony telephony = ITelephony.Stub.asInterface(binder);
//			telephony.answerRingingCall();  //接听 电话 
			telephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 从呼叫记录移除一声响号码
	 *
	 * @param incomingNumber
	 */
	public void deleteFromCallLog(String incomingNumber) {
		Log.i(TAG, "删除呼叫记录:" + incomingNumber);
		Uri uri = Uri.parse("content://call_log/calls");// 得到呼叫记录内容提供者的路径
		Cursor cursor = getContentResolver().query(uri, new String[] { "_id" },
				"number=?", new String[] { incomingNumber }, null);
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			getContentResolver().delete(uri, "_id=?", new String[] { id });
		}
		cursor.close();
	}

	/**
	 * 提示来电一声响
	 *
	 * @param incomingNumber
	 *            电话号码
	 */
	public void showNotification(String incomingNumber) {
		// 1.获取NotificationManager的引用:
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 2. 实例化 Notification:
		Notification notification = new Notification(R.drawable.notification,
				"发现来电响一声号码", System.currentTimeMillis());
		// 3. 配置notification的详细信息
		Context context = getApplicationContext();
		CharSequence contentTitle = "拦截到响一声号码";
		CharSequence contentText = "号码为:" + incomingNumber;
		Intent notificationIntent = new Intent(this, CallSmsSafeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.flags = Notification.FLAG_AUTO_CANCEL;//点击之后会取消
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		// 4. 把notification给显示出来
		nm.notify(0, notification);
	}

}