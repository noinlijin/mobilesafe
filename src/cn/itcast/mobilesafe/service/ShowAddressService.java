package cn.itcast.mobilesafe.service;

import cn.itcast.mobilesafe.R;
import cn.itcast.mobilesafe.db.dao.AddressDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
/**
 * 归属地提示服务 
 * @author superboy
 *
 */
public class ShowAddressService extends Service {

	public static final String TAG = "ShowAddressService";
	private TelephonyManager tm;
	private MyPhoneStatusListener listener;
	private OutCallReceiverInService receiver;
	private WindowManager wm;
	private View view;
	// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
	private static final int[] bgs = { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green };

	private SharedPreferences sp;
	/**
	 * 服务中创建广播接受者(不用在清单文件里面配置 )
	 * @author superboy
	 *
	 */
	private class OutCallReceiverInService extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			Log.i(TAG, "内部的广播接收者");
			if (number != null) {
				String address = AddressDao.getAddress(number);
				// Toast.makeText(context, address, 1).show();
				showAddress(address);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);

		// 在服务创建的时候 采用代码的方式 注册一个广播接受者.
		// 广播接收者的作用周期 就跟 服务一致的
		receiver = new OutCallReceiverInService();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");//监听外拨电话的动作
		this.registerReceiver(receiver, filter);//注册广播接受者

		// 注册一个电话状态的监听器.
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStatusListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//监听电话状态 

		//PhoneStateListener.LISTEN_DATA_ACTIVITY    流量 监听 
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);//取消监听 
		listener = null;
		this.unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	private class MyPhoneStatusListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:// 手机处于空闲状态.
					if (view != null) {
						wm.removeView(view);//移除模仿土司
						view = null;
					}
					break;

				case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
					String address = AddressDao.getAddress(incomingNumber);
					// Toast.makeText(getApplicationContext(), address, 1).show();
					showAddress(address);

					break;

				case TelephonyManager.CALL_STATE_OFFHOOK:// 接通电话的状态

					break;
			}

		}
	}

	/**
	 * 模仿土司 在手机的窗体上 显示一个自定义的view对象.
	 *
	 * @param address 手机归属地
	 */
	public void showAddress(String address) {
		view = View.inflate(this, R.layout.toast_address, null);
		// 根据设置 修改一下背景颜色.
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(bgs[which]);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_address);
		tv.setText(address);

		//参考系统代码 
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.gravity = Gravity.LEFT | Gravity.TOP;// 与左上角对其

		//设置后的参数
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//需要指定 

		//控制控件移动
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {

					case MotionEvent.ACTION_DOWN:
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;

					case MotionEvent.ACTION_MOVE:
						int x = (int) event.getRawX();
						int y = (int) event.getRawY();
						int dx = x - startX;
						int dy = y - startY;
						params.x += dx;
						params.y += dy;

						wm.updateViewLayout(view, params);//更新view
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
				}

				return true;
			}
		});

		wm.addView(view, params);

	}
}