package cn.itcast.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.mobilesafe.db.dao.AntivirusDao;
import cn.itcast.mobilesafe.utils.MD5Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AntiVirusActivity extends Activity {
	private ImageView iv_scan;
	private ProgressBar pb;
	private TextView tv_scan_status;

	private PackageManager pm;

	private List<PackageInfo> virusInfos;

	private LinearLayout ll_scan_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);

		virusInfos = new ArrayList<PackageInfo>();
		pm = getPackageManager();
		ll_scan_status = (LinearLayout) findViewById(R.id.ll_scan_status);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		pb = (ProgressBar) findViewById(R.id.progressBar1);

		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
				1.0f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);

		new AsyncTask<Void, Object, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
					//比对程序的签名 和 数据库里面的签名是否一致.
					List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES|PackageManager.GET_SIGNATURES);
					pb.setMax(packinfos.size());
					int total = 0;
					for(PackageInfo packinfo : packinfos){
						Signature signature =  packinfo.signatures[0];
						boolean isvirus = false;
						String md5 = MD5Util.encode(signature.toCharsString());
						String result = AntivirusDao.findVirus(md5);
						if(!TextUtils.isEmpty(result)){
							//TODO:记录下来这个病毒信息 通知界面更新内容
							virusInfos.add(packinfo);
							isvirus = true;
						}
						publishProgress("正在扫描:"+ packinfo.applicationInfo.loadLabel(pm),packinfo,isvirus);
						total++;
						pb.setProgress(total);
						Thread.sleep(80);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}


			@Override
			protected void onPreExecute() {
				tv_scan_status.setText("正在初始化杀毒引擎!");
				super.onPreExecute();
			}


			@Override
			protected void onPostExecute(Void result) {
				tv_scan_status.setText("扫描完成...");
				iv_scan.clearAnimation();

				if(virusInfos.size()>0){
					Toast.makeText(getApplicationContext(), "发现病毒.请查杀", 0).show();
					AlertDialog.Builder  builder = new Builder(AntiVirusActivity.this);
					builder.setTitle("警告");
					builder.setMessage("发现病毒是否立刻清理?");
					builder.setPositiveButton("确定", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							for(PackageInfo info : virusInfos){
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_DELETE);
								intent.setData(Uri.parse("package:"+info.packageName));
								startActivity(intent);
							}
						}
					});
					builder.setNegativeButton("取消", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					});
					builder.show();
				}
				super.onPostExecute(result);
			}


			@Override
			protected void onProgressUpdate(Object... values) {
				String text = (String) values[0];
				PackageInfo packinfo = (PackageInfo) values[1];
				boolean isvirus = (Boolean) values[2];
				View view = View.inflate(getApplicationContext(), R.layout.list_scan_item, null);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				ImageView iv_status = (ImageView) view.findViewById(R.id.iv_status);
				tv_name.setText(packinfo.applicationInfo.loadLabel(pm));
				iv_icon.setImageDrawable(packinfo.applicationInfo.loadIcon(pm));
				if(isvirus){
					iv_status.setImageResource(R.drawable.list_icon_risk);
				}
				ll_scan_status.addView(view, 0);
				tv_scan_status.setText(text);
				super.onProgressUpdate(values);
			}

		}.execute();

	}
}