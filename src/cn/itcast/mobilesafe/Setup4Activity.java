package cn.itcast.mobilesafe;

import cn.itcast.mobilesafe.receiver.MyAdmin;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
/**
 * 手机防盗设置向导4
 * @author superboy
 *
 */
public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_setup4_status;

	public void findView() {
		setContentView(R.layout.activity_setup4);
		cb_setup4_status = (CheckBox) findViewById(R.id.cb_setup4_status);
	}

	public void setupView() {

		boolean isprotecting = sp.getBoolean("isprotecting", false);
		if(isprotecting){
			cb_setup4_status.setChecked(true);
			cb_setup4_status.setText("防盗保护已经开启");
		}else{
			cb_setup4_status.setChecked(false);
			cb_setup4_status.setText("防盗保护没有开启");
		}
		cb_setup4_status
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
												 boolean isChecked) {//buttonView代表当前checkbox
						Editor editor = sp.edit();
						if(isChecked){
							editor.putBoolean("isprotecting", true);
							cb_setup4_status.setText("防盗保护已经开启");
						}else{
							editor.putBoolean("isprotecting", false);
							cb_setup4_status.setText("防盗保护没有开启");
						}
						editor.commit();
					}
				});
	}

	public void showNext() {
		openActivity(LostFindActivity.class);
		overridePendingTransition(R.anim.tran_shownext_in,
				R.anim.tran_shownext_out);

		Editor editor = sp.edit();
		editor.putBoolean("setup", true);
		editor.commit();
	}

	public void showPre() {
		openActivity(Setup3Activity.class);
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}
	/**
	 * 开启应用程序的超级管理员权限
	 * @param view
	 */
	public void activeDeviceAdmin(View view){
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);
		startActivity(intent);
	}
}