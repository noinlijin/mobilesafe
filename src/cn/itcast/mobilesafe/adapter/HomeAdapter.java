package cn.itcast.mobilesafe.adapter;

import cn.itcast.mobilesafe.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * HomeActivity GridView 数据适配器(待优化参考listview优化)
 * @author superboy
 *
 */
public class HomeAdapter extends BaseAdapter {

	private Context context;


	public HomeAdapter(Context context) {
		this.context = context;
	}

	private static final String[] names={"手机防盗","通讯卫士","软件管理","进程管理","流量统计",
			"手机杀毒","系统清理","高级工具","设置中心"};

	private static final int[] icons={R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,R.drawable.taskmanager,R.drawable.netmanager,
			R.drawable.trojan,R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};
	public int getCount() {
		return names.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//1.把xml布局转化成一个view对象.
		View view = View.inflate(context, R.layout.grid_item_main, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_item_main_name);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_item_main_icon);
		tv.setText(names[position]);
		iv.setImageResource(icons[position]);
		return view;
	}

}