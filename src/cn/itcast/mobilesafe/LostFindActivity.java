package cn.itcast.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 手机防盗界面
 * @author superboy
 *
 */
public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_lost_find);
		//安全号码设置
		tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
		//防盗保护设置状态
		iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);

		if(isSetup()){
			//显示详细配置界面
			tv_lostfind_number.setText(sp.getString("safenumber", ""));
			boolean isprotecting = sp.getBoolean("isprotecting", false);
			if(isprotecting){
				iv_lostfind_status.setImageResource(R.drawable.lock);
			}else{
				iv_lostfind_status.setImageResource(R.drawable.unlock);
			}
		}else{
			//定向页面到设置向导界面.
			loadSetupUI();
		}
	}



	private void loadSetupUI() {
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
	}



	/**
	 * 判断用户是否进行过设置向导
	 * @return
	 */
	private boolean isSetup(){
		return sp.getBoolean("setup", false);
	}
	/**
	 * 重新设置
	 * @param view
	 */
	public void reEntrySetup(View view){
		loadSetupUI();
	}
}