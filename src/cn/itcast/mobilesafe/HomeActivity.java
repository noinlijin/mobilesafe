package cn.itcast.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import cn.itcast.mobilesafe.adapter.HomeAdapter;
import cn.itcast.mobilesafe.utils.MD5Util;
/**
 * HomeActivity 首页面  GridView 塞数据 并添加点击事件
 * @author superboy
 *
 */
public class HomeActivity extends Activity implements OnClickListener {
	private GridView gv_main;
	private SharedPreferences sp;

	//第一次进入对话框组件的初始化
	private EditText et_first_pwd;
	private EditText et_first_pwd_confirm;
	private Button bt_first_ok;
	private Button bt_first_cancle;


	//第二次进入对话框组件的初始化
	private EditText et_normal_pwd;
	private Button bt_normal_ok;
	private Button bt_normal_cancle;

	private AlertDialog dialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gv_main = (GridView) findViewById(R.id.gv_main);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_main.setAdapter(new HomeAdapter(this));


		gv_main.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent;
				switch (position) {
					case 0:
						loadLostProtectedUI();//手机防盗

						break;
					case 1:
						intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
						startActivity(intent);

						break;
					case 2:
						intent = new Intent(HomeActivity.this,AppManagerActivity2.class);
						startActivity(intent);

						break;
					case 3:
						intent = new Intent(HomeActivity.this,TaskManagerActivity.class);
						startActivity(intent);

						break;
					case 4:
						intent = new Intent(HomeActivity.this,TrafficManagerActivity.class);
						startActivity(intent);

						break;
					case 5:
						intent = new Intent(HomeActivity.this,AntiVirusActivity.class);
						startActivity(intent);

						break;
					case 6:
						intent = new Intent(HomeActivity.this,SystemOptActivity.class);
						startActivity(intent);

						break;
					case 7:
						intent = new Intent(HomeActivity.this,AtoolsActivity.class);
						startActivity(intent);
						break;
					case 8://设置中心
						intent = new Intent(HomeActivity.this,SettingCenterActivity.class);
						startActivity(intent);
						break;


				}


			}
		});
	}
	/**
	 * 进入手机防盗的UI
	 */
	protected void loadLostProtectedUI() {
		//判断用户是否设置过密码.
		if(isSetupPWD()){
			//正常进入的对话框
			showNormalEntryDialog();
		}else{
			//设置密码对话框
			showFirstEntryDialog();
		}

	}

	/**
	 * 检查是否设置过密码
	 * @return
	 */
	private boolean isSetupPWD(){
		String savedPwd = sp.getString("password", "");
		if(TextUtils.isEmpty(savedPwd)){
			return false;
		}else{
			return true;
		}
	}


	/**
	 * 第一次进入对话框
	 */
	private void showFirstEntryDialog() {
		AlertDialog.Builder builder = new Builder(this);

		View view = View.inflate(this, R.layout.dialog_first_entry, null);
		et_first_pwd = (EditText) view.findViewById(R.id.et_first_pwd);
		et_first_pwd_confirm = (EditText) view.findViewById(R.id.et_first_pwd_confirm);
		bt_first_ok = (Button)view.findViewById(R.id.bt_first_ok);
		bt_first_cancle = (Button)view.findViewById(R.id.bt_first_cancle);

		bt_first_ok.setOnClickListener(this);
		bt_first_cancle.setOnClickListener(this);

		dialog = builder.create();//消除对话框的黑边
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
	/**
	 * 正常进入对话框
	 */
	private void showNormalEntryDialog() {
		AlertDialog.Builder builder = new Builder(this);

		View view = View.inflate(this, R.layout.dialog_normal_entry, null);
		et_normal_pwd = (EditText) view.findViewById(R.id.et_normal_pwd);
		bt_normal_ok = (Button)view.findViewById(R.id.bt_normal_ok);
		bt_normal_cancle = (Button)view.findViewById(R.id.bt_normal_cancle);

		bt_normal_ok.setOnClickListener(this);
		bt_normal_cancle.setOnClickListener(this);

		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);//去掉黑色边框
		dialog.show();

	}
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_first_cancle:
				dialog.dismiss();
				break;

			case R.id.bt_first_ok:
				String pwd = et_first_pwd.getText().toString().trim();
				String pwd_confirm = et_first_pwd_confirm.getText().toString().trim();

				if(TextUtils.isEmpty(pwd)||TextUtils.isEmpty(pwd_confirm)){
					Toast.makeText(this, "密码不能为空", 0).show();//LENGTH_SHORT = 0; LENGTH_LONG = 1;
					return ;
				}
				if(pwd.equals(pwd_confirm)){
					Editor editor = sp.edit();
					editor.putString("password", MD5Util.encode(pwd));
					editor.commit();
					dialog.dismiss();//关闭对话框
				}else {
					Toast.makeText(this, "两次密码不一致", 0).show();
					return;
				}
				break;
			case R.id.bt_normal_cancle:
				dialog.dismiss();
				break;
			case R.id.bt_normal_ok:
				String enterdpwd = et_normal_pwd.getText().toString().trim();

				if(TextUtils.isEmpty(enterdpwd)){
					Toast.makeText(this, "密码不能为空", 0).show();
					return;
				}
				String savedpwd = sp.getString("password", "");
				if((MD5Util.encode(enterdpwd)).equals(savedpwd)){
					dialog.dismiss();
					//进入界面
					Intent intent = new Intent(this,LostFindActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(this, "密码不正确", 0).show();
					return;
				}
				break;
		}

	}
}