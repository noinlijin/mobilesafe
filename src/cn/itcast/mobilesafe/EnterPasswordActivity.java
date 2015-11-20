package cn.itcast.mobilesafe;

import cn.itcast.mobilesafe.service.WatchDogService2;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 程序锁，输入 密码界面 
 * @author superboy
 *
 */
public class EnterPasswordActivity extends Activity {
	private EditText et_enterpwd;
	private ImageView iv_enterpwd_icon;
	private TextView tv_enterpwd_name;
	private String packname;

	private Intent watchDogIntent;
	private IService iService;
	private MyConn conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_password);
		et_enterpwd = (EditText) findViewById(R.id.et_enterpwd);
		iv_enterpwd_icon = (ImageView) findViewById(R.id.iv_enterpwd_icon);
		tv_enterpwd_name = (TextView) findViewById(R.id.tv_enterpwd_name);

		watchDogIntent = new Intent(this,WatchDogService2.class);
		conn = new MyConn();
		bindService(watchDogIntent, conn, BIND_AUTO_CREATE);//activity中绑定 service,BIND_AUTO_CREATE服务不存在则自动 创建

		//获取传递过来的参数
		Intent intent = getIntent();//获取跳转来的bean封装的数据
		packname = intent.getStringExtra("packname");

		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);//暂时无用，塞个0
			Drawable icon = info.loadIcon(pm);
			CharSequence name = info.loadLabel(pm);
			iv_enterpwd_icon.setImageDrawable(icon);
			tv_enterpwd_name.setText(name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void enter (View view){
		String password = et_enterpwd.getText().toString().trim();
		if("123".equals(password)){//对应程序加对应 密码，或者 统一密码
			//密码输入正确.
		/*	Intent intent = new Intent();
			intent.setAction("cn.itcast.stop");//发送一个自定义的广播事件.
			intent.putExtra("stopedname", packname);
			sendBroadcast(intent);*/
			//采用调用服务里面的方法. 通知服务临时的停止保护.
			iService.callTempStopProtect(packname);
			finish();
		}else{
			Toast.makeText(this, "密码不正确", 0).show();
		}

	}



	private class MyConn implements ServiceConnection{

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("Binder","服务被成功绑定了.");
			iService = (IService) service;//远程代理对象的初始化
		}

		public void onServiceDisconnected(ComponentName name) {

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//判断当前按键是否是后退键
		if(keyCode==KeyEvent.KEYCODE_BACK){
			//回桌面的操作，激活桌面的操作
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			startActivity(intent);
			finish();//关闭输入密码的界面.
			return true;//返回true以阻止这一事件的进一步传播，或假来表明你没有处理这个事件，它应该继续传播。
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		unbindService(conn);//服务销毁的时候 解绑服务
		super.onDestroy();
	}
}