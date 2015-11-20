package cn.itcast.mobilesafe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import cn.itcast.mobilesafe.db.dao.CommonNumberDao;

public class CommnumActivity extends Activity {
	private ExpandableListView elv;

	private List<String> groupItems;
	private Map<Integer, List<String>> childItemsMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_num);
		elv = (ExpandableListView) findViewById(R.id.elv_commonnum);
		childItemsMap = new HashMap<Integer, List<String>>();

		elv.setAdapter(new CommonNumAdapter());

	}

	private class CommonNumAdapter extends BaseExpandableListAdapter {

		/**
		 * 返回分组的数量
		 */
		public int getGroupCount() {
			groupItems = CommonNumberDao.getGroupItems();
			return groupItems.size();
		}

		/**
		 * 返回每个分组里面有多少个子孩子
		 */
		public int getChildrenCount(int groupPosition) {
			List<String> childItems;
			if (childItemsMap.get(groupPosition) != null) {
				childItems = childItemsMap.get(groupPosition);
			} else {
				childItems = CommonNumberDao.getChildItems(groupPosition);
				childItemsMap.put(groupPosition, childItems);
			}
			return childItemsMap.get(groupPosition).size();
		}

		public Object getGroup(int groupPosition) {
			return null;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public boolean hasStableIds() {
			return false;
		}

		/**
		 * 得到每一个分组的view对象
		 */
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView != null && convertView instanceof TextView) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(getApplicationContext());
			}
			tv.setTextSize(24);
			tv.setTextColor(Color.RED);
			tv.setText("      " + groupItems.get(groupPosition));
			return tv;
		}

		public View getChildView(int groupPosition, int childPosition,
								 boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView != null && convertView instanceof TextView) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(getApplicationContext());
			}
			tv.setTextSize(15);
			tv.setTextColor(Color.BLUE);
			tv.setText(childItemsMap.get(groupPosition).get(childPosition));
			return tv;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}
}