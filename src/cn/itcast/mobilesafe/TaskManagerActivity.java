package cn.itcast.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itcast.mobilesafe.domain.TaskInfo;
import cn.itcast.mobilesafe.engine.TaskInfoProvider;
import cn.itcast.mobilesafe.utils.MyToast;
import cn.itcast.mobilesafe.utils.TaskUtils;

public class TaskManagerActivity extends Activity {
	private TextView tv_task_count;
	private TextView tv_task_mem;
	//系统可用的内存大小
	private long availmem;
	//系统的总内存
	private long totalmem;
	private int processcount;

	private LinearLayout ll_loading;
	private ListView lv_task_manager;

	private List<TaskInfo> taskinfos;

	private TaskAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_task_mem = (TextView) findViewById(R.id.tv_task_mem);
		tv_task_count = (TextView) findViewById(R.id.tv_task_count);
		availmem = TaskUtils.getAvailMem(this);
		totalmem = TaskUtils.getTotalMem();

		//格式化大小	
		tv_task_mem.setText("剩余/总内存:"+Formatter.formatFileSize(this, availmem)+"/"+Formatter.formatFileSize(this, totalmem));
		processcount = TaskUtils.getRunningProcessCount(this);
		tv_task_count.setText("正在运行:"+ processcount+"个");
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);

		fillData();

		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				TaskInfo taskinfo = taskinfos.get(position);
				if(getPackageName().equals(taskinfo.getPackname())){
					return ;
				}
				CheckBox cb = (CheckBox) view.findViewById(R.id.cb_task_item_status);
				if(taskinfo.isChecked()){
					taskinfo.setChecked(false);
					cb.setChecked(false);
				}else{
					taskinfo.setChecked(true);
					cb.setChecked(true);
				}

			}
		});
	}


	private void fillData() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				taskinfos = TaskInfoProvider.getTaskInfos(getApplicationContext());

				return null;
			}

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				ll_loading.setVisibility(View.INVISIBLE);
				//TODO:
				adapter = new TaskAdapter();
				lv_task_manager.setAdapter(adapter);
			}



		}.execute();


	}

	private class TaskAdapter extends BaseAdapter{

		public int getCount() {
			return taskinfos.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			View view ;
			ViewHolder holder;
			if(convertView!=null&& convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(), R.layout.list_item_task_item, null);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_task_item_name);
				holder.tv_mem = (TextView) view.findViewById(R.id.tv_task_item_mem);
				holder.cb = (CheckBox) view.findViewById(R.id.cb_task_item_status);
				holder.iv = (ImageView) view.findViewById(R.id.iv_task_item_icon);
				view.setTag(holder);

			}
			TaskInfo info = taskinfos.get(position);
			holder.iv.setImageDrawable(info.getIcon());
			holder.tv_name .setText(info.getName());
			holder.tv_mem.setText("内存占用:"+Formatter.formatFileSize(getApplicationContext(), info.getMemsize()));
			holder.cb.setChecked(info.isChecked());
			//只是让checkbox不显示.
			if(getPackageName().equals(info.getPackname())){
				holder.cb.setVisibility(View.INVISIBLE);
			}else{
				//利用必须加上这个 条件
				holder.cb.setVisibility(View.VISIBLE);
			}


			return view;
		}

	}
	static class ViewHolder{
		ImageView iv;
		TextView tv_name;
		TextView tv_mem;
		CheckBox cb;
	}

	public void killTask(View view){
		long count=0;
		long savedmem = 0;
		//杀死那些被选择的item对应的进程.
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ArrayList<TaskInfo> killedTasks = new ArrayList<TaskInfo>();
		for(TaskInfo info : taskinfos){
			if(info.isChecked()){
				//杀死这个进程.
				am.killBackgroundProcesses(info.getPackname());
				count++;
				savedmem+=info.getMemsize();
				killedTasks.add(info);
			}
		}
		taskinfos.removeAll(killedTasks);
		adapter.notifyDataSetChanged();//通知适配器
		String size = Formatter.formatFileSize(this, savedmem);
		//Toast.makeText(this, "杀死了"+count+"个进程,释放了"+size+"的内存", 1).show();
		MyToast.show(this, R.drawable.notification, "杀死了"+count+"个进程,释放了"+size+"的内存");
		processcount -= count;
		availmem+= savedmem;
		tv_task_count.setText("正在运行:"+ processcount+"个");
		tv_task_mem.setText("剩余/总内存:"+Formatter.formatFileSize(this, availmem)+"/"+Formatter.formatFileSize(this, totalmem));
	}
}