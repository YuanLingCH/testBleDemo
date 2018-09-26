package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/9/18.
 */

public class keyManagerDetialActivity extends BaseActivity {
    Toolbar toolbar;
    boolean isKitKat = false;
    RelativeLayout rl_name,rl_time,rl_jilu;
    TextView key_lock,key_time,accept_name,send_name,send_time,key_time_end;
    Button but_key_delect;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.key_manager_detail_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initViews();
        getIntentData();
        initEvent();
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
    private void initViews() {
        rl_name=(RelativeLayout) findViewById(R.id.rl_name);
        rl_time=(RelativeLayout) findViewById(R.id.rl_time);
        rl_jilu=(RelativeLayout) findViewById(R.id.rl_jilu);
        key_lock=(TextView) findViewById(R.id.key_lock);
        key_time=(TextView) findViewById(R.id.key_time);
        accept_name=(TextView) findViewById(R.id.accept_name);
        send_name=(TextView) findViewById(R.id.send_name);
        send_time=(TextView) findViewById(R.id.send_time);
        but_key_delect=(Button) findViewById(R.id.but_key_delect);
        key_time_end=(TextView) findViewById(R.id.key_time_end);

    }

    /**
     * 接收上一个界面传过来的数据
     */
    String startTime;
    String endTime;
    String keyName;
    String childUsername;
    String parentId;
    String userId1;
    String id;
    public void getIntentData() {
      startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
       keyName = getIntent().getStringExtra("keyName");
        childUsername = getIntent().getStringExtra("childUsername");
        parentId = getIntent().getStringExtra("parentId");
      userId1 = getIntent().getStringExtra("userId1");
       id = getIntent().getStringExtra("id");



    }
    /**
     * 点击事件
     */
    private void initEvent() {

        accept_name.setText(childUsername);
        send_name.setText(parentId);
        send_time.setText(startTime);
        key_lock.setText(keyName);
        key_time.setText(startTime);
        key_time_end.setText(endTime);
        /**
         * 修改名称
         */
        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(keyManagerDetialActivity.this);
                View view = inflater.inflate(R.layout.updata_lock_name, null);
                TextView add_cancle= (TextView) view.findViewById(R.id.add_cancle);
                // add_submit
                TextView add_submit= (TextView) view.findViewById(R.id.add_submit);


                final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_name);
                final AlertDialog dialog = new AlertDialog.Builder(keyManagerDetialActivity.this)
                        .setView(view)
                        .create();
                dialog.show();
                add_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                add_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trim = editText.getText().toString().trim();
                        if (!StringUtils.isEmpty(trim)){
                            dialog.dismiss();
                         upDatakeyLockName(trim,startTime,endTime);
                        }else {

                            Toast.makeText(keyManagerDetialActivity.this,"内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        /**
         * 修改时间
         */
        rl_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(),modificationKeyLockMessageActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("startTime",startTime);
                intent.putExtra("endTime",endTime);
                intent.putExtra("keyName",keyName);
                startActivity(intent);
            }
        });
        /**
         * 删除钥匙
         */
        but_key_delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit re=new Retrofit.Builder()
                        .baseUrl(apiManager.baseUrl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(MainApplication.getInstence().getClient())
                        .build();
                apiManager manager = re.create(apiManager.class);
                Call<String> call = manager.delectKey(id);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String body = response.body();
                        Gson gson=new Gson();
                        msg m = gson.fromJson(body, new TypeToken<msg>() {}.getType());

                        int code = m.getCode();
                        if (code==1001){
                            Log.d("TAG","删除成功");
                            Toast.makeText(keyManagerDetialActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                            finish();

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });
        /**
         * 开锁记录
         */
        rl_jilu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(),childOpenClickRecord.class);
                intent.putExtra("uid",userId1); //child 的
                intent.putExtra("keyName",keyName);
                intent.putExtra("lockId",id);
                startActivity(intent);
            }
        });
    }

    /**
     * 上传修改钥匙的名称
     *
     */
    private void upDatakeyLockName(final String lockName, String start, String end) {
        Map <String,String>map=new HashMap<>();
        map.put("id",id);
        map.put("keyName",lockName);
        map.put("startTime",start);
        map.put("endTime",end);
        final Gson gson=new Gson();
        final String s = gson.toJson(map);
        Log.d("TAG","上传数据"+s);
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();

        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.modificationKeyLcokMeaasge(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){


                Log.d("TAG","修改数据"+body);
                msg o = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = o.getCode();
                if (code==1001){
                    key_lock.setText(lockName);
                    Toast.makeText(MainApplication.getInstence(),"修改名称成功",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainApplication.getInstence(),"修改名称失败",Toast.LENGTH_LONG).show();
                }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
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


}
