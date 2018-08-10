package fangzuzu.com.ding.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansion.h_ble.BleController;

import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
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
 * 打开蓝牙检查
 * Created by lingyuan on 2018/6/20.
 */

public class keySetActivity extends BaseActivity {
    Toolbar toolbar;
    TextView tv_time;
    private BleController mBleController;
    LinearLayout ll_set_managerPasw;
    TextView set_keymanager,tv_factory_reset,name_lock,lock_elect,mac;
    RelativeLayout rl;
    String uid; //蓝牙管理员
    String adminUserId;//锁的
    private StringBuffer mReciveString = new StringBuffer();
    boolean isKitKat = false;
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
        setContentView(R.layout.key_set_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initgetInintentData();
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




    private void initgetInintentData() {
         uid = SharedUtils.getString("uid");
      adminUserId = SharedUtils.getString("adminUserId");
        Log.d("TAG","uid"+uid);
        Log.d("TAG","adminUserId"+adminUserId);
    }

    String id;
    private void initlize() {
       id = getIntent().getStringExtra("id");
        set_keymanager= (TextView) findViewById(R.id.set_keymanager);
        tv_time= (TextView) findViewById(R.id.tv_time_clock);
        ll_set_managerPasw= (LinearLayout) findViewById(R.id.ll_set_managerPasw);
        //同步时钟
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(keySetActivity.this,TimeLockActivity.class);
                startActivity(intent);
            }
        });
        ll_set_managerPasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (uid.equals(adminUserId)){

                        // tv_pasw.setText(content);
                        //跳到修改管理员密码界面
                        Intent intent =new Intent(keySetActivity.this,upDataManagerPaswActivity.class);
                        startActivity(intent);

                    }else {

                        View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                        TextView tv1= (TextView) viewDialog.findViewById(R.id.tv);
                        tv1.setVisibility(View.INVISIBLE);
                        tv.setText("你不是锁管理员，没有权限操作");
                        tv.setTextColor(Color.RED);
                        tv.setGravity(Gravity.CENTER);
                        TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                        final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                                .setView(viewDialog)
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


                            }
                        });
                    }

            }
        });
        //恢复出厂设置
        tv_factory_reset= (TextView) findViewById(R.id.tv_factory_reset);
        tv_factory_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.equals(adminUserId)){
                    Intent intent =new Intent(keySetActivity.this,factoryResetActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }else {
                    View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                    final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                    TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                    tv.setText("你不是锁管理员，没有权限操作");
                    TextView tv1= (TextView) viewDialog.findViewById(R.id.tv);
                    tv1.setVisibility(View.INVISIBLE);
                    tv.setTextColor(Color.RED);
                    tv.setGravity(Gravity.CENTER);
                    TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                    final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                            .setView(viewDialog)
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


                        }
                    });
                }


            }
        });
        rl= (RelativeLayout) findViewById(R.id.rl);
        name_lock= (TextView) findViewById(R.id.name_lock);
        String lockName = MainApplication.getInstence().getLockName();
        name_lock.setText(lockName);
        lock_elect= (TextView) findViewById(R.id.lock_elect);
        String elect = MainApplication.getInstence().getElect();
        lock_elect.setText(elect+"%");
        String pasword = MainApplication.getInstence().getPasword();
        if (uid.equals(adminUserId)){
            set_keymanager.setText(pasword);
        }else {
            set_keymanager.setVisibility(View.INVISIBLE);
        }

        String mac = MainApplication.getInstence().getMac();
        this.mac = (TextView) findViewById(R.id.mac);
        this.mac.setText(mac);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框 修改锁名字
            if (uid.equals(adminUserId)){
                LayoutInflater inflater = LayoutInflater.from(keySetActivity.this);
                View view = inflater.inflate(R.layout.updata_lock_name, null);
                TextView title = new TextView(keySetActivity.this);
                title.setGravity(Gravity.CENTER);
                title.setPaddingRelative(0,50,0,0);
                title.setText("修改锁名称");

                final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_name);
                AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                        .setView(view)
                        .setCustomTitle(title)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String trim = editText.getText().toString().trim();
                                if (!StringUtils.isEmpty(trim)){
                                    name_lock.setText(trim);
                                    upDataLockName(trim);
                                }else {
                                    String content = editText.getText().toString().trim();
                                    // tv_pasw.setText(content);
                                    //跳到修改管理员密码界面
                                    Intent intent =new Intent(keySetActivity.this,upDataManagerPaswActivity.class);
                                    startActivity(intent);

                                }

                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }else {
                View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                tv.setText("你不是锁管理员，没有权限操作");
                TextView tv1= (TextView) viewDialog.findViewById(R.id.tv);
                tv1.setVisibility(View.INVISIBLE);
                tv.setTextColor(Color.RED);
                tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                        .setView(viewDialog)
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


                    }
                });
            }

            }
        });
    }

    /**
     * 修改锁名
     */
    private void upDataLockName(String name) {
        String lockid = MainApplication.getInstence().getLockid();
        String uid = MainApplication.getInstence().getUid();
        String elect = MainApplication.getInstence().getElect();
        String pasword = MainApplication.getInstence().getPasword();
        Map<String,String>map=new HashMap<>();
        map.put("id",lockid );
        map.put("adminUserId",uid );
        map.put("lockName",name );
        map.put("adminPsw",pasword );
        map.put("electricity",elect );
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.upDatalockName(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG","修改"+body);
               msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = s.getCode();
                if (code==1001){
                    Toast.makeText(keySetActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
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
