package cn.itcast.mobilesafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.itcast.mobilesafe.domain.UpdateInfo;
import cn.itcast.mobilesafe.engine.UpdateInfoParser;
import cn.itcast.mobilesafe.utils.AssetCopyUtil;
import cn.itcast.mobilesafe.utils.DownLoadUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * SplashActivity界面 1、负责连接服务器检查系统版本2、拷贝数据库文件assets-->data/data/cn.itcast.mobliesafe/databases/下
 *
 * @author superboy
 *
 */
public class SplashActivity extends Activity {
	public static final int PARSE_SUCCESS = 10;
	public static final int PARSE_ERROR = 11;
	public static final int SERVER_ERROR = 12;
	public static final int URL_ERROR = 13;
	public static final int NETWORK_ERROR = 14;
	public static final int DOWNLOAD_SUCCESS=15;
	public static final int DOWNLOAD_ERROR=16;
	protected static final String TAG = "SplashActivity";
	public static final int COPY_ADDRESSDB_ERROR = 17;
	public static final int COPY_ADDRESSDB_SUCCESS = 18;
	public static final int COPY_NUMBERDB_ERROR = 19;
	public static final int COPY_NUMBERDB_SUCCESS = 20;
	public static final int COPY_VIRUS_SUCCESS = 21;
	private TextView tv_splash_version;
	private RelativeLayout rl_splash_main;
	private UpdateInfo updateInfo;

	private ProgressDialog pd;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case PARSE_SUCCESS:
					// 判断版本号
					if (getVersion().equals(updateInfo.getVersion())) {
						loadMainUI();
					} else {
						// 显示更新提示对话框
						showUpdateDialog();
					}

					break;

				case PARSE_ERROR:
					showMsg("解析XML失败,....");
					loadMainUI();
					break;
				case SERVER_ERROR:
					showMsg("服务器错误");
					loadMainUI();
					break;
				case URL_ERROR:
					showMsg("url路径不正确");
					loadMainUI();
					break;
				case NETWORK_ERROR:
					showMsg("网络连接错误");
					loadMainUI();
					break;
				case DOWNLOAD_ERROR:
					showMsg("下载文件错误");
					loadMainUI();
					break;
				case DOWNLOAD_SUCCESS:
					//文件安装  系统应用 PackageInstaller清单文件
					File file = (File) msg.obj;
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					startActivity(intent);
					finish();
					break;
				case COPY_ADDRESSDB_ERROR:
					showMsg("初始化电话数据库错误");
					break;
				case COPY_ADDRESSDB_SUCCESS:
					showMsg("初始化电话归属地数据成功");
					break;
				case COPY_NUMBERDB_ERROR:
					showMsg("初始化常用号码数据库错误");
					break;
				case COPY_NUMBERDB_SUCCESS:
					showMsg("初始化常用号码数据成功");
					break;
				case COPY_VIRUS_SUCCESS:
					showMsg("初始化病毒数据库数据成功");
					break;
			}

		}

	};
	private void showMsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, 0)
				.show();
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本:" + getVersion());
		rl_splash_main = (RelativeLayout) findViewById(R.id.rl_splash_main);
		new Thread(new CheckVersionTask()).start();//子线程中连接服务器做费操作

		//root权限
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			e.printStackTrace();
		}


		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);//透明度动画
		aa.setDuration(2000);//持续时间 
		rl_splash_main.startAnimation(aa);

		new Thread(new CopyFileTask()).start();
/*		创建快捷图标
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士快捷图标");
		Intent homeIntent = new Intent();
		homeIntent.setAction("cn.itcast.home");
		homeIntent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, homeIntent);
		
		sendBroadcast(intent);*/
	}
	/**
	 * 拷贝文件 assets-->data/data/cn.itcast.mobliesafe/databases/
	 * @param dbname  要拷贝的文件
	 * @param success  成功提示
	 * @param fail  失败提示
	 */
	private void copyFile(String dbname, int success,int fail){
		//判断一下文件是否已经拷贝到系统的目录下.
		File file = new File(getFilesDir(),dbname);
		if(file.exists()&&file.length()>0){
			//什么事情都不做
		}else{
			//拷贝文件到系统目录.
			File copyedfile = AssetCopyUtil.copy(getApplicationContext(), dbname, file.getAbsolutePath());
			Message msg = Message.obtain();
			if(copyedfile==null){
				msg.what=success;
			}else{
				msg.what=fail;
			}
			handler.sendMessage(msg);
		}
	}

	/**
	 * 拷贝文件到系统目录
	 * @author Administrator
	 *
	 */
	private class CopyFileTask implements Runnable{

		public void run() {
			//判断一下文件是否已经拷贝到系统的目录下.
			copyFile("address.db",COPY_ADDRESSDB_ERROR,COPY_ADDRESSDB_SUCCESS);
			copyFile("commonnum.db",COPY_ADDRESSDB_ERROR,COPY_ADDRESSDB_SUCCESS);
			copyFile("antivirus.db",COPY_ADDRESSDB_ERROR,COPY_VIRUS_SUCCESS);
		}
	}

	/**
	 * 从AndroidManifest.xml文件中获取以下信息
	 android:versionName="1.0"
	 * @return
	 */
	private String getVersion() {

		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);//参数2用不到 塞0
			return packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// todo: can't reach
			return "";
		}

	}

	private class CheckVersionTask implements Runnable {
		public void run() {
			//检查是否开启自动更新
			SharedPreferences sp  = getSharedPreferences("config", MODE_PRIVATE);
			boolean update = sp.getBoolean("update", true);
			if(!update){//不更新
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				loadMainUI();
				return ;
			}

			long startTime = System.currentTimeMillis();
			Message msg = Message.obtain(); //利用旧的 msg提升效率
			// 连接服务器 获取更新信息.
			try {
				URL url = new URL(getResources().getString(R.string.serverurl));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(2000);

				int code = conn.getResponseCode();
				if (code == 200) {
					InputStream is = conn.getInputStream();
					// 解析xml
					updateInfo = UpdateInfoParser.getUpdateInfo(is);
					if (updateInfo != null) {
						// 解析成功.
						msg.what = PARSE_SUCCESS;
					} else {
						// 解析xml失败.
						msg.what = PARSE_ERROR;
					}
				} else {
					msg.what = SERVER_ERROR;
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (NotFoundException e) {
				e.printStackTrace();
				msg.what = URL_ERROR;
			} catch (IOException e) {
				e.printStackTrace();
				msg.what = NETWORK_ERROR;
			} finally {
				long endtime = System.currentTimeMillis();
				long dtime = endtime - startTime;
				if (dtime < 2000) {//缓冲时间在2秒内
					try {
						Thread.sleep(2000 - dtime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				handler.sendMessage(msg);
			}

		}

	}

	/**
	 * 进入应用程序主界面
	 */
	private void loadMainUI() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();//关闭当前activity
	}

	/**
	 * 显示更新提示的对话框
	 */
	protected void showUpdateDialog() {

		Log.i(TAG, "显示更新提示对话框");
		AlertDialog.Builder buidler = new Builder(this);
		buidler.setIcon(R.drawable.notification);
		buidler.setTitle("升级提示");
		buidler.setMessage(updateInfo.getDescription());
		buidler.setOnCancelListener(new OnCancelListener() {//后退键    事件 

			public void onCancel(DialogInterface dialog) {
				loadMainUI();//进入到主界面 
			}
		});
		buidler.setPositiveButton("升级", new OnClickListener() {//点击确定事件 

			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "下载:" + updateInfo.getPath());
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {//判断 外置内存卡状态 

					pd = new ProgressDialog(SplashActivity.this);//内部类多了后，无代码提示
					pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置方向
					pd.show();//显示 出来

					//将下载 文件创建在sd卡根目录
					final File file = new File(Environment.getExternalStorageDirectory(),DownLoadUtil.getFileName(updateInfo.getPath()));
					new Thread() {
						public void run() {
							File downloadfile = DownLoadUtil.downLoad(updateInfo.getPath(),
									file.getAbsolutePath(), pd);
							Message msg = Message.obtain();//子线程中创建 msg
							if(downloadfile!=null){
								//下载成功,安装....
								msg.what= DOWNLOAD_SUCCESS;//what存放 常量 obj存放对象
								msg.obj = downloadfile;
							}else{
								//提示用户下载失败.
								msg.what= DOWNLOAD_ERROR;
							}
							handler.sendMessage(msg);//发送msg
							pd.dismiss();//关闭更新条
						};
					}.start();
				}else{
					showMsg("sd卡不可用");
					loadMainUI();
				}

			}
		});
		buidler.setNegativeButton("取消", new OnClickListener() {//点击取消事件

			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
			}
		});
		// buidler.create().show();
		buidler.show();//必须调用 它的show方法
	}
}