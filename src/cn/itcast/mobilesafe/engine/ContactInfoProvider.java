package cn.itcast.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.itcast.mobilesafe.domain.ContactInfo;
/**
 * * 获取系统里面的联系人.
 * @author superboy
 *
 */
public class ContactInfoProvider {
	/**
	 * 获取系统里面的联系人.
	 *
	 * @param context
	 * @return
	 */
	public static List<ContactInfo> getContactInfos(Context context) {
		// 1.从raw_contact 把联系人的id取出去 .
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		while (cursor.moveToNext()) {
			// 2.根据这个id 把data表里面的数据取出来.
			String id = cursor.getString(0);
			if (id != null) {//联系人删除  即 修改data表中contact_id为null同步邮箱。。。
				ContactInfo info = new ContactInfo();
				Cursor dataCursor = resolver.query(datauri, new String[] {
								"mimetype", "data1" }, "raw_contact_id=?",
						new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String mime = dataCursor.getString(0);
					String data1 = dataCursor.getString(1);
					if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
						info.setNumber(data1);
					} else if ("vnd.android.cursor.item/name".equals(mime)) {
						info.setName(data1);
					}
				}
				dataCursor.close();//关闭cursor
				infos.add(info);
			}
		}
		cursor.close();//关闭cursor
		return infos;
	}
}