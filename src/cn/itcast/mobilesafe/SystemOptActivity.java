package cn.itcast.mobilesafe;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class SystemOptActivity extends TabActivity {
	private TabHost mTabHost;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_opt);
		//1.获取当前界面里面的tabhost
		mTabHost = getTabHost();

		//2.填充tabhost的数据
		// 准备好每个小的选项卡标签.
		TabSpec tab1 = mTabHost.newTabSpec("缓存清理");

		tab1.setIndicator(getIndicatorView(R.drawable.tab1, "缓存清理"));
		Intent intent1 = new Intent(this,CleanCacheActivity.class);
		tab1.setContent(intent1);

		TabSpec tab2 = mTabHost.newTabSpec("SD卡清理");
		tab2.setIndicator(getIndicatorView(R.drawable.tab2, "SD卡清理"));
		Intent intent2 = new Intent(this,CleanSDActivity.class);
		tab2.setContent(intent2);


		TabSpec tab3 = mTabHost.newTabSpec("启动加速");
		tab3.setIndicator(getIndicatorView(R.drawable.tab3, "启动加速"));
		Intent intent3 = new Intent(this,CleanStartupActivity.class);
		tab3.setContent(intent3);

		mTabHost.addTab(tab1);
		mTabHost.addTab(tab2);
		mTabHost.addTab(tab3);


	}

	public View getIndicatorView(int icon,String text){
		View view = View.inflate(getApplicationContext(), R.layout.system_opt_item, null);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_opt_item);
		TextView tv = (TextView) view.findViewById(R.id.tv_opt_item);
		iv.setImageResource(icon);
		tv.setText(text);
		return view;

	}
}