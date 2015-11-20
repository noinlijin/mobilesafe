package cn.itcast.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 获取手机号码归属地的Dao
 *
 * @author Administrator
 *
 */
public class AddressDao {
	public static String getAddress(String number) {
		// 如果号码没有查询到 就返回当前号码
		String address = number;
		// 1.打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/address.db", null,
				SQLiteDatabase.OPEN_READONLY);
		// 首先判断 number是否是一个手机号码.
		// 长度11位 1开头 3458 9位的数字.
		if (number.matches("^1[3458]\\d{9}$")) {
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id = ?)",
							new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();

		} else {// 非手机号码
			switch (number.length()) {
				case 3:
					address = "紧急电话";
					break;
				case 4:
					address = "模拟器";
					break;
				case 5:
					address = "特殊电话";
					break;

				case 7:
					address = "本地电话";
					break;
				case 8:
					address = "本地电话";
					break;
				default://前3位 或4位 不会重复
					if (number.length() >= 10 && number.startsWith("0")) {
						Cursor cursor = db.rawQuery(
								"select location from data2 where area = ?",
								new String[] { number.substring(1, 3) });
						if (cursor.moveToFirst()) {
							String location = cursor.getString(0);
							location = location.replace("电信", "");
							location = location.replace("移动", "");
							location = location.replace("联通", "");
							address = location;
						}
						cursor.close();
						cursor = db.rawQuery(
								"select location from data2 where area = ?",
								new String[] { number.substring(1, 4) });
						if (cursor.moveToFirst()) {
							String location = cursor.getString(0);
							location = location.replace("电信", "");
							location = location.replace("移动", "");
							location = location.replace("联通", "");
							address = location;
						}
						cursor.close();
					}

					break;
			}

		}
		db.close();
		return address;
	}
}