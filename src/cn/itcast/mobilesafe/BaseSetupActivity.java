package cn.itcast.mobilesafe;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
/**
 * 封装 手机防盗公共的部分
 * @author superboy
 *
 */
public abstract class BaseSetupActivity extends Activity {
	protected static final String TAG = "BaseSetupActivity";
	protected SharedPreferences sp;
	// 1.创建一个手势识别器
	protected GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 2.初始化手势识别器
		mGestureDetector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener() {
					// velocityx 水平方向移动的速度
					@Override//返回true消费掉  即 终止掉
					public boolean onFling(MotionEvent e1, MotionEvent e2,
										   float velocityX, float velocityY) {
						//e1 e2  第一点第二触点   velocityX velocityY  竖直与垂直方向上移动的速度
						if (Math.abs(velocityX) < 100) {//取绝对值   单位 像素
							Log.i(TAG, "移动的太慢");
							return true;
						}

						if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
							Log.i(TAG, "动作不合法");
							return true;
						}
						if (e2.getRawX() - e1.getRawX() > 200) {
							showPre();
							return true;
						}

						if (e1.getRawX() - e2.getRawX() > 200) {
							showNext();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});

		sp = getSharedPreferences("config", MODE_PRIVATE);
		findView();
		setupView();
	}
	/**
	 *查找控件
	 */
	public abstract void findView();
	/**
	 * 数据回显
	 */
	public abstract void setupView();
	/**
	 * 下一页
	 */
	public abstract void showNext();
	/**
	 * 前一页
	 */
	public abstract void showPre();

	public void next(View view) {
		showNext();
	}

	public void pre(View view) {
		showPre();
	}

	public void openActivity(Class<?> cls) {
		Intent intent = new Intent(this, cls);
		startActivity(intent);
		finish();
	}

	// 3.让手势识别器工作
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}