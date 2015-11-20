package cn.itcast.mobilesafe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Text;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.mobilesafe.domain.AppInfo;
import cn.itcast.mobilesafe.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private ListView lv_appmanger;
	private View ll_loading;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private AppManagerAdapter adapter;

	private TextView tv_app_manager_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ll_loading = findViewById(R.id.ll_loading);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_appmanger = (ListView) findViewById(R.id.lv_appmanger);
		tv_avail_rom.setText("可用内存:" + getAvailRom());
		tv_avail_sd.setText("可用SD卡:" + getAvailSD());
		tv_app_manager_status = (TextView) findViewById(R.id.tv_app_manager_status);
		fillData();

		lv_appmanger.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			/**
			 *
			 * @param view
			 * @param firstVisibleItem第一个用户可见的条目的位置
			 *            .
			 * @param visibleItemCount
			 * @param totalItemCount
			 */
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				int position = lv_appmanger.getFirstVisiblePosition();
				if (userAppInfos != null&& systemAppInfos!=null) {
					if (position < userAppInfos.size()) {
						tv_app_manager_status.setText("用户程序("
								+ userAppInfos.size() + "个)");
					} else {
						tv_app_manager_status.setText("系统程序("
								+ systemAppInfos.size() + "个)");
					}
				}
			}
		});

	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				List<AppInfo> appinfos = AppInfoProvider
						.getAppInfos(getApplicationContext());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appinfos) {
					if (info.isUserapp()) {
						userAppInfos.add(info);
					} else {
						systemAppInfos.add(info);
					}
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				ll_loading.setVisibility(View.INVISIBLE);
				adapter = new AppManagerAdapter();
				lv_appmanger.setAdapter(adapter);
				super.onPostExecute(result);
			}

		}.execute();

	}

	private class AppManagerAdapter extends BaseAdapter {

		public int getCount() {
			return userAppInfos.size() + systemAppInfos.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_app_item, null);
				holder.iv = (ImageView) view
						.findViewById(R.id.iv_app_item_icon);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_item_location);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_app_item_name);
				holder.tv_version = (TextView) view
						.findViewById(R.id.tv_app_item_version);
				view.setTag(holder);

			}
			AppInfo appinfo;
			if (position < userAppInfos.size()) {
				appinfo = userAppInfos.get(position);
			} else {
				int newposition = position - userAppInfos.size();
				appinfo = systemAppInfos.get(newposition);
			}

			holder.iv.setImageDrawable(appinfo.getIcon());
			if (appinfo.isInrom()) {
				holder.tv_location.setText("手机内存");
			} else {
				holder.tv_location.setText("SD卡");
			}
			holder.tv_name.setText(appinfo.getName());
			holder.tv_version.setText(appinfo.getVersion());
			return view;
		}

	}

	static class ViewHolder {
		ImageView iv;
		TextView tv_name;
		TextView tv_location;
		TextView tv_version;
	}

	/**
	 * 获取手机sd卡可用的空间
	 *
	 * @return
	 */
	private String getAvailSD() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize(); // 获取到每一块空间存储数据的大小
		long availableBlocks = stat.getAvailableBlocks();// 得到可用的sd空间的个数

		long size = blockSize * availableBlocks;
		return Formatter.formatFileSize(this, size);
	}

	/**
	 * 获取手机sd卡可用的空间
	 *
	 * @return
	 */
	private String getAvailRom() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long size = blockSize * availableBlocks;
		return Formatter.formatFileSize(this, size);
	}
}