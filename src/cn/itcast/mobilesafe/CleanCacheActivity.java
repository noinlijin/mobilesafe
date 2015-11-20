package cn.itcast.mobilesafe;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.itcast.mobilesafe.utils.MD5Util;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity {
	private ProgressBar pb;
	private TextView tv_status;
	private LinearLayout ll_container;
	private PackageManager pm;

	private Map<String, Long> cacheMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		cacheMap = new HashMap<String, Long>();
		pb = (ProgressBar) findViewById(R.id.pb_clean_cache);
		tv_status = (TextView) findViewById(R.id.tv_clean_cache_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_clean_cache_container);
		pm = getPackageManager();

		// 开启一个后台任务 扫描 当前手机上的缓存信息 .

		new AsyncTask<Void, Object, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
					// 扫描手机上所有的应用程序.
					List<PackageInfo> packinfos = pm.getInstalledPackages(0);
					pb.setMax(packinfos.size());
					int total = 0;
					for (PackageInfo packinfo : packinfos) {
						String packname = packinfo.packageName;
						getCacheInfo(packname);
						total++;
						Thread.sleep(30);
						pb.setProgress(total);
						publishProgress(packinfo);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPreExecute() {
				tv_status.setText("正在初始化八核缓存扫描引擎......");
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				tv_status.setText("扫描完毕!");
				if (cacheMap.size() > 0) {
					Set<Entry<String, Long>> set = cacheMap.entrySet();
					for (Entry<String, Long> entry : set) {
						final String packname = entry.getKey();
						long size = entry.getValue();

						View view = View.inflate(getApplicationContext(),
								R.layout.list_item_cache_item, null);
						ImageView iv_icon = (ImageView) view
								.findViewById(R.id.iv_cache_item_icon);
						TextView tv_name = (TextView) view
								.findViewById(R.id.tv_cache_item_name);
						TextView tv_size = (TextView) view
								.findViewById(R.id.tv_cache_item_size);
						ImageView iv_delete = (ImageView) view
								.findViewById(R.id.iv_cache_item_delete);
						ApplicationInfo appinfo;
						try {
							appinfo = pm.getApplicationInfo(packname, 0);
							tv_name.setText(appinfo.loadLabel(pm));
							iv_icon.setImageDrawable(appinfo.loadIcon(pm));
						} catch (NameNotFoundException e) {
							e.printStackTrace();
							tv_name.setText(packname);
							iv_icon.setImageDrawable(getResources()
									.getDrawable(R.drawable.ic_launcher));
						}

						tv_size.setText("缓存:"
								+ Formatter.formatFileSize(
								getApplicationContext(), size));

						iv_delete.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								System.out.println("清理缓存" + packname);

								deleteCache(packname);

							}

						});

						ll_container.addView(view);
					}

				}
				super.onPostExecute(result);
			}

			@Override
			protected void onProgressUpdate(Object... values) {
				PackageInfo packinfo = (PackageInfo) values[0];
				tv_status.setText("正在扫描:"
						+ packinfo.applicationInfo.loadLabel(pm));
				super.onProgressUpdate(values);
			}

		}.execute();
	}

	public void getCacheInfo(String packname) {
		try {
			Method method = PackageManager.class.getMethod(
					"getPackageSizeInfo", new Class[] { String.class,
							IPackageStatsObserver.class });

			method.invoke(pm,
					new Object[] { packname, new MyObserver(packname) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyObserver extends IPackageStatsObserver.Stub {
		private String packname;

		public MyObserver(String packname) {
			this.packname = packname;
		}

		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cache = pStats.cacheSize;
			if (cache > 0) {
				cacheMap.put(packname, cache);
			}

		}

	}

	private void deleteCache(String packname) {
		// 3 1.5
		// 7 2.1
		// 8 2.2
		// 9 2.3.1
		// 10 2.3.3
		// 15 4.0
		// 16 4.1

		int version = Build.VERSION.SDK_INT;
		if (version >= 9) {
			// 2.3 +
			Intent intent = new Intent();
			intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + packname));
			startActivity(intent);
		} else {
			// 2.2 以下
			Intent intent2 = new Intent();
			intent2.setAction("android.intent.action.VIEW");
			intent2.addCategory("android.intent.category.DEFAULT");
			intent2.addCategory("android.intent.category.VOICE_LAUNCH");
			intent2.putExtra("pkg", packname);
			startActivity(intent2);
		}
		/*
		 * try { Method method =
		 * PackageManager.class.getMethod("deleteApplicationCacheFiles", new
		 * Class[]{String.class,IPackageDataObserver.class}); method.invoke(pm,
		 * new Object[]{packname,new MyPackDataObserver()}); } catch (Exception
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */

	}

	private class MyPackDataObserver extends IPackageDataObserver.Stub {

		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {

		}
	}

}