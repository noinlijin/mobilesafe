package cn.itcast.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobilesafe.db.dao.BlackNumberDao;
import cn.itcast.mobilesafe.domain.BlackNumberBean;

public class CallSmsSafeActivityCopy02 extends Activity {
	private ImageView iv_add;
	private ListView lv_call_sms_safe;
	private BlackNumberDao dao;
	private LinearLayout ll_loading;
	private List<BlackNumberBean> blacknumberBeans;

	// 一次最多获取的数据
	private static final int maxNumber = 25;
	protected static final String TAG = "CallSmsSafeActivity";

	private int startIndex = 0;

	private CallSmsAdapter adapter;

	private boolean isloading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		iv_add = (ImageView) findViewById(R.id.iv_call_sms_safe_icon);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);

		lv_call_sms_safe.setOnScrollListener(new OnScrollListener() {

			/**
			 * 静止->滚动 滚动->静止
			 *
			 */
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (isloading) {
					Log.i(TAG,"正在加载数据,不接受新的任务");
					return;
				}

				switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_FLING: // 手指离开后的滑动状态

						break;
					case OnScrollListener.SCROLL_STATE_IDLE:// 静止状态
						// 当listview处于静止状态的时候, 判断一下在当前界面上最后一个条目
						// 是否已经是listview的数据适配器里面的最后一个条目
						int lastItemPosition = lv_call_sms_safe
								.getLastVisiblePosition();// 这个位置是从0开始的.
						int size = adapter.getCount();// 数据适配器里面有多少个条目 数目 从1开始的.
						if (lastItemPosition == (size - 1)) {
							Toast.makeText(getApplicationContext(), "拖动到了最后面", 0)
									.show();
							startIndex += maxNumber;
							fillData();
						}
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 手指在界面的滚动状态.

						break;
				}
			}

			/**
			 * 只要listview处于滚动状态
			 */
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {

			}
		});

		dao = new BlackNumberDao(this);

		fillData();

	}

	private void fillData() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				isloading = true;
				ll_loading.setVisibility(View.VISIBLE);
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
			}

			@Override
			protected Void doInBackground(Void... params) {
				if (blacknumberBeans == null) {
					blacknumberBeans = dao.findPart(startIndex, maxNumber);
				} else {
					List<BlackNumberBean> addBeans = dao.findPart(startIndex,
							maxNumber);
					blacknumberBeans.addAll(addBeans);
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

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

		public View getView(int position, View convertView, ViewGroup parent) {
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
				// 2.进一步优化listview 减少里面控件的查找次数
				view.setTag(holder);
			}

			BlackNumberBean bean = blacknumberBeans.get(position);

			String mode = bean.getMode();
			final String number = bean.getNumber();
			holder.iv.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Log.i(TAG, "删除" + number);

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
	}

}