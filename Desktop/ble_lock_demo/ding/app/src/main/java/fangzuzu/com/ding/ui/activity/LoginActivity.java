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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.bean.userLockBean;
import fangzuzu.com.ding.impl.OnLoginListener;
import fangzuzu.com.ding.md5.MD5Utils;
import fangzuzu.com.ding.presenter.LoginPresenter;
import fangzuzu.com.ding.service.mqttService;
import fangzuzu.com.ding.utils.KeyBoardHelper;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.phoneCheck;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.view.ILoginView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


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

    private View layoutContent;
    boolean isKitKat = false;
    Toolbar toolbar;
    List data3;
    String uid;
    List dataPart=new ArrayList();

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
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    pasw_show.setVisibility(View.VISIBLE);
                }else {
                    pasw_show.setVisibility(View.INVISIBLE);
                }
            }
        });
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){

                    String name = etName.getText().toString().trim();
                    if (phoneCheck.isChinaPhoneLegal(name)){

                    }else {
                        Toast.makeText(LoginActivity.this, "不是电话号码", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



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
                getUserLockList();
                hideProgressDialog();
              /* Timer timer=new Timer();

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

*/

                //成功界面
                String pasw = etPassword.getText().toString().trim();
                Log.d("TAG","成功后密码"+pasw);
                SharedUtils.putString("pasw",pasw);

                //启动服务 连接mqtt
                Intent intent=new Intent(LoginActivity.this, mqttService.class);
                startService(intent);
            }
        });

    }

    @Override
    public void loginFaild() {

        hideProgressDialog();
       Toast.makeText(this, "用户名或者密码错误", Toast.LENGTH_LONG).show();
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

    /**
     * 请求网络数据
     */
    String lockName;
    String secretKey;
    String adminPsw;
    String adminUserId;
    String electricity;
    String allow;
    String id1;
    String lockNumber;
    public void getUserLockList() {
        data3=new ArrayList();
        String partid = SharedUtils.getString("partid");
        uid= SharedUtils.getString("uid");
        Log.d("TAG","partid"+partid);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        final NetWorkTesting net=new NetWorkTesting(this);
        if (net.isNetWorkAvailable()){
            Call<String> call = manager.getLockUserList("aa123456", uid);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String body = response.body();
                    if (StringUtils.isEmpty(body)){
                        Log.d("TAG","网络错误"+body);

                    }else {
                        Log.d("TAG","测试一把手锁"+body );
                        Gson gson=new Gson();
                        userLockBean bean = gson.fromJson(body, new TypeToken<userLockBean>() {}.getType());
                        int code = bean.getCode();
                        data3.clear();// 防止加载重复数据
                        if (code==1001){
                            userLockBean.DataBean data = bean.getData();
                            List<?> parentLock = data.getParentLock();
                            Iterator<?> iterator = parentLock.iterator();
                            while (iterator.hasNext()){
                                Object next = iterator.next();
                                dataPart.add(next);
                            }
                            List<userLockBean.DataBean.UserLockBean> userLock = data.getUserLock();
                            Iterator<userLockBean.DataBean.UserLockBean> iterator1 = userLock.iterator();
                            while (iterator1.hasNext()){
                                userLockBean.DataBean.UserLockBean next = iterator1.next();
                                lockName = next.getLockName();
                                secretKey = next.getSecretKey();
                                adminPsw = next.getAdminPsw();
                                adminUserId = next.getAdminUserId();
                                electricity = next.getElectricity();
                                allow = next.getAllow();
                                id1 = next.getId();
                                lockNumber = next.getLockNumber();
                                Log.d("TAG","锁命"+lockName);
                                data3.add(next);
                            }
                            data3.addAll(dataPart);
                            Log.d("TAG","集合大小"+data3.size());
                            Log.d("TAG",body);

                            if (data3.size()==1){
                                Intent intent=new Intent(MainApplication.getInstence(),MainActviity.class);
                                intent.putExtra("id",id1);
                                intent.putExtra("secretKey",secretKey);
                                intent.putExtra("allow",allow);
                                intent.putExtra("electricity",electricity);
                                intent.putExtra("lockNumber",lockNumber);
                                intent.putExtra("adminPsw",adminPsw);
                                intent.putExtra("lockName",lockName);
                                intent.putExtra("jihe","1");
                                intent.putExtra("adminUserId",adminUserId);
                                startActivity(intent);
                                finish();

                            }else {
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);
                            }


                        }else if(code==1002){
                            Log.d("TAG","网络错误");

                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }else {

            Toast.makeText( LoginActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();



        }
    }


}
