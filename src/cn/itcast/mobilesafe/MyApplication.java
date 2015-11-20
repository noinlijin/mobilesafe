package cn.itcast.mobilesafe;

import android.app.Application;

//在当前应用对应的进程 创建的时候  实例化. 实例化所有的四大组件之前的.
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		// 全局初始化的操作.
		super.onCreate();
		// 把系统里面所有的未能捕获的异常都给创建出来一个默认的处理器.

		//给当前的主线程 设置一个默认的 未捕获的消息处理器
		Thread.currentThread().setUncaughtExceptionHandler(MyUncaughtExceptionHandler.getInstance());

	}
}