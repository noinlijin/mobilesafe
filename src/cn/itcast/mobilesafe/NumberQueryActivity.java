package cn.itcast.mobilesafe;


import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobilesafe.db.dao.AddressDao;

public class NumberQueryActivity extends Activity {
	private EditText et_number_query;
	private TextView tv_number_query_address;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_query);
		tv_number_query_address = (TextView) findViewById(R.id.tv_number_query_address);
		et_number_query = (EditText)findViewById(R.id.et_number_query);

		et_number_query.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			public void afterTextChanged(Editable s) {
				String number = s.toString();//当前 窗口里面的内容 
				String address = AddressDao.getAddress(number);
				tv_number_query_address.setText("归属地:"+address);
			}
		});

	}

	public void query(View view){
		String number = et_number_query.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			Toast.makeText(this, "号码不能为空", 0).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number_query.startAnimation(shake);
//			Vibrator vibrotor =	(Vibrator) getSystemService(VIBRATOR_SERVICE);
//			vibrotor.vibrate(new long[]{300,200,800,200}, 10);
			return;
		}
		String address = AddressDao.getAddress(number);
		tv_number_query_address.setText("归属地:"+address);
	}
}