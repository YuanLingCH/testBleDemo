package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.md5.MD5Utils;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.phoneCheck;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * 忘记密码界面
 * Created by yuanling on 2018/4/2.
 */

public class ForgetPassWordActivity extends BaseActivity {
    Toolbar toolbar;
    ProgressDialog progressDialog;
    Button but_outhcode;
    private TimeCount time;
    EditText et_phone,et_authcode,et_passWord,et_repetition_password;
    Button but_modifier_password;
    boolean isKitKat = false;
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
        setContentView(R.layout.forgetpassword_activity_layout);

    toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

    setSupportActionBar(toolbar);
    getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();

        et_phone= (EditText) findViewById(R.id.et_phone);
        but_modifier_password= (Button) findViewById(R.id.but_modifier_password);
        et_authcode= (EditText) findViewById(R.id.et_authcode);
        et_passWord= (EditText) findViewById(R.id.et_passWord);
        et_repetition_password= (EditText) findViewById(R.id.et_repetition_password);
        initlize();

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




    String msg;
    String outhPhone;
    private void initlize() {
        time = new TimeCount(60000, 1000);
        but_outhcode= (Button) findViewById(R.id.but_outhcode);

        //获取手机验证码
        but_outhcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outhPhone = et_phone.getText().toString().trim();
                Log.d("TAG","走了");
                if (phoneCheck.isChinaPhoneLegal(outhPhone)){


                if (outhPhone.isEmpty()||outhPhone.equals("")){
                    but_outhcode.setClickable(false);
                }else if (outhPhone.length()==11) {
                    Log.d("TAG","走了12");
                    time.start();
                    Retrofit retrofit=new Retrofit.Builder()
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .baseUrl(apiManager.baseUrl)
                            .client(MainApplication.getInstence().getClient())
                            .build();
                    apiManager api= retrofit.create(apiManager.class);

                    Call<String> call = api.getOuthCode(outhPhone);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String body = response.body();
                            Log.d("TAG","验证码"+body);
                            Gson gson=new Gson();
                            msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());

                            msg= s.getData();

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }

                }else {
                    Toast.makeText(ForgetPassWordActivity.this, "不是电话号码", Toast.LENGTH_LONG).show();
                }

            }
        });


        but_modifier_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("", "正在加载...");
                String auth = et_authcode.getText().toString().trim();
                String password = et_passWord.getText().toString().trim();
                final String repaw = et_repetition_password.getText().toString().trim();
                String passwordjiami = MD5Utils.md5Password(repaw).toUpperCase();
                final String auth1 = MD5Utils.md5Password(auth + "fzz").toUpperCase();
                if (!StringUtils.isEmpty(auth) && !StringUtils.isEmpty(password) && !StringUtils.isEmpty(auth1)&&!StringUtils.isEmpty(msg)) {


                Log.d("TAG", "验证码加密" + auth1);
                if (!msg.equals(auth1)) {
                    Toast.makeText(ForgetPassWordActivity.this, "验证码输入错", Toast.LENGTH_LONG).show();
                } else if (msg.equals(auth1)) {
                    if (!password.equals(repaw)) {
                        Toast.makeText(ForgetPassWordActivity.this, "密码不相同", Toast.LENGTH_LONG).show();
                    } else {
                        Map<String, String> map = new HashMap();
                        map.put("username", outhPhone);
                        map.put("password", passwordjiami);
                        final Gson gson = new Gson();
                        String s = gson.toJson(map);
                        Retrofit retrofit = new Retrofit.Builder()
                                .addConverterFactory(ScalarsConverterFactory.create())
                                .baseUrl(apiManager.baseUrl)
                                .client(MainApplication.getInstence().getClient())
                                .build();
                        final apiManager apim = retrofit.create(apiManager.class);
                        Call<String> call = apim.upDataPassword(outhPhone, passwordjiami);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String body = response.body();
                                Log.d("TAG", "修改密码" + body);
                                msg s = gson.fromJson(body, new TypeToken<msg>() {
                                }.getType());
                                String code = s.getCode() + "";
                                if (code.equals("1001")) {
                                    Toast.makeText(ForgetPassWordActivity.this, "修改密码成功，牢记密码", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ForgetPassWordActivity.this, LoginActivity.class);
                                    intent.putExtra("username", outhPhone);
                                    intent.putExtra("password", repaw);
                                    startActivity(intent);
                                    hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(ForgetPassWordActivity.this, "修改密码失败，请检查网络", Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                            }
                        });


                    }

                }
            }else {
                    Toast.makeText(ForgetPassWordActivity.this, "请填写完整信息", Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }
            }
        });

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




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            but_outhcode.setBackgroundColor(Color.parseColor("#B6B6D8"));
            but_outhcode.setClickable(false);
            but_outhcode.setTextSize(12);
            but_outhcode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            but_outhcode.setText("重新获取验证码");
            but_outhcode.setClickable(true);
            but_outhcode.setBackgroundColor(Color.parseColor("#4EB84A"));

        }
    }

}
