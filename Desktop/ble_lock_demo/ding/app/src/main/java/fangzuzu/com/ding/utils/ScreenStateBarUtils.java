package fangzuzu.com.ding.utils;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by lingyuan on 2018/7/25.
 */

public class ScreenStateBarUtils {
    protected boolean useThemestatusBarColor = false;
    public static void setStatusBar(Toolbar toolbar, Context mcontext, View decorView,WindowManager.LayoutParams localLayoutParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上


         //   View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            int statusH = screenAdapterUtils.getStatusHeight(mcontext);
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            //根据上面设置是否对状态栏单独设置颜色
           /* if (useThemestatusBarColor) {
                decorView.setStatusBarColor(getResources().getColor(R.color.color3));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }*/
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
           // WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            int statusH = screenAdapterUtils.getStatusHeight(mcontext);
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            //华为手机
            if (screenAdapterUtils.hasNotchInScreen(mcontext)){

                int[] notchSize = screenAdapterUtils.getNotchSize(mcontext);
                for (int i = 0; i < notchSize.length; i++) {
                    Log.d("TAG",notchSize[i]+"刘海kk");
                }
                int statusBarHeight = screenAdapterUtils.getStatusBarHeight(mcontext);
                Log.d("TAG",statusBarHeight+"刘海状态栏kk");
                //计算状态栏的高度
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
                params.height = params.height + statusBarHeight;
                toolbar.setLayoutParams(params);
                //设置ToolBar的PaddingTop属性
                toolbar.setPadding(0, statusBarHeight, 0, 0);
                Log.d("TAG",statusBarHeight+"刘海走了kk");
             decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            //opp手机
            if (screenAdapterUtils.hasNotchInOppo(mcontext)){
              //  WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
                int statusH = screenAdapterUtils.getStatusHeight(mcontext);
                //获取ToolBar的布局属性，设置ToolBar的高度
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
                params.height = params.height + statusH;
                toolbar.setLayoutParams(params);
                //设置ToolBar的PaddingTop属性
                toolbar.setPadding(0, statusH, 0, 0);
            }if (screenAdapterUtils.hasNotchInScreenAtVoio(mcontext)){
              //  WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
                int statusH = screenAdapterUtils.getStatusHeight(mcontext);
                //获取ToolBar的布局属性，设置ToolBar的高度
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
                params.height = params.height + statusH;
                toolbar.setLayoutParams(params);
                //设置ToolBar的PaddingTop属性
                toolbar.setPadding(0, statusH, 0, 0);

            }


        }
    }
}
