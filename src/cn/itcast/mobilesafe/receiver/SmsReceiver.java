package cn.itcast.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.engine.GPSInfoProvider;
/**
 * 短信拦截器
 * @author superboy
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"短信到来了");

		Object[] objs = (Object[]) intent.getExtras().get("pdus");//拿到系统短信广播接收数据

		for(Object obj: objs){
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			String body = smsMessage.getMessageBody();
			String sender = smsMessage.getOriginatingAddress();

			if("#*location*#".equals(body)){
				Log.i(TAG,"返回手机的位置");
				String address = GPSInfoProvider.getInstance(context).getAddress();
				if(!TextUtils.isEmpty(address)){
					SmsManager manager = SmsManager.getDefault();
					manager.sendTextMessage(sender, null, address, null, null);
				}
				abortBroadcast();//短信广播是个有序广播 
			}else if("#*alarm*#".equals(body)){
				Log.i(TAG,"播放报警音乐");
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);//参数2音乐资源
				player.setVolume(1.0f, 1.0f);//设置左右声音
				player.start();
				abortBroadcast();
			}else if("#*wipedata*#".equals(body)){//DevicePolicyManager需要系统授于管理员权限
				Log.i(TAG,"清除手机数据");
				DevicePolicyManager  dm =	(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dm.wipeData(0);

				abortBroadcast();
			}else if("#*lockscreen*#".equals(body)){
				Log.i(TAG,"锁定手机");
				DevicePolicyManager  dm =	(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dm.resetPassword("321", 0);
				dm.lockNow();

				abortBroadcast();//中止广播
			}
		}
	}

}