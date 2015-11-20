package cn.itcast.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import cn.itcast.mobilesafe.db.AppLockDBOpenHelper;
/**
 * 应用程序锁dao层
 * @author superboy
 *
 */
public class AppLockDao {

	private AppLockDBOpenHelper helper;
	private Context context;
	public static  final Uri applockuri = Uri.parse("content://cn.itcast.mobile/applock");//  content:   必须 的
	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * 添加包名
	 *
	 * @param packname
	 *            包名
	 * @param mode
	 *            拦截模式
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into applock (packname) values (?)",
				new Object[] { packname });
		context.getContentResolver().notifyChange(applockuri, null);//通知 内容 观察者
		db.close();
	}

	/**
	 * 查询包名
	 *
	 * @param packname
	 *            包名
	 * @return
	 */
	public boolean find(String packname) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select * from applock where packname=?",
				new String[] { packname });
		if (curosr.moveToNext()) {
			result = true;
		}
		curosr.close();
		db.close();
		return result;
	}



	/**
	 * 删除一条包名记录
	 *
	 * @param packname
	 */
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from applock where packname=?",
				new Object[] { packname });
		context.getContentResolver().notifyChange(applockuri, null);//通知 内容 观察者
		db.close();
	}

	/**
	 * 查询全部的程序锁 包名信息
	 * @return
	 */
	public List<String> findAll() {
		List<String> result = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor curosr = db.rawQuery("select packname from applock ",
				null);
		while (curosr.moveToNext()) {
			result.add(curosr.getString(0));
		}
		curosr.close();
		db.close();
		return result;
	}

}