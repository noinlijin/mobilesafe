package cn.itcast.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntivirusDao {

	/**
	 * 查询当前md5是否在病毒数据库里面.
	 *
	 * @param md5
	 * @return 病毒的描述信息. 如果不存在 返回null
	 */
	public static String findVirus(String md5) {
		String result = null;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/antivirus.db", null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select desc from datable where md5=?",
				new String[] { md5 });
		if (cursor.moveToNext()) {
			result = cursor.getString(0);

		}
		cursor.close();
		db.close();
		return result;
	}
}