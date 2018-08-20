package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
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
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.bean.userLockBean;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.impl.OnMqttListener;
import fangzuzu.com.ding.presenter.MqttPresenter;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.utils.byteCunchu.getbyte;

/**
 * 打开蓝牙检查
 * Created by lingyuan on 2018/6/20.
 */

public class keySetActivity extends BaseActivity implements OnMqttListener{
    List dataPart=new ArrayList();
    List data3;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "keySetActivity";
    private byte[] token3;
    byte[] aesks;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
    Toolbar toolbar;
    TextView tv_time;
    ProgressDialog progressDialog;
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
        mBleController = BleController.getInstance().init(keySetActivity.this);
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
        aesks = getbyte("secretKeyBytes");
        allowbyt= getbyte("allowbyt");
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
                        View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                        final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                        final TextView tv_tishi = (TextView) viewDialog.findViewById(R.id.tv);
                        tv_tishi.setText("密码验证");

                        // tv.setText("谨慎操作，导致数据丢失...");
                        // tv.setTextColor(Color.RED);
                        tv.setVisibility(View.GONE);
                        tv.setGravity(Gravity.CENTER);
                        TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                        final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                                .setView(viewDialog)
                                .create();
                        dialog.show();
                        final String pasw = SharedUtils.getString("pasw");
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
                                String pas = et.getText().toString().trim();
                                if (pas.equals(pasw)){

                                    // tv_pasw.setText(content);
                                    //跳到修改管理员密码界面
                                    Intent intent =new Intent(keySetActivity.this,upDataManagerPaswActivity.class);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(keySetActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


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

                    View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                    final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                    TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                    final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                    final TextView tv_tishi = (TextView) viewDialog.findViewById(R.id.tv);
                    tv_tishi.setText("密码验证");

                    // tv.setText("谨慎操作，导致数据丢失...");
                    // tv.setTextColor(Color.RED);
                    tv.setVisibility(View.GONE);
                    tv.setGravity(Gravity.CENTER);
                    TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                    final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                            .setView(viewDialog)
                            .create();
                    dialog.show();
                    final String pasw = SharedUtils.getString("pasw");
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
                            String pas = et.getText().toString().trim();
                            if (pas.equals(pasw)){

                                Intent intent =new Intent(keySetActivity.this,factoryResetActivity.class);
                                intent.putExtra("id",id);
                                startActivity(intent);

                            }else {
                                Toast.makeText(keySetActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



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
                TextView add_cancle= (TextView) view.findViewById(R.id.add_cancle);
                // add_submit
                TextView add_submit= (TextView) view.findViewById(R.id.add_submit);


                final EditText editText = (EditText) view.findViewById(R.id.dialog_edit_name);
                final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
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
                            name_lock.setText(trim);
                            upDataLockName(trim);
                        }else {

                            Toast.makeText(keySetActivity.this,"内容不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

           /*     AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                        .setView(view)
                        .setCustomTitle(title)
                        .create()
                        .show();*/

            /*            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
                dialog.show();*/
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


    public void butClick(View view) {

        final String adminUserId = SharedUtils.getString("adminUserId");
        final String keyId = SharedUtils.getString("keyId");
        Log.d("TAG","修改+点击了我"+adminUserId+":"+ keyId);
        final String lockid = MainApplication.getInstence().getLockid();
        final String mac = MainApplication.getInstence().getMac();
        View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
        final EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
        // tv.setText("谨慎操作，导致数据丢失...");
        // tv.setTextColor(Color.RED);
        tv.setVisibility(View.GONE);
        tv.setGravity(Gravity.CENTER);
        TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
        final AlertDialog dialog = new AlertDialog.Builder(keySetActivity.this)
                .setView(viewDialog)
                .create();
        dialog.show();
        final String pasw = SharedUtils.getString("pasw");
        Log.d("TAG","密码"+pasw);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pas = et.getText().toString().trim();
                if (!StringUtils.isEmpty(pas)){
                    if (pas.equals(pasw)){
                        Log.d("TAG","删除走了"+pasw);
                        dialog.dismiss();
                        //1 根据当前uid 来判断  和锁里面的uid 是不是蓝牙管理员和普通用户
                        //连接蓝牙 和删除服务器数据
                        if (uid.equals(adminUserId)){
                            initReceiveData();
                            initConnectBle(mac,lockid);
                        }else {
                            //普通直接删除钥匙

                            upDataDelet(keyId);
                        }

                    }else {
                        Toast.makeText(keySetActivity.this,"你的密码错误", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }else {
                    Toast.makeText(keySetActivity.this,"请输入密码", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    MqttPresenter mqPre;
    ;public void   upDataDelet(String id){
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
                    Toast.makeText(keySetActivity.this,"删除数据成功", Toast.LENGTH_SHORT).show();

                    getUserLockList();

                    mqPre=new MqttPresenter();
                    mqPre.sendMqtt("az"+uid,keySetActivity.this);
                    mqPre.sendMqtt("ios"+uid,keySetActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    @Override
    public void mqttSuccess() {

    }

    @Override
    public void mqttFaild() {

    }

    /**
     * 连接蓝牙
     *
     */
    String idL;

    private void initConnectBle(String lockNumber,String idlock) {
        idL=idlock;
        Log.d("TAG","idlock"+idlock);


        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            showProgressDialog("","正在连接蓝牙...");
            // 7D:8D:22:4A:85:C7
            mBleController.connect(0, lockNumber, new ConnectCallback() {
                @Override
                public void onConnSuccess() {
                    // Toast.makeText(MainApplication.getInstence(), "连接成功", Toast.LENGTH_SHORT).show();
                    Log.d("TAG","连接成功删除钥匙");
                    jiaoyan();

                }

                @Override
                public void onConnFailed() {
                    //如果失败连接  考虑重连蓝牙   递归
                    mBleController.closeBleConn();
                    Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败，确认手机在锁旁边", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();

                }

            });
        }

    }

    private void jiaoyan(){
        //身份校验
        Log.d("TAG","身份校验开始");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[]data4={0x02,0x01,0x04,0x02,0x03,0x04,0x05,0x00,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf};
                byte[] encrypt = jiamiandjiemi.Encrypt(data4,  aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt) + "\r\n");

                mBleController.writeBuffer(encrypt, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","身份校验成功");
                        sendFirstCode();  //发送锁标识符
                    }
                    @Override
                    public void onFailed(int state) {
                        Log.d("TAG","身份校验失败"+state);
                    }
                });
            }
        },500);

    }

    /**
     * 发送锁标识符
     */
    private void sendFirstCode(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //4.发送锁标识
                //  allowbyt
                Log.d("TAG","存储数据 Token_fr"+mBleController.bytesToHexString(token3) + "\r\n");
                byte[]data5=new byte[16];
                for (int i = 0; i < allowbyt.length; i++) {
                    Log.d("TAG","all"+allowbyt[i]);
                }
                data5[0]=0x02;
                data5[1]=0x02;
                data5[2]=0x06;
                data5[3]=allowbyt[0];
                data5[4]=allowbyt[1];
                data5[5]=allowbyt[2];
                data5[6]=allowbyt[3];
                data5[7]=allowbyt[4];
                data5[8]=allowbyt[5];
                data5[11]= token3[0];
                data5[12]= token3[1];
                data5[13]= token3[2];
                data5[14]= token3[3];
                byte[] encrypt1 = jiamiandjiemi.Encrypt(data5,  aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt1) + "\r\n");
                Log.d("TAG","aaaaaa"+mBleController.bytesToHexString(data5) + "\r\n");

                mBleController.writeBuffer(encrypt1, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");
                        tongbuTime();

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });
            }
        },500);


    }
    //同步时间
    private void tongbuTime() {
        //获取当前时间戳
        long timeStampSec = System.currentTimeMillis()/1000;
        String timestamp = String.format("%010d", timeStampSec);
        Log.d("TAG",""+timestamp);
        String string1 = Integer.toHexString((int) timeStampSec);
        Log.d("TAG","..."+string1);
        byte[] bytes = jiamiandjiemi.hexString2Bytes(string1);
        for (int i = 0; i < bytes.length; i++) {
            Log.d("TAG","."+bytes[i]);
        }

        byte[]data80=new byte[16];
        data80[0]=0x03;
        data80[1]=0x03;
        data80[2]=0x04;
        data80[3]=bytes[0];
        data80[4]=bytes[1];
        data80[5]=bytes[2];
        data80[6]=bytes[3];
        data80[7]=token3[0];
        data80[8]=token3[1];
        data80[9]=token3[2];
        data80[10]=token3[3];
        ;
        byte[] encrypt40 = jiamiandjiemi.Encrypt(data80, aesks);
        Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt40) + "\r\n");

        mBleController.writeBuffer(encrypt40, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","发送成功");

            }
            @Override
            public void onFailed(int state) {

            }
        });

    }

    /**
     * 删除蓝牙管理员
     *
     */
    private void upDataDeletLock() {
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.delctLock(idL);   //锁id
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Gson gson=new Gson();
                msg m = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = m.getCode();
                if (code==1001){
                    Log.d("TAG","删除成功");
                    Toast.makeText( keySetActivity.this,"删除数据成功", Toast.LENGTH_SHORT).show();
                    getUserLockList();
                    mqPre=new MqttPresenter();
                    mqPre.sendMqtt("az"+uid, keySetActivity.this);
                    mqPre.sendMqtt("ios"+uid, keySetActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 蓝牙管理员
     */
    public  void blemanager(){
        byte[]data10=new byte[16];
        data10[0]=0x03;
        data10[1]=0x07;
        data10[2]=0x01;
        data10[3]=0x00;
        data10[4]=token3[0];
        data10[5]=token3[1];
        data10[6]=token3[2];
        data10[7]=token3[3];
        byte[] encrypt6 = jiamiandjiemi.Encrypt(data10, aesks);
        Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt6) + "\r\n");

        mBleController.writeBuffer(encrypt6, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","发送成功");

            }
            @Override
            public void onFailed(int state) {

            }
        });
    }


    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","解密"+mBleController.bytesToHexString(decrypt) + "\r\n");
                if (decrypt[0]==02&&decrypt[1]==01&&decrypt[2]==04){
                    System.arraycopy(decrypt,3,token2,0,token2.length);
                    token3=new byte[4];
                    byte[]token1=new byte[4];
                    token1[0]=02;
                    token1[1]=03;
                    token1[2]=04;
                    token1[3]=05;
                    token3[0]= (byte) (token2[0]^token1[0]);
                    token3[1]= (byte) (token2[1]^token1[1]);
                    token3[2]= (byte) (token2[2]^token1[2]);
                    token3[3]= (byte) (token2[3]^token1[3]);
                }if (decrypt[0]==03&&decrypt[1]==04&&decrypt[2]==01&&decrypt[3]==01){
                    tongbuTime();  //同步时间
                }if (decrypt[0]==02&&decrypt[1]==02&&decrypt[2]==04&&decrypt[3]==00){
                    blemanager();//删除锁

                }if (decrypt[0]==03&&decrypt[1]==07&&decrypt[2]==01&&decrypt[3]==00){
                    upDataDeletLock();//删除服务器数据
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    mBleController.closeBleConn();
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


    /**
     * 请求网络数据
     */
    String lockName;
    String secretKey;
    String adminPsw;
    String adminUser;
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
                                String keyId = next.getKeyId();
                                SharedUtils.putString("keyId",keyId);
                                id1 = next.getId();
                                lockNumber = next.getLockNumber();
                                Log.d("TAG","锁命"+lockName);
                                data3.add(next);
                            }
                            data3.addAll(dataPart);
                            Log.d("TAG","集合大小"+data3.size());
                            Log.d("TAG",body);

                            if (data3.size()==1){
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),MainActviity.class);
                                        intent.putExtra("id",id1);
                                        intent.putExtra("secretKey",secretKey);
                                        intent.putExtra("allow",allow);
                                        intent.putExtra("electricity",electricity);
                                        intent.putExtra("lockNumber",lockNumber);
                                        intent.putExtra("adminPsw",adminPsw);
                                        intent.putExtra("lockName",lockName);
                                        intent.putExtra("jihe","1");
                                        intent.putExtra("adminUserId",adminUser);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);


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

            Toast.makeText(keySetActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();



        }
    }
}
