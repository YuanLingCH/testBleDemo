package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.impl.OnLoginListener;
import fangzuzu.com.ding.md5.MD5Utils;
import fangzuzu.com.ding.presenter.LoginPresenter;
import fangzuzu.com.ding.utils.KeyBoardHelper;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.phoneCheck;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.view.ILoginView;


/**
 * 登录界面
 * Created by yuanling on 2018/4/2.
 */

public class LoginActivity extends BaseActivity implements ILoginView, OnLoginListener,View.OnClickListener {
    LoginPresenter presenter;

    EditText etName;
ImageView pasw_show;
    private Boolean showPassword = true;
    EditText etPassword;
    ProgressDialog progressDialog;
    private KeyBoardHelper boardHelper;

    private TextView tv_forgetPassWord;
    private TextView tv_immediately_register;
    private Button but_login;
    String uid;
    private View layoutContent;
    boolean isKitKat = false;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.login_activity_layout);


        initalize();//初始化控件
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setStatusBar();
        setSupportActionBar(toolbar);

        String uid = MainApplication.getInstence().getUid();
        Log.d("TAG","udi"+uid);
        //实例化presenter
        presenter = new LoginPresenter(this);
        //设置界面
        presenter.setUser();
        initStep(); //点击事件


        boardHelper = new KeyBoardHelper(this);
        boardHelper.onCreate();
        boardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);

    }


    protected void setStatusBar() {
        if (isKitKat){

            int statusH = screenAdapterUtils.getStatusHeight(this);
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }




    private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

        @Override
        public void OnKeyBoardPop(int keyBoardheight) {




                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layoutContent
                        .getLayoutParams();
                lp.topMargin = (int) -(keyBoardheight/1.2);
                layoutContent.setLayoutParams(lp);



        }

        @Override
        public void OnKeyBoardClose(int oldKeyBoardheight) {

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layoutContent
                    .getLayoutParams();
            if (lp.topMargin != 0) {
                lp.topMargin = 0;
                layoutContent.setLayoutParams(lp);
            }

        }
    };





    private void initalize() {
        layoutContent=findViewById(R.id.layoutContent);
        etName= (EditText) findViewById(R.id.et_name);
        etPassword= (EditText) findViewById(R.id.et_password);
        tv_forgetPassWord= (TextView) findViewById(R.id.tv_forget_passWord);
        tv_immediately_register= (TextView) findViewById(R.id.tv_immediately_register);
        but_login= (Button) findViewById(R.id.but_login);
        pasw_show= (ImageView) findViewById(R.id.pasw_show);
        pasw_show.setImageDrawable(getResources().getDrawable(R.drawable.eye_unable));
        pasw_show.setOnClickListener(this);


    }

    @Override
    public void loginSuccess(UserBean user) {
        presenter.saveUser(user);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               Timer timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent  intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                        startActivity(intent);
                    }
                },500);
                //成功界面
                String pasw = etPassword.getText().toString().trim();
                Log.d("TAG","成功后密码"+pasw);
                SharedUtils.putString("pasw",pasw);
                hideProgressDialog();
            }
        });

    }

    @Override
    public void loginFaild() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        });
       Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show();
    }
    @Override
    public void setName(String name) {
        etName.setText(name);
    }

    @Override
    public void setPassword(String password) {
        final String psw = etPassword.getText().toString().trim();
        etPassword.setText(psw);
    }

    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title, message, true, false);
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

    private void initStep() {



        //登录到主界面
        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString().trim();
                final String pasw = etPassword.getText().toString().trim();
                Log.d("TAG","name"+name);
                etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Log.d("TAG","校验name"+name);
                        if (!hasFocus){
                            if (phoneCheck.isChinaPhoneLegal(name)){

                            }else {
                                Toast.makeText(LoginActivity.this, "不是电话号码", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                NetWorkTesting net=new NetWorkTesting(MainApplication.getInstence());
                if (net.isNetWorkAvailable()) {

                if (!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(pasw)){
                    showProgressDialog("","正在登陆...");
                    final UserBean bean = new UserBean();
                    bean.name = etName.getText().toString();
                    String psw = MD5Utils.md5Password(etPassword.getText().toString()).toUpperCase();
                    bean.password = psw;
                    presenter.login(bean, LoginActivity.this);



                }else {
                    Toast.makeText(LoginActivity.this, "填写正确信息", Toast.LENGTH_LONG).show();
                }
                }
                else {
                    Toast.makeText(MainApplication.getInstence(),"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();

                }
            }
        });
        //忘记密码界面
        tv_forgetPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(),ForgetPassWordActivity.class );
                startActivity(intent);

            }
        });
        //注册界面
        tv_immediately_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(), RegisterActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        boardHelper.onDestory();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pasw_show:
                if (showPassword) {// 显示密码
                    pasw_show.setImageDrawable(getResources().getDrawable(R.drawable.eye_able));
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(etPassword.getText().toString().length());
                    showPassword = !showPassword;
                } else {// 隐藏密码
                    pasw_show.setImageDrawable(getResources().getDrawable(R.drawable.eye_unable));
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPassword.setSelection(etPassword.getText().toString().length());
                    showPassword = !showPassword;
                }
                break;
        }
    }
}
