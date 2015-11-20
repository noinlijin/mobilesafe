package cn.itcast.mobilesafe;

import cn.itcast.mobilesafe.service.CallSmsFirewallService;
import cn.itcast.mobilesafe.service.ShowAddressService;
import cn.itcast.mobilesafe.service.WatchDogService2;
import cn.itcast.mobilesafe.ui.SettingView;
import cn.itcast.mobilesafe.utils.ServiceStatusUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 设置中心
 * @author superboy
 *
 */
public class SettingCenterActivity extends Activity implements OnClickListener {
	private SettingView sv_setting_update;
	private SharedPreferences sp;


	//号码归属地显示
	private SettingView sv_setting_showaddress;
	private Intent showAddressServiceIntent;

	//短信电话黑名单
	private SettingView sv_setting_call_sms_safe;
	private Intent callSmsSafeIntent;

	//程序锁 sv_setting_app_lock
	private SettingView sv_setting_app_lock;
	private Intent appLockIntent;



	//归属地显示的风格
	private RelativeLayout rl_setting_showaddress_bg;
	private TextView tv_setting_showaddress_bg;


	private static final String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};

	//归属地显示位置
	private RelativeLayout rl_setting_showaddress_location;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_setting_center);

		//自动更新设置
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		sv_setting_update.setOnClickListener(this);
		boolean update = sp.getBoolean("update", true);//数据下次开启回显
		if(update){
			sv_setting_update.setContent("自动更新已经开启");
			sv_setting_update.setChecked(true);
		}else{
			sv_setting_update.setContent("自动更新没有开启");
			sv_setting_update.setChecked(false);
		}

		//归属地提示服务显示的设置初始化
		sv_setting_showaddress = (SettingView)findViewById(R.id.sv_setting_showaddress);
		showAddressServiceIntent = new Intent(this,ShowAddressService.class);
		sv_setting_showaddress.setOnClickListener(this);

		//程序锁设置初始化
		sv_setting_app_lock = (SettingView)findViewById(R.id.sv_setting_app_lock);
		appLockIntent = new Intent(this,WatchDogService2.class);
		sv_setting_app_lock.setOnClickListener(this);



		//黑名单拦截初始化

		sv_setting_call_sms_safe = (SettingView)findViewById(R.id.sv_setting_call_sms_safe);
		callSmsSafeIntent = new Intent(this,CallSmsFirewallService.class);
		sv_setting_call_sms_safe.setOnClickListener(this);


		//归属地风格初始化
		tv_setting_showaddress_bg = (TextView)findViewById(R.id.tv_setting_showaddress_bg);
		rl_setting_showaddress_bg =(RelativeLayout)findViewById(R.id.rl_setting_showaddress_bg);
		rl_setting_showaddress_bg.setOnClickListener(this);
		int which = sp.getInt("which", 0);
		tv_setting_showaddress_bg.setText(items[which]);

		rl_setting_showaddress_location = (RelativeLayout) findViewById(R.id.rl_setting_showaddress_location);
		rl_setting_showaddress_location.setOnClickListener(this);
	}


	protected void onResume() {
		if(ServiceStatusUtil.isServiceRunning(this, "cn.itcast.mobilesafe.service.ShowAddressService")){
			sv_setting_showaddress.setChecked(true);
			sv_setting_showaddress.setContent("号码归属地服务已经开启");
		}else{
			sv_setting_showaddress.setChecked(false);
			sv_setting_showaddress.setContent("号码归属地服务没有开启");
		}

		if(ServiceStatusUtil.isServiceRunning(this, "cn.itcast.mobilesafe.service.CallSmsFirewallService")){
			sv_setting_call_sms_safe.setChecked(true);
			sv_setting_call_sms_safe.setContent("黑名单拦截服务已经开启");
		}else{
			sv_setting_call_sms_safe.setChecked(false);
			sv_setting_call_sms_safe.setContent("黑名单拦截服务没有开启");
		}


		if(ServiceStatusUtil.isServiceRunning(this, "cn.itcast.mobilesafe.service.WatchDogService2")){
			sv_setting_app_lock.setChecked(true);
			sv_setting_app_lock.setContent("程序锁服务已经开启");
		}else{
			sv_setting_app_lock.setChecked(false);
			sv_setting_app_lock.setContent("程序锁服务没有开启");
		}
		super.onResume();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			//自动更新条目的点击事件
			case R.id.sv_setting_update:
				Editor editor = sp.edit();//获取editor
				if(sv_setting_update.isChecked()){
					sv_setting_update.setChecked(false);
					sv_setting_update.setContent("自动更新没有开启");
					editor.putBoolean("update", false);
				}else{
					sv_setting_update.setChecked(true);
					sv_setting_update.setContent("自动更新已经开启");
					editor.putBoolean("update", true);
				}
				editor.commit();//提交数据
				break;
			case R.id.sv_setting_call_sms_safe:
				if(sv_setting_call_sms_safe.isChecked()){
					sv_setting_call_sms_safe.setChecked(false);
					sv_setting_call_sms_safe.setContent("黑名单拦截服务没有开启");
					stopService(callSmsSafeIntent);
				}else{
					sv_setting_call_sms_safe.setChecked(true);
					sv_setting_call_sms_safe.setContent("黑名单拦截服务已经开启");
					startService(callSmsSafeIntent);
				}
				break;
			case R.id.sv_setting_app_lock:
				if(sv_setting_app_lock.isChecked()){
					sv_setting_app_lock.setChecked(false);
					sv_setting_app_lock.setContent("程序锁服务没有开启");
					stopService(appLockIntent);
				}else{
					sv_setting_app_lock.setChecked(true);
					sv_setting_app_lock.setContent("程序锁服务已经开启");
					startService(appLockIntent);
				}
				break;
			case R.id.sv_setting_showaddress:
				if(sv_setting_showaddress.isChecked()){
					sv_setting_showaddress.setChecked(false);
					sv_setting_showaddress.setContent("号码归属地服务没有开启");
					stopService(showAddressServiceIntent);
				}else{
					sv_setting_showaddress.setChecked(true);
					sv_setting_showaddress.setContent("号码归属地服务已经开启");
					startService(showAddressServiceIntent);
				}
				break;
			case R.id.rl_setting_showaddress_bg:
				showChangeBgDialog();
				break;
			case R.id.rl_setting_showaddress_location:
				Intent intent = new Intent(this,DragViewActivity.class);
				startActivity(intent);
				break;
		}

	}

	/**
	 * 显示更改背景的对话框
	 */
	private void showChangeBgDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.notification);
		builder.setTitle("更改归属地提示风格");
		int which = sp.getInt("which", 0);
		builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Editor editor = sp.edit();
				editor.putInt("which", which);
				editor.commit();
				dialog.dismiss();
				tv_setting_showaddress_bg.setText(items[which]);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}


}



