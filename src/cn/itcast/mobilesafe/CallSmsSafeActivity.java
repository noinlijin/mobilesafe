package cn.itcast.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobilesafe.db.dao.BlackNumberDao;
import cn.itcast.mobilesafe.domain.BlackNumberBean;
/**
 * 通讯 卫士 
 * @author superboy
 *
 */
public class CallSmsSafeActivity extends Activity implements OnClickListener {
	private ImageView iv_add;
	private ListView lv_call_sms_safe;
	private BlackNumberDao dao;
	private LinearLayout ll_loading;
	private List<BlackNumberBean> blacknumberBeans;

	// 跳转的页码
	private EditText et_call_sms_pagenumber;

	// 页码状态
	private TextView tv_call_sms_page_status;

	// 一次最多获取的数据
	private static final int maxNumber = 25;
	protected static final String TAG = "CallSmsSafeActivity";

	private int startIndex = 0;

	private CallSmsAdapter adapter;

	private boolean isloading;

	private int currentpage = 1;
	private int totalpage;

	private int selectItemPostion;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		et_call_sms_pagenumber = (EditText) findViewById(R.id.et_call_sms_pagenumber);
		tv_call_sms_page_status = (TextView) findViewById(R.id.tv_call_sms_page_status);

		iv_add = (ImageView) findViewById(R.id.iv_call_sms_safe_icon);
		iv_add.setOnClickListener(this);

		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);

		dao = new BlackNumberDao(this);
		totalpage = dao.getTotalPage(maxNumber);
		tv_call_sms_page_status.setText("当前页/总页码:" + currentpage + "/"
				+ totalpage);
		fillData();
	}

	private void fillData() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				isloading = true;
				ll_loading.setVisibility(View.VISIBLE);//显示加载框
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				ll_loading.setVisibility(View.INVISIBLE);
				if (adapter == null) {
					adapter = new CallSmsAdapter();
					lv_call_sms_safe.setAdapter(adapter);
				} else {
					// 通知数据适配器数据发生了改变.
					adapter.notifyDataSetChanged();
				}
				isloading = false;
				tv_call_sms_page_status.setText("当前页/总页码:" + currentpage + "/"
						+ totalpage);
			}

			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				blacknumberBeans = dao.findPart(startIndex, maxNumber);

				return null;
			}
		}.execute();//必须 调用 这个方法 
	}
	/**
	 * CallSmsAdapter
	 * @author superboy
	 *
	 */
	private class CallSmsAdapter extends BaseAdapter {

		protected static final String TAG = "CallSmsAdapter";

		public int getCount() {
			return blacknumberBeans.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			// Log.i(TAG,"getview:"+position);
			View view;
			// 用来保存那些view对象里面控件的引用.
			ViewHolder holder;
			// 1.复用缓存的view对象 优化listview 减少布局文件->view的操作次数.
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				Log.i(TAG, "复用  old view :" + position);
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_call_sms, null);
				Log.i(TAG, "创建新的 view :" + position);
				holder = new ViewHolder();
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_call_sms_item_mode);
				holder.tv_number = (TextView) view
						.findViewById(R.id.tv_call_sms_item_number);
				holder.iv = (ImageView) view
						.findViewById(R.id.iv_call_sms_item_delete);
				holder.iv_edit = (ImageView)view.findViewById(R.id.iv_call_sms_item_edit);
				// 2.进一步优化listview 减少里面控件的查找次数
				view.setTag(holder);
			}

			BlackNumberBean bean = blacknumberBeans.get(position);

			String mode = bean.getMode();
			final String number = bean.getNumber();
			holder.iv.setOnClickListener(new OnClickListener() {// 删除黑名单 的操作
				public void onClick(View v) {
					Log.i(TAG, "删除" + number);
					dao.delete(number);
					blacknumberBeans.remove(position);
					adapter.notifyDataSetChanged();//通知适配器更新变化 
				}
			});
			holder.iv_edit.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//把在列表中的位置设置进来
					selectItemPostion = position;
					showEditNumberDialog();
				}
			});
			holder.tv_number.setText(number);
			if ("1".equals(mode)) {
				holder.tv_mode.setText("只拦截电话");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("只拦截短信");
			} else {
				holder.tv_mode.setText("电话短信全部拦截");
			}

			return view;
		}

	}

	static class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
		ImageView iv;
		ImageView iv_edit;
	}

	public void jump(View view) {
		String pagenumber = et_call_sms_pagenumber.getText().toString().trim();
		int num = Integer.parseInt(pagenumber);
		if (num <= 0) {
			Toast.makeText(getApplicationContext(), "页码数不合法", 0).show();
			return;
		}
		if (num > totalpage) {
			Toast.makeText(getApplicationContext(), "页码不存在", 0).show();
			return;
		}
		if (num == currentpage) {
			Toast.makeText(getApplicationContext(), "在当前页", 0).show();
			return;
		}
		currentpage = num;
		// 1 页 数据 0~24 (1-1)*maxnumber ~ 1* maxnumber-1
		// 2也 25~49 (2-1)*maxnumber ~ 2* maxnumber-1
		startIndex = (num - 1) * maxNumber;
		fillData();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_call_sms_safe_icon:
				showAddNumberDialog();
				break;

			case R.id.bt_edit_blacknumber_cancle:
				dialog.dismiss();
				break;

			case R.id.bt_edit_blacknumber_ok:

				BlackNumberBean bean = blacknumberBeans.get(selectItemPostion);
				String newmode =null;
				if(cb_edit_blacknumber_phone.isChecked()&&cb_edit_blacknumber_sms.isChecked()){
					newmode = "0";
				}else if(cb_edit_blacknumber_phone.isChecked()){
					newmode = "1";
				}else if(cb_edit_blacknumber_sms.isChecked()){
					newmode = "2";
				}else{
					Toast.makeText(this, "请设置拦截模式", 0).show();
					return;
				}
				dao.update(newmode, bean.getNumber());
				bean.setMode(newmode);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
				break;
			case R.id.bt_add_blacknumber_cancle:
				dialog.dismiss();
				break;
			case R.id.bt_add_blacknumber_ok:
				String number = et_number.getText().toString().trim();
				String mode =null;
				if(cb_add_blacknumber_phone.isChecked()&&cb_add_blacknumber_sms.isChecked()){
					mode = "0";
				}else if(cb_add_blacknumber_phone.isChecked()){
					mode = "1";
				}else if(cb_add_blacknumber_sms.isChecked()){
					mode = "2";
				}else{
					Toast.makeText(this, "请设置拦截模式", 0).show();
					return;
				}
				if(TextUtils.isEmpty(number)){
					Toast.makeText(this, "请设置电话号码", 0).show();
					return ;
				}
				//加到数据库
				dao.add(number, mode);
				currentpage = 1;
				startIndex = 0;
				fillData();
				dialog.dismiss();
				break;
		}

	}

	/**
	 * 显示添加对话框
	 */
	private EditText et_number;
	private Button bt_ok;
	private Button bt_cancle;
	private AlertDialog dialog;
	private CheckBox cb_add_blacknumber_phone;
	private CheckBox cb_add_blacknumber_sms;


	private void showAddNumberDialog() {
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		et_number = (EditText) view
				.findViewById(R.id.et_add_blacknumber_number);
		bt_cancle = (Button) view.findViewById(R.id.bt_add_blacknumber_cancle);
		bt_ok = (Button) view.findViewById(R.id.bt_add_blacknumber_ok);
		cb_add_blacknumber_phone = (CheckBox)view.findViewById(R.id.cb_add_blacknumber_phone);
		cb_add_blacknumber_sms = (CheckBox)view.findViewById(R.id.cb_add_blacknumber_sms);
		bt_cancle.setOnClickListener(this);
		bt_ok.setOnClickListener(this);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}


	private CheckBox cb_edit_blacknumber_phone;
	private CheckBox cb_edit_blacknumber_sms;
	private Button bt_edit_ok;
	private Button bt_edit_cancle;
	private void showEditNumberDialog() {
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_edit_blacknumber, null);
		et_number = (EditText) view
				.findViewById(R.id.et_edit_blacknumber_number);
		BlackNumberBean bean = blacknumberBeans.get(selectItemPostion);
		et_number.setText(bean.getNumber());
		bt_edit_cancle = (Button) view.findViewById(R.id.bt_edit_blacknumber_cancle);
		bt_edit_ok = (Button) view.findViewById(R.id.bt_edit_blacknumber_ok);
		cb_edit_blacknumber_phone = (CheckBox)view.findViewById(R.id.cb_edit_blacknumber_phone);
		cb_edit_blacknumber_sms = (CheckBox)view.findViewById(R.id.cb_edit_blacknumber_sms);

		//数据回显 0 全部拦截 1电话拦截 2短信拦截
		String mode = bean.getMode();
		if("0".equals(mode)){
			cb_edit_blacknumber_phone.setChecked(true);
			cb_edit_blacknumber_sms.setChecked(true);
		}else if("1".equals(mode)){
			cb_edit_blacknumber_phone.setChecked(true);
		}else if("2".equals(mode)){
			cb_edit_blacknumber_sms.setChecked(true);
		}

		bt_edit_ok.setOnClickListener(this);
		bt_edit_cancle.setOnClickListener(this);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
}