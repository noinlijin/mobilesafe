package cn.itcast.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.itcast.mobilesafe.domain.ContactInfo;
import cn.itcast.mobilesafe.engine.ContactInfoProvider;
/**
 * 选择联系人界面
 * @author superboy
 *
 */
public class SelectContactActivity extends Activity {
	private ListView lv_select_contact;
	private List<ContactInfo>  infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv_select_contact = (ListView)findViewById(R.id.lv_select_contact);
		infos = ContactInfoProvider.getContactInfos(this);//查询数据库的操作是否放在子线程中呢
		lv_select_contact.setAdapter(new SelectContactAdapter());

		lv_select_contact.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				ContactInfo info = (ContactInfo) lv_select_contact.getItemAtPosition(position);
				String number = info.getNumber();
				Intent data = new Intent();
				data.putExtra("number", number);
				setResult(0, data);
				finish();
			}
		});
	}

	private class SelectContactAdapter extends BaseAdapter{
		public int getCount() {
			return infos.size();
		}

		public Object getItem(int position) {
			return infos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {//是否优化
			View view = View.inflate(getApplicationContext(), R.layout.list_item_contact, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_item_contact_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_item_contact_number);
			ContactInfo info = infos.get(position);
			tv_name.setText(info.getName());
			tv_number.setText(info.getNumber());
			return view;
		}
	}
}