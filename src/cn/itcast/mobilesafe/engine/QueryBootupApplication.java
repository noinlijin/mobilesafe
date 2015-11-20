package cn.itcast.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import cn.itcast.mobilesafe.domain.AppInfo;

public class QueryBootupApplication {

	/**
	 * 获取所有具有开机启动能力的应用程序
	 *
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getBootupApps(Context context) {
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.BOOT_COMPLETED");

		List<ResolveInfo> infos = pm.queryBroadcastReceivers(intent,
				PackageManager.GET_INTENT_FILTERS);

		for(ResolveInfo info : infos){
			AppInfo appinfo = new AppInfo();
			appinfo.setReceivername(info.activityInfo.name);

			appinfo.setIcon(info.activityInfo.applicationInfo.loadIcon(pm));
			appinfo.setName(info.activityInfo.applicationInfo.loadLabel(pm).toString());
			appinfo.setPackname(info.activityInfo.packageName);
			if((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM )!=1){
				appinfo.setUserapp(true);
			}else{
				appinfo.setUserapp(false);
			}
			appinfos.add(appinfo);
		}
		/*
		 * Intent intent = new Intent();
		 * intent.setAction("android.intent.action.MAIN");
		 * intent.addCategory("android.intent.category.LAUNCHER");
		 * List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		 * PackageManager.GET_INTENT_FILTERS); System.out.println(infos.size());
		 */
		return appinfos;

	}
}