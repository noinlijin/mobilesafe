package cn.itcast.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 黑名单 拦截数据库blacknumber.db的创建
 * @author superboy
 *
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	public BlackNumberDBOpenHelper(Context context) {

		super(context, "blacknumber.db", null, 1);
	}

	/**
	 * 数据库第一次被创建的时候 调用的方法.
	 * 拦截模式 0 全部拦截 1电话拦截 2短信拦截
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//黑名单号码 , 拦截模式
		db.execSQL("create table blacknumber (id integer primary key autoincrement,number varchar(20),mode varchar(2) )", new Object[]{});
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}