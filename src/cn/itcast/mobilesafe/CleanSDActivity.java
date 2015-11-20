package cn.itcast.mobilesafe;

import java.io.File;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

public class CleanSDActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				File sd = Environment.getExternalStorageDirectory();
				File[] files = sd.listFiles();
				for (File file : files) {
					if (file.isDirectory()) {
						String name = file.getName();
						// 查询数据库 是否有这个名称.
						// 添加到待清理的界面上.
					}
				}

				return null;
			}

			@Override
			protected void onPreExecute() {
				Toast.makeText(getApplicationContext(), "开始扫描sd卡", 0).show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(getApplicationContext(), "扫描sd卡完成", 0).show();
				super.onPostExecute(result);
			}

		}.execute();

	}


	//清除文件夹
	private void deleteFile(File file) {
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (File f : fs) {
				deleteFile(f);
			}
		} else {
			file.delete();
		}
		file.delete();
	}

}