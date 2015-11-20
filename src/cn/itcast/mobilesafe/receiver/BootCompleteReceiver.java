package cn.itcast.mobilesafe.receiver;

import cn.itcast.mobilesafe.HomeActivity;
import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.service.CallSmsFirewallService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
/**
 * 订阅开机广播
 * @author superboy
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "手机开机了.");


		//1. 
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification no = new Notification(R.drawable.notification, "神马护卫护卫您", System.currentTimeMillis());
		no.flags = Notification.FLAG_NO_CLEAR;//设置后，消息点击后 不会消失
		Intent homeIntent = new Intent(context,HomeActivity.class);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//非activity中启动另一个activity必须要加
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, homeIntent, 0);
		no.setLatestEventInfo(context, "我是神马护卫", "护卫您的手机", contentIntent);

		nm.notify(0, no);

		/**
		 * 开机启动来电防火墙服务
		 */
		Intent callIntent = new Intent(context,CallSmsFirewallService.class);
		context.startService(callIntent);



		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String currentSim = tm.getSimSerialNumber();

		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String bindSim = sp.getString("sim", "");

		if (!bindSim.equals(currentSim)) {
			// 手机的sim卡被更换了.
			// 判断手机是否是处于防盗保护的状态.
			boolean isprotecting = sp.getBoolean("isprotecting", false);
			if (isprotecting) {
				//发送报警短信.
				SmsManager smsManager =SmsManager.getDefault();
				String safenumber = sp.getString("safenumber", "");//可采取base64加密后解密
				smsManager.sendTextMessage(safenumber, null, "sim changed", null, null);
			}
		}
	}

}