package cn.itcast.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
/**
 * 归属地提示框位置
 * @author superboy
 *
 */
public class DragViewActivity extends Activity {
	protected static final String TAG = "DragViewActivity";
	private ImageView iv_drag_view;
	private TextView tv_drag_info;
	private Display  display;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.activity_drag_view);
		sp =getSharedPreferences("config", MODE_PRIVATE);
		WindowManager wm = getWindowManager();
		display = wm.getDefaultDisplay();

		iv_drag_view = (ImageView) findViewById(R.id.iv_drag_view);
		tv_drag_info = (TextView) findViewById(R.id.tv_drag_info);


		//初始化 位置
		int lastx = sp.getInt("lastx", 0);
		int lasty = sp.getInt("lasty", 0);

		//注意: 在界面没有被渲染出来之前 layout的方法是不会生效的 ,必须要采用布局的方式 指定位置
		RelativeLayout.LayoutParams params = (LayoutParams) iv_drag_view.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		iv_drag_view.setLayoutParams(params);

		iv_drag_view.setOnTouchListener(new OnTouchListener() {
			//记录初始的坐标
			int startX;
			int startY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					// 手指第一次触摸到屏幕
					case MotionEvent.ACTION_DOWN:
						Log.i(TAG,"摸到!");
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					// 手指在屏幕上移动
					case MotionEvent.ACTION_MOVE:
						Log.i(TAG,"滑动!");
						int newX = (int) event.getRawX();
						int newY = (int) event.getRawY();
						int dx = newX - startX;
						int dy = newY - startY;
						int l = iv_drag_view.getLeft();
						int t = iv_drag_view.getTop();
						int b = iv_drag_view.getBottom();
						int r = iv_drag_view.getRight();

						int newl = l+dx;
						int newr = r+dx;
						int newt = t+dy;
						int newb = b+dy;

						if(newl<0||newr>display.getWidth()||newt<0||newb>display.getHeight()){
							break;
						}

						int tv_height = tv_drag_info.getBottom() - tv_drag_info.getTop();


						if(newt>display.getHeight()/2){
							//tv 设置显示在上方
							tv_drag_info.layout(tv_drag_info.getLeft(), 0, tv_drag_info.getRight(), tv_height);
						}else{
							//tv 显示在下方.
							tv_drag_info.layout(tv_drag_info.getLeft(), display.getHeight()- tv_height-30, tv_drag_info.getRight(), display.getHeight()-30);
						}

						iv_drag_view.layout(newl, newt, newr, newb);
						startX = (int) event.getRawX();//更改初始位置
						startY = (int) event.getRawY();

						break;
					// 手指离开了屏幕
					case MotionEvent.ACTION_UP:
						Log.i(TAG,"松手!");
						Editor editor = sp.edit();
						editor.putInt("lastx", iv_drag_view.getLeft());
						editor.putInt("lasty", iv_drag_view.getTop());
						editor.commit();
						break;

				}

				return true;
			}
		});
	}

}