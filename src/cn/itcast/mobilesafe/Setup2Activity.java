package cn.itcast.mobilesafe;

import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
/**
 * 手机防盗设置向导2
 * @author superboy
 *
 */
public class Setup2Activity extends BaseSetupActivity {
	private ImageView iv_setup2_status;
	@Override
	public void findView() {
		setContentView(R.layout.activity_setup2);
		iv_setup2_status = (ImageView) findViewById(R.id.iv_setup2_status);
	}

	@Override
	public void setupView() {
		if (TextUtils.isEmpty(sp.getString("sim", ""))) {
			iv_setup2_status.setImageResource(R.drawable.unlock);
		}else{
			iv_setup2_status.setImageResource(R.drawable.lock);
		}
	}

	@Override
	public void showNext() {
		openActivity(Setup3Activity.class);
		overridePendingTransition(R.anim.tran_shownext_in,
				R.anim.tran_shownext_out);
	}

	@Override
	public void showPre() {
		openActivity(Setup1Activity.class);
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}

	/**
	 * 绑定sim卡的点击事件
	 */
	public void bind(View view) {
		Editor editor = sp.edit();
		if (TextUtils.isEmpty(sp.getString("sim", ""))) {
			//获取sim卡串号
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			String sim = tm.getSimSerialNumber();//sim卡唯一
			editor.putString("sim", sim);
			editor.commit();
			iv_setup2_status.setImageResource(R.drawable.lock);
		}else{
			editor.putString("sim", "");
			editor.commit();
			iv_setup2_status.setImageResource(R.drawable.unlock);
		}
	}
}