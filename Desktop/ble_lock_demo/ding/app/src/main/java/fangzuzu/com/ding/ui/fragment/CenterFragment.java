package fangzuzu.com.ding.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.UIutils;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.ui.activity.ForgetPassWordActivity;
import fangzuzu.com.ding.ui.activity.LoginActivity;
import fangzuzu.com.ding.utils.screenAdapterUtils;

/**
 * Created by yuanling on 2018/5/12.
 */

public class CenterFragment extends BaseFragment {
    TextView exit_login, updataPasw;
    Toolbar toolbar;
    boolean isKitKat = false;
    RelativeLayout ll_versions;
    TextView tv_version;//版本号
    ProgressDialog progressDialog;
    @Override
    protected int getLayoutId() {
        return R.layout.center_faragment_layout;
    }

    @Override
    protected void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);

        toolbar.setTitle("");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        updataPasw = (TextView) root.findViewById(R.id.updataPasw);
        exit_login = (TextView) root.findViewById(R.id.exit_login);
        setStatusBar();
        //退出登陆
        exit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBean user = SharedUtils.getUser();
                if (user != null) {
                    String name = user.name;
                    name = "";
                    SharedPreferences shared = MainApplication.getInstence().getSharedPreferences("shared", Context.MODE_PRIVATE);
                    shared.edit().clear().commit();
                    MainApplication.getInstence().removeALLActivity_();  //清掉全部Activity
                    Intent intent = new Intent(MainApplication.getInstence(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }


            }
        });
        updataPasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getInstence().removeALLActivity_();  //清掉全部Activity
                Intent intent = new Intent(MainApplication.getInstence(), ForgetPassWordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initEvents() {
        //版本升级  检查是不是有新版本 有就提示  没有就显示已经最新的了
        ll_versions= (RelativeLayout) root.findViewById(R.id.ll_versions);
        tv_version= (TextView) root.findViewById(R.id.tv_version);
        int versionCode = UIutils.getVersionCode();
        String versionName = UIutils.getVersionName();
        setVersionNema();
        Log.d("TAG","versionCode"+versionCode);
        Log.d("TAG","versionName"+versionName);
        ll_versions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppUpdate();
            }
        });
    }
    /**设置版本名称*/
    private void setVersionNema() {
        tv_version.setText(UIutils.getVersionName());
    }
    @Override
    protected void initData() {

    }
    protected void setStatusBar() {
        if (isKitKat){
            int statusH = screenAdapterUtils.getStatusHeight(MainApplication.getInstence());
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getActivity().getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }

    /**
     * 待完善
     */
    public void getAppUpdate() {
      //  showProgressDialog("","正在检查版本");
        //联网请求数据


    }

    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(getActivity(), title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
