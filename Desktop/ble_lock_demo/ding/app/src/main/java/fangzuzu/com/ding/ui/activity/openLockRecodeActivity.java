package fangzuzu.com.ding.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import fangzuzu.com.ding.adapter.openLockRecodeAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.openLockRecoderBean;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.utils.byteCunchu.getbyte;

/**
 * 获取开锁记录
 * Created by lingyuan on 2018/6/20.
 */

public class openLockRecodeActivity extends BaseActivity {

    Toolbar toolbar;
    openLockRecodeAdapter adapter;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "openLockRecodeActivity";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    RecyclerView rc;
    List data3;
    String lockid;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
    private byte[] token3;
    byte[] aesks;//密钥
    EditText et_open_lock;
    FrameLayout open_lock_frl;
    TextView open_lock_pasw,open_lock_app,open_lock_finger,open_lock_ic,open_lock_shengfenz,tv_open_lock_cannel;
    LinearLayout open_molder_ll;
    RecyclerView re_reach;
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
        setContentView(R.layout.open_lock_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        //初始化蓝牙
       lockid = getIntent().getStringExtra("Lockid");
        Log.d("TAG","传过来的id"+lockid);

        initlize();
        initEvent();
        getOpenLockRecoder("");
        initSearch();
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




    private void initEvent( ) {

        mBleController = BleController.getInstance();

        //获取密钥
        aesks = getbyte("secretKeyBytes");
        allowbyt= getbyte("allowbyt");
        initReceiveData();
        initConnectBle();
    }

    /**
     * 连接蓝牙
     */
    private void initConnectBle() {

        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
        String lockNumber = MainApplication.getInstence().getMac();
        Log.d("TAG","mac地址"+lockNumber);
        // 7D:8D:22:4A:85:C7
        mBleController.connect(0, lockNumber, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                // Toast.makeText(MainApplication.getInstence(), "连接成功", Toast.LENGTH_SHORT).show();
                Log.d("TAG","连接成功");
                jiaoyan();

            }

            @Override
            public void onConnFailed() {
                //如果失败连接  考虑重连蓝牙   递归
                mBleController.closeBleConn();
                Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败，确认手机在锁旁边", Toast.LENGTH_SHORT).show();


            }

        });

        }
    }

    private void jiaoyan(){
        //身份校验
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
                        sendopenLockrecode();
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

    private void sendopenLockrecode() {

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    byte[]data14a=new byte[16];
                    data14a[0]=0x05;
                    data14a[1]=0x01;
                    data14a[2]=0x01;
                    data14a[3]=0x00;
                    data14a[4]=token3[0];
                    data14a[5]=token3[1];
                    data14a[6]=token3[2];
                    data14a[7]=token3[3];

                    byte[] encrypt10a = jiamiandjiemi.Encrypt(data14a, aesks);
                    Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10a) + "\r\n");

                    mBleController.writeBuffer(encrypt10a, new OnWriteCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("TAG","发送成功");
                            sendopenLockrecodeone();
                        }
                        @Override
                        public void onFailed(int state) {
                        }
                    });

                }
            },500);



    }


    /**
     * 确认第一次
     */
    private void sendopenLockrecodeone() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[]data14a=new byte[16];
                data14a[0]=0x05;
                data14a[1]=0x02;
                data14a[2]=0x01;
                data14a[3]=0x00;
                data14a[4]=token3[0];
                data14a[5]=token3[1];
                data14a[6]=token3[2];
                data14a[7]=token3[3];

                byte[] encrypt10a = jiamiandjiemi.Encrypt(data14a, aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10a) + "\r\n");

                mBleController.writeBuffer(encrypt10a, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");
                        sendopenLockrecodetow();
                    }
                    @Override
                    public void onFailed(int state) {
                    }
                });

            }
        },500);



    }

    /**
     * 确认第二次
     */
    private void sendopenLockrecodetow() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[]data14a=new byte[16];
                data14a[0]=0x05;
                data14a[1]=0x03;
                data14a[2]=0x01;
                data14a[3]=0x00;
                data14a[4]=token3[0];
                data14a[5]=token3[1];
                data14a[6]=token3[2];
                data14a[7]=token3[3];

                byte[] encrypt10a = jiamiandjiemi.Encrypt(data14a, aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10a) + "\r\n");

                mBleController.writeBuffer(encrypt10a, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");
                    }
                    @Override
                    public void onFailed(int state) {
                    }
                });

            }
        },500);



    }

    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","解密addICCardOneStepActivity"+mBleController.bytesToHexString(decrypt) + "\r\n");
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
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleController.closeBleConn();
    }

    private void initlize() {
        data3=new ArrayList();

        rc= (RecyclerView) findViewById(R.id.rc);
        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
        lin.setOrientation(OrientationHelper.VERTICAL);
        rc.setLayoutManager(lin);

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

    /**
     * 获取开锁记录
     */
    public void getOpenLockRecoder(String key) {
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = re.create(apiManager.class);
        Map<String,String>map=new HashMap<>();
        map.put("pageSize","50");
        map.put("currentPage","1");
        map.put("key",key);
        map.put("lockId",lockid);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        data3.clear();
        Call<String> call = manager.getopenLockRecoder(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG",body);
                openLockRecoderBean bean = gson.fromJson(body, new TypeToken<openLockRecoderBean>() {}.getType());
                openLockRecoderBean.DataBeanX data = bean.getData();

                List<openLockRecoderBean.DataBeanX.DataBean> data1 = data.getData();
                if (data1.size()>0){



                Iterator<openLockRecoderBean.DataBeanX.DataBean> iterator = data1.iterator();
                while (iterator.hasNext()){
                    openLockRecoderBean.DataBeanX.DataBean next = iterator.next();
                    data3.add(next);

                }
                adapter=new openLockRecodeAdapter(data3,openLockRecodeActivity.this);
                rc.setAdapter(adapter);
                re_reach.setAdapter(adapter);
                }else {
                    Toast.makeText(MainApplication.getInstence(), "没有数据", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 点击搜索
     */
    private void initSearch() {
    et_open_lock= (EditText) findViewById(R.id.et_open_lock);
        open_lock_frl= (FrameLayout) findViewById(R.id.open_lock_frl);
        open_lock_pasw= (TextView) findViewById(R.id.open_lock_pasw);
        open_lock_app= (TextView) findViewById(R.id.open_lock_app);
        open_lock_finger= (TextView) findViewById(R.id.open_lock_finger);
        open_lock_ic= (TextView) findViewById(R.id.open_lock_ic);
        open_lock_shengfenz= (TextView) findViewById(R.id.open_lock_shengfenz);
        open_molder_ll= (LinearLayout) findViewById(R.id.open_molder_ll);
        re_reach= (RecyclerView) findViewById(R.id.re_reach);
        tv_open_lock_cannel= (TextView) findViewById(R.id.tv_open_lock_cannel);


        et_open_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rc.setVisibility(View.GONE);//隐藏正常的recycleview
                open_molder_ll.setVisibility(View.VISIBLE);
                //显示搜索的布局
                //显示取消按钮
                //  open_lock_frl.setVisibility(View.VISIBLE);
                open_molder_ll.setVisibility(View.VISIBLE);
                re_reach.setVisibility(View.GONE);
                tv_open_lock_cannel.setVisibility(View.VISIBLE);
                // 每个模块的点击事件

                open_lock_pasw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 0
                        open_molder_ll.setVisibility(View.GONE);
                        re_reach.setVisibility(View.VISIBLE);
                        getOpenLockRecoder("0");
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);


                    }
                });

                open_lock_app.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  1
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("1");
                        re_reach.setVisibility(View.VISIBLE);
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);


                    }
                });

                open_lock_finger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 2
                        re_reach.setVisibility(View.VISIBLE);
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("2");
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);

                    }
                });

                open_lock_ic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    // 3
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("3");
                        re_reach.setVisibility(View.VISIBLE);
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);

                    }
                });

                open_lock_shengfenz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    // 4
                        open_molder_ll.setVisibility(View.GONE);
                        getOpenLockRecoder("4");
                        re_reach.setVisibility(View.VISIBLE);
                        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                        lin.setOrientation(OrientationHelper.VERTICAL);
                        re_reach.setLayoutManager(lin);

                    }
                });
                //显示结果   隐藏模块

            }

        });
        //点击取消
        tv_open_lock_cannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","取消走了");
                //隐藏搜索模块和搜索recycle
                open_molder_ll.setVisibility(View.GONE);
                re_reach.setVisibility(View.GONE);
                //显示正常的recycle
                rc.setVisibility(View.VISIBLE);
                getOpenLockRecoder("");
                LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
                lin.setOrientation(OrientationHelper.VERTICAL);
                rc.setLayoutManager(lin);
                tv_open_lock_cannel.setVisibility(View.GONE);
            }
        });

    }
}
