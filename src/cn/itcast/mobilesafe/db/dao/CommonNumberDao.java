package cn.itcast.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {

	/**
	 * 获取数据库有多少个分组信息
	 *
	 * @return
	 */
	public static int getGroupCount() {
		int count = 0;
		// 1.打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from classlist", null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	/**
	 * 获取数据库所有的分组信息
	 *
	 * @return
	 */
	public static List<String> getGroupItems() {
		List<String>  groupItems = new ArrayList<String>();
		// 1.打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select name from classlist", null);
		while(cursor.moveToNext()){
			groupItems.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return groupItems;
	}
	/**
	 * 获取数据库某个分组有多少个孩子信息
	 * groupPosition从0开始
	 * @return
	 */
	public  static int getChildCount(int groupPosition) {
		int count = 0;
		// 1.打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newid = groupPosition + 1;
		Cursor cursor = db.rawQuery("select * from table" + newid, null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	/**
	 * 获取数据库某个分组所有的孩子信息
	 *
	 * @return
	 */
	public  static List<String> getChildItems(int groupPosition) {
		List<String> childItems =new ArrayList<String>();
		// 1.打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newid = groupPosition + 1;
		Cursor cursor = db.rawQuery("select name,number from table" + newid, null);
		while(cursor.moveToNext()){
			childItems.add(cursor.getString(0)+"\n"+cursor.getString(1));
		}
		cursor.close();
		db.close();
		return childItems;
	}

	/**
	 * 返回某个分组的名称
	 * @param groupPosition
	 * @return
	 */
	public static  String getGroupName(int groupPosition){
		String groupname="";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newid = groupPosition + 1;
		Cursor cursor = db.rawQuery("select name from classlist where idx=?", new String[]{newid+""});
		if(cursor.moveToFirst()){
			groupname = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return groupname;
	}
	/**
	 * 返回某个分组的某个孩子信息
	 * @param groupPosition
	 * @return
	 */
	public static  String getChildInfoByPosition(int groupPosition,int childPosition){
		String result="";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"/data/data/cn.itcast.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);
		int newgroupid = groupPosition + 1;
		int newchildid = childPosition + 1;

		Cursor cursor = db.rawQuery("select name,number from table"+newgroupid+" where _id=?", new String[]{newchildid+""});
		if(cursor.moveToFirst()){
			String name  = cursor.getString(0);
			String number  = cursor.getString(1);
			result = name+"\n"+number;
		}
		cursor.close();
		db.close();
		return result;
	}
}
