package cn.itcast.mobilesafe.ui;

import cn.itcast.mobilesafe.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 算定义cn.itcast.mobilesafe.ui.SettingView
 * @author superboy
 *
 */
public class SettingView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_content;
	private CheckBox cb_setting_view_status;

	public SettingView(Context context) {
		super(context);
		inflateView(context);
	}


	//无构造函数  xml引用会报错
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflateView(context);
		TypedArray  a = context.obtainStyledAttributes(attrs, R.styleable.setting_view);//obtainStyledAttributes之后一定要调用 recycle
		String title = a.getString(R.styleable.setting_view_title);
		String content = a.getString(R.styleable.setting_view_content);
		tv_title.setText(title);
		tv_content.setText(content);
		//记得清理数据
		a.recycle();
	}

	private void inflateView(Context context){
		View view = View.inflate(context, R.layout.ui_setting_view, this);
		//在代码里面找到布局文件的配置信息. 修改view对象里面的内容
		tv_title = (TextView) view.findViewById(R.id.tv_setting_view_title);
		tv_content = (TextView) view.findViewById(R.id.tv_setting_view_content);
		cb_setting_view_status = (CheckBox)view.findViewById(R.id.cb_setting_view_status);
	}

	/**
	 * 判断当前settingview是否被选中
	 * @return
	 */
	public boolean isChecked(){
		return cb_setting_view_status.isChecked();
	}
	/**
	 * 更改settingview的勾选状态.
	 * @param checked
	 */
	public void setChecked(boolean checked){
		cb_setting_view_status.setChecked(checked);
	}
	public void setContent(String text){
		tv_content.setText(text);
	}
}