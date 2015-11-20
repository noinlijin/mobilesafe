package cn.itcast.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AtoolsActivity extends Activity implements OnClickListener {
	private LinearLayout ll_atools_numberquery;

	private LinearLayout ll_atools_commonnumquery;

	private LinearLayout ll_atools_sms_backup;
	private LinearLayout ll_atools_sms_restore;
	String s;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		ll_atools_numberquery = (LinearLayout) findViewById(R.id.ll_atools_numberquery);
		ll_atools_numberquery.setOnClickListener(this);
//		s.equals("haha");  //异常
		ll_atools_commonnumquery = (LinearLayout) findViewById(R.id.ll_atools_commonnumquery);
		ll_atools_commonnumquery.setOnClickListener(this);

		ll_atools_sms_backup = (LinearLayout) findViewById(R.id.ll_atools_sms_backup);
		ll_atools_sms_backup.setOnClickListener(this);
//常用 号码查询
		ll_atools_sms_restore = (LinearLayout) findViewById(R.id.ll_atools_sms_restore);
		ll_atools_sms_restore.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.ll_atools_numberquery:
				// 定向到号码归属地查询的界面.
				intent = new Intent(this, NumberQueryActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_atools_commonnumquery:
				// 定向到号码归属地查询的界面.
				intent = new Intent(this, CommnumActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_atools_sms_backup:
				backUpSmsTask();

				break;
			case R.id.ll_atools_sms_restore:

				break;
			default:
				break;
		}

	}

	private void backUpSmsTask() {
		new AsyncTask<Void, Void, Void>() {
			ProgressDialog pd;

			@Override
			protected Void doInBackground(Void... params) {
				// 读取短信的所有的内容,备份到SD的文件上.
				try {
					Uri uri = Uri.parse("content://sms/");
					Cursor cursor = getContentResolver().query(uri,
							new String[] { "address", "date", "type", "body" },
							null, null, null);
					int max = cursor.getCount();
					pd.setMax(max);
					File file = new File(
							Environment.getExternalStorageDirectory(),
							"smsbaup.xml");
					FileOutputStream fos = new FileOutputStream(file);
					XmlSerializer serializer = Xml.newSerializer();
					serializer.setOutput(fos, "utf-8");
					serializer.startDocument("utf-8", true);
					serializer.startTag(null, "smss");
					int total = 0;
					while (cursor.moveToNext()) {
						serializer.startTag(null, "sms");

						serializer.startTag(null, "address");
						serializer.text(cursor.getString(0));
						serializer.endTag(null, "address");

						serializer.startTag(null, "date ");
						serializer.text(cursor.getString(1));
						serializer.endTag(null, "date");


						serializer.startTag(null, "type");
						serializer.text(cursor.getString(2));
						serializer.endTag(null, "type");


						serializer.startTag(null, "body");
						serializer.text(new String(Base64.encode(cursor.getString(3).getBytes(), 0)));
						serializer.endTag(null, "body");

						serializer.endTag(null, "sms");
						total ++;
						pd.setProgress(total);
					}
					serializer.endTag(null, "smss");
					cursor.close();
					serializer.endDocument();
					fos.flush();
					fos.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(AtoolsActivity.this);
				pd.setTitle("提示");
				pd.setMessage("正在备份短信");
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				pd.dismiss();
				Toast.makeText(getApplicationContext(), "备份完毕", 0).show();
				super.onPostExecute(result);
			}

		}.execute();
	}

}