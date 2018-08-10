package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.widget.DatePicier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/6/4.
 */

public class addfingerOneStepActivity extends BaseActivity {
    Toolbar toolbar;
    LinearLayout  create_time;
    String username,usernumber;
    ToggleButton tglSound;



    LinearLayout lose_time;
    EditText electfrg_key_name;

    private TextView currentDate, currentTime;
    Button but_next;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_finger_one_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initlize();


        EventBus.getDefault().register(this);
    }

    private void initlize() {
        electfrg_key_name= (EditText) findViewById(R.id.electfrg_key_name);
        but_next= (Button) findViewById(R.id.but_next);
        currentTime= (TextView)findViewById(R.id.electfrg_effect_time);
        currentDate= (TextView) findViewById(R.id.electfrg_lose_time);
        create_time= (LinearLayout)findViewById(R.id.create_time);
        lose_time=(LinearLayout)findViewById(R.id.lose_time);
        DatePicier.initDatePicker(currentDate, currentTime, addfingerOneStepActivity.this);
        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(currentTime.getText().toString());
            }
        });
        lose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker1().show(currentTime.getText().toString());
            }
        });
        tglSound= (ToggleButton) findViewById(R.id.tglSound);



        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("","正在加载...");
                s1 = createtime.replaceAll(" ", "-");
                s2 = s1.replaceAll(":", "-");
                Log.d("TAG","s2"+ s2);

                s3 =   endtime.replaceAll(" ", "-");
                s4 = s3.replaceAll(":", "-");
                Log.d("TAG","s4"+s4);

                if (s1.isEmpty()){
                    Toast.makeText(addfingerOneStepActivity.this,"生效时间不能为空",Toast.LENGTH_LONG).show();
                }else if (s3.isEmpty()){
                    Toast.makeText(addfingerOneStepActivity.this,"失效时间不能为空",Toast.LENGTH_LONG).show();
                }else if (!s2.equals("")&&!s4.equals("")){



                    Map<String,String> map= new HashMap<>();

                    //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}

                    map.put("lockId","5b97192a-631d-11e8-b04f-00163e0c1269");
                    map.put("unlockName",electfrg_key_name.getText().toString().trim());
                    map.put("unlockFlag","");
                    map.put("allow","54321");
                    map.put("addPerson","123456");
                    map.put("forWay","");
                    map.put("startTime",s2);
                    map.put("endTime",s4);
                    map.put("addType","2");
                    map.put("unlockType","0");
                    Gson gson=new Gson();
                    String value = gson.toJson(map);



                    Retrofit retrofit=new Retrofit.Builder()
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .client(MainApplication.getInstence().getClient())
                            .baseUrl(apiManager.baseUrl)
                            .build();
                    apiManager manager = retrofit.create(apiManager.class);
                    Call<String> call = manager.sendPassward(value);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String body = response.body();
                            Log.d("TAG","上传数据默认"+body);
                            Gson gson1=new Gson();
                            msg s = gson1.fromJson(body, new TypeToken<msg>() {}.getType());
                            String code = s.getCode()+"";
                            if (code.equals("1001")){
                                Intent intent=new Intent(addfingerOneStepActivity.this,addfingerTwoStepActivity.class);
                                startActivity(intent);
                                hideProgressDialog();

                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                       Toast.makeText(addfingerOneStepActivity.this,"请检测网络",Toast.LENGTH_LONG).show();
                            hideProgressDialog();
                        }
                    });


                }



            }
        });










        tglSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    //做开启的业务
                    create_time.setVisibility(View.INVISIBLE);
                    lose_time.setVisibility(View.INVISIBLE);
                    but_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProgressDialog("","正在加载...");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");// HH:mm:ss
                            //获取当前时间
                            Date date = new Date(System.currentTimeMillis());
                            // time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));

                            Log.d("TAG","当前时间"+simpleDateFormat.format(date));




                            Map<String,String> map= new HashMap<>();

                            //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}


                            map.put("lockId","5b97192a-631d-11e8-b04f-00163e0c1269");
                            map.put("unlockName",electfrg_key_name.getText().toString().trim());
                            map.put("unlockFlag","");
                            map.put("allow","54321");
                            map.put("addPerson","123456");
                            map.put("forWay","");
                            map.put("startTime",simpleDateFormat.format(date));
                            map.put("endTime","2025-06-02-10-54");
                            map.put("addType","2");
                            map.put("unlockType","1");
                            Gson gson=new Gson();
                            String value = gson.toJson(map);

                            Retrofit retrofit=new Retrofit.Builder()
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .client(MainApplication.getInstence().getClient())
                                    .baseUrl(apiManager.baseUrl)
                                    .build();
                            apiManager manager = retrofit.create(apiManager.class);
                            Call<String> call = manager.sendPassward(value);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    String body = response.body();
                                    Log.d("TAG","上传数据"+body);
                                    Gson gson1=new Gson();
                                    msg s = gson1.fromJson(body, new TypeToken<msg>() {}.getType());
                                    String code = s.getCode()+"";
                                    if (code.equals("1001")){
                                        Intent intent=new Intent(addfingerOneStepActivity.this,addfingerTwoStepActivity.class);
                                        startActivity(intent);
                                        hideProgressDialog();

                                    }

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(addfingerOneStepActivity.this,"请检测网络",Toast.LENGTH_LONG).show();
                                    hideProgressDialog();
                                }
                            });
                        }
                    });

                }else{
                    //做没开启的业务  对事显示
                    Log.d("TAG","显示走了");
                    create_time.setVisibility(View.VISIBLE);
                    lose_time.setVisibility(View.VISIBLE);

                    but_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProgressDialog("","正在加载...");
                            s1 = createtime.replaceAll(" ", "-");
                            s2 = s1.replaceAll(":", "-");
                            Log.d("TAG","s2"+ s2);

                            s3 =   endtime.replaceAll(" ", "-");
                            s4 = s3.replaceAll(":", "-");
                            Log.d("TAG","s4"+s4);

                            if (s1.isEmpty()){
                                Toast.makeText(addfingerOneStepActivity.this,"生效时间不能为空",Toast.LENGTH_LONG).show();
                            }else if (s3.isEmpty()){
                                Toast.makeText(addfingerOneStepActivity.this,"失效时间不能为空",Toast.LENGTH_LONG).show();
                            }else if (!s2.equals("")&&!s4.equals("")){



                                Map<String,String> map= new HashMap<>();

                                //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}

                                map.put("lockId","5b97192a-631d-11e8-b04f-00163e0c1269");
                                map.put("unlockName",electfrg_key_name.getText().toString().trim());
                                map.put("unlockFlag","");
                                map.put("allow","54321");
                                map.put("addPerson","123456");
                                map.put("forWay","");
                                map.put("startTime",s2);
                                map.put("endTime",s4);
                                map.put("addType","2");
                                map.put("unlockType","0");
                                Gson gson=new Gson();
                                String value = gson.toJson(map);



                                Retrofit retrofit=new Retrofit.Builder()
                                        .addConverterFactory(ScalarsConverterFactory.create())
                                        .client(MainApplication.getInstence().getClient())
                                        .baseUrl(apiManager.baseUrl)
                                        .build();
                                apiManager manager = retrofit.create(apiManager.class);
                                Call<String> call = manager.sendPassward(value);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        String body = response.body();
                                        Log.d("TAG","上传数据现时"+body);
                                        Gson gson1=new Gson();
                                        msg s = gson1.fromJson(body, new TypeToken<msg>() {}.getType());
                                        String code = s.getCode()+"";
                                        if (code.equals("1001")){
                                            Intent intent=new Intent(addfingerOneStepActivity.this,addfingerTwoStepActivity.class);
                                            startActivity(intent);
                                            hideProgressDialog();
                                        }


                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(addfingerOneStepActivity.this,"请检测网络",Toast.LENGTH_LONG).show();
                                        hideProgressDialog();
                                    }
                                });


                            }



                        }
                    });
                }

            }
        });
    }


    String s2;
    String s4;
    String s1;
    String s3;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime();
        Log.d("TAG","event"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime();
        Log.d("TAG","event"+currentTime);


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
ProgressDialog progressDialog;
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
}
