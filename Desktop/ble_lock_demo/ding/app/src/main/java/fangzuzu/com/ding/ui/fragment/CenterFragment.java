package fangzuzu.com.ding.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.UIutils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.bean.appBean;
import fangzuzu.com.ding.ui.activity.ForgetPassWordActivity;
import fangzuzu.com.ding.ui.activity.LoginActivity;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.widget.VersionUpdateTipdialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

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
    ImageView center_iv_notifi;
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
        center_iv_notifi=(ImageView) root.findViewById(R.id.center_iv_notifi);
        setStatusBar();
        //退出登陆
        exit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View view = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) view.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) view.findViewById(R.id.add_cancle);
                EditText et_yanzhenpasw= (EditText) view.findViewById(R.id.et_yanzhenpasw);
                et_yanzhenpasw.setVisibility(View.INVISIBLE);
                TextView tv1= (TextView) view.findViewById(R.id.tv);
                tv1.setVisibility(View.INVISIBLE);
                tv.setText("确定退出登陆");
                tv.setTextSize(16);
                tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setView(view)
                        .create();
                dialog.show();
                tv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                tv_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

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

        getAppVersion();
    }
   String versionName;
    @Override
    protected void initEvents() {
        //版本升级  检查是不是有新版本 有就提示  没有就显示已经最新的了
        ll_versions= (RelativeLayout) root.findViewById(R.id.ll_versions);
        tv_version= (TextView) root.findViewById(R.id.tv_version);
        int versionCode = UIutils.getVersionCode();
       versionName = UIutils.getVersionName();
        setVersionNema();
        Log.d("TAG","versionCode"+versionCode);
        Log.d("TAG","versionName"+versionName);


        ll_versions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (versionName.equals(newVersionNum)){
                    Toast.makeText(MainApplication.getInstence(),"当前已是最新版本",Toast.LENGTH_SHORT).show();
                }else {

                    getAppUpdate();
                }

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
        //联网请求数据   服务器版本大于本地版本  就升级
        if (!StringUtils.isEmpty(versionFileUrl)){
            new VersionUpdateTipdialog(getActivity(),newVersionNum,"2018年9月31号","修复了一下bug ,优化了功能",versionFileUrl).show();
        }else {
            Toast.makeText(MainApplication.getInstence(),"服务器开小差了，请重试",Toast.LENGTH_SHORT).show();
        }



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

    /**
     * 检查APP 版本
     */
    String newVersionNum ; //服务器版本
    String versionFileUrl; //服务器的uir APK 路径
    public void getAppVersion() {
        Log.d("TAG","检查版本走了");
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .baseUrl(apiManager.baseUrl)
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Call<String> callVersion = manager.getAppVersion("1");
        callVersion.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){
                    Log.d("TAG","检查版本"+body);
                    Gson gson=new Gson();
                    appBean bean = gson.fromJson(body, new TypeToken<appBean>() {}.getType());
                    int code = bean.getCode();
                    if (code==1001){
                        appBean.DataBean data = bean.getData();
                       newVersionNum = data.getNewVersionNum();
                       versionFileUrl = data.getVersionFileUrl();
                        MainApplication.getInstence().setAppVersion(versionFileUrl);
                       if (versionName.equals(newVersionNum)){
                           center_iv_notifi.setVisibility(View.GONE);
                       }else {
                           center_iv_notifi.setVisibility(View.VISIBLE);
                       }

                    }else {

                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }
}
