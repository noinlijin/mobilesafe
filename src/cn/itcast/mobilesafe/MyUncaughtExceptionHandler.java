package cn.itcast.mobilesafe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.os.Build;
import android.os.Environment;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private MyUncaughtExceptionHandler() {
	};

	private static MyUncaughtExceptionHandler mExceptionHandler;

	/**
	 * 未处理的异常处理器 第一个参数 出现异常的线程 第二个参数 当前的异常对象.
	 */
	public void uncaughtException(Thread thread, Throwable err) {

		// throwable.printStackTrace(err);
		System.out.println("发生了异常,但是被我捕获了.!!!");
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					"error.log");
			FileOutputStream fos;
			fos = new FileOutputStream(file);
			fos.write(("time:"+System.currentTimeMillis()+"\n").getBytes());
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 暴力反射私有字段.
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				fos.write((name+":"+value+"\n").getBytes());
			}

			StringWriter sw = new StringWriter();
			PrintWriter writer = new PrintWriter(sw);
			err.printStackTrace(writer);
			err.printStackTrace();

			String errorlog = sw.toString();
			System.out.println("---"+errorlog);
			fos.write(errorlog.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public synchronized static MyUncaughtExceptionHandler getInstance() {
		if (mExceptionHandler == null) {
			mExceptionHandler = new MyUncaughtExceptionHandler();
		}
		return mExceptionHandler;
	}
}