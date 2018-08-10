package fangzuzu.com.ding.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import fangzuzu.com.ding.R;

/**
 * 功能：Dialog基类
 * 时间：2017年02月18日
 */
public class BaseDialog extends Dialog{

	public BaseDialog(Context context, int themeResId) {
		super(context, themeResId);
	}
	
	public BaseDialog(Context context) {
		super(context, R.style.dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去掉标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	/**
	 * 宽度全屏，高度自动
	 */
	public void setWidthM_HeithW(){
		// 设置窗口大小
		LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	}
}
