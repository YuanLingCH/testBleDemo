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
import android.widget.ImageView;
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
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.openLockRecodeAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.openLockRecoderBean;
import fangzuzu.com.ding.bean.xuzhuBean;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
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
    LinearLayout ll_nodata,ll_no_noe;
    FrameLayout fr_no_two;
    ImageView iv_no_data;
    TextView tv_no_data;
    String uid;
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
        uid= SharedUtils.getString("uid");
        getIDData();
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
                if (token3!=null){
                byte[]data5=new byte[16];

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
                sendData();
            }
            @Override
            public void onFailed(int state) {

            }
        });

    }
    //  0 ic   1身份证   2指纹     2：获取指纹开锁记录；3：获取用户IC卡开锁记录；4：获取用户身份证开锁记录；
    List ziwenData=new ArrayList();
    List ICData=new ArrayList();
    List shenfenzData=new ArrayList();
    int numbler=0;
    byte []byteType=new byte[1];
    public void  sendData(){
        Timer timer=new Timer();
        if (ziwenData.size()>0) {
            Log.d("TAG", "指纹大小" + ziwenData.size());
            byteType[0] = 0x02;
            Log.d("TAG", "指纹走了");
            for (int i = 0; i < ziwenData.size(); i++) {
                sendopenLockrecode((String) ziwenData.get(i), byteType);
                Log.d("TAG", "ic卡Id" + ziwenData.get(i));
            }
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ICData.size()>0) {
                    Log.d("TAG", "ic卡走了");
                    byteType[0]=0x03;
                    for (int i = 0; i < ICData.size(); i++) {
                        sendopenLockrecode((String) ICData.get(i),byteType);

                    }
                }
            }
        },500);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (shenfenzData.size()>0) {
                    Log.d("TAG", "身份证走了");
                    byteType[0]=0x04;
                    for (int i = 0; i < shenfenzData.size(); i++) {
                        sendopenLockrecode((String) shenfenzData.get(i),byteType);

                    }
                }
            }
        },500);

    }



    /**
     *  发送蓝牙数据获取开锁记录
     */
    private void sendopenLockrecode(final String unlockFlag, final byte[] byteType ) {

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //  7dc2f302
                  //  String str="0700";
                    if (!StringUtils.isEmpty(unlockFlag)){


                    byte[] bytes = StringUtils.toByteArray(unlockFlag);
                    byte[]length=new byte[1];
                    length[0]= (byte) (bytes.length+1);
                    byte []head=new byte[4];
                    head[0]=0x05;
                    head[1]=0x01;
                    head[2]=length[0];
                    head[3]=byteType[0];  //类型
                    byte[] byteOne = byteCunchu.unitByteArray(head, bytes);
                    byte[] byteTwo = byteCunchu.unitByteArray(byteOne, token3);
                    byte[]data16=new byte[16];
                    for (int i = 0; i < byteTwo.length; i++) {
                        data16[i]= byteTwo[i];
                    }

                    byte[] encrypt10a = jiamiandjiemi.Encrypt(data16, aesks);

                    byte[] decrypt = jiamiandjiemi.Decrypt(encrypt10a, aesks);
                    Log.d("TAG","发送数据"+mBleController.bytesToHexString(decrypt) + "\r\n");
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
             //   Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10a) + "\r\n");

                mBleController.writeBuffer(encrypt10a, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                    //    Log.d("TAG","发送成功");

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
            //    Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10a) + "\r\n");

                mBleController.writeBuffer(encrypt10a, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                     //   Log.d("TAG","发送成功");
                    }
                    @Override
                    public void onFailed(int state) {
                    }
                });

            }
        },500);



    }
    byte [] bleToAppDataOne=new byte[1];
    byte [] bleToAppDataTow=new byte[1];
    byte [] bleToAppDataThree=new byte[1];
    byte [] bleToAppDataFore=new byte[1];
    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","解密获取开锁记录"+mBleController.bytesToHexString(decrypt) + "\r\n");
                if (value.length>0){


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
                if (decrypt[0]==05&&decrypt[1]==01&&decrypt[3]==00){
                    sendopenLockrecodeone();  //第一次确认
                }
                if (decrypt[0]==05&&decrypt[1]==03&&decrypt[2]==05) {
                    sendopenLockrecodetow();  //第二次确认 //每回来一条确认一次
                    // 蓝牙返回开锁时间
                    System.arraycopy(decrypt, 3, bleToAppDataOne, 0, bleToAppDataOne.length);
                    System.arraycopy(decrypt, 4, bleToAppDataTow, 0, bleToAppDataTow.length);
                    System.arraycopy(decrypt, 5, bleToAppDataThree, 0, bleToAppDataThree.length);
                    System.arraycopy(decrypt, 6, bleToAppDataFore, 0, bleToAppDataFore.length);
                    String s1 = mBleController.bytesToHexString(bleToAppDataOne).toString().trim().replaceAll(" ", "");
                    String s2 = mBleController.bytesToHexString(bleToAppDataTow).toString().trim().replaceAll(" ", "");
                    String s3 = mBleController.bytesToHexString(bleToAppDataThree).toString().trim().replaceAll(" ", "");
                    String s4 = mBleController.bytesToHexString(bleToAppDataFore).toString().trim().replaceAll(" ", "");
                    // String s1 = CharAtreverse(s);
                    String time = s4 + s3 + s2 + s1;
                    long x = Integer.parseInt(time, 16);
                    String s = unixTime. stampToTime(x);
                    Log.d("TAG", "开锁时间撮" + x);
                    Log.d("TAG", "开锁时间" + s);
                    //  0 ic   1身份证   2指纹     2：获取指纹开锁记录；3：获取用户IC卡开锁记录；4：获取用户身份证开锁记录；

                    if (byteType[0]==0x02){
                      upDataOpenRecoder(s,"2");
                    }else if (byteType[0]==0x03){
                      upDataOpenRecoder(s,"3");
                    }/*else if (byteType[0]==0x04){
                        upDataOpenRecoder(s,"4");
                    }*/
                    //   upDataOpenRecoder(s,"");

                }
                }
                }
        });
    }

    /**
     * 删除开锁记录
     */
    public  void deleteOpenRecord(){
         // id长度大于14 就要分包 7个字节
                byte []head=new byte[4];
                head[0]=0x05;
                head[1]=0x04;
                head[2]=0x05;  //长度
                head[3]=0x05;   //类型

            }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleController!=null){
            mBleController.closeBleConn();
            mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
        }
    }

    private void initlize() {
        data3=new ArrayList();
        ll_no_noe=(LinearLayout) findViewById(R.id.ll_no_one);
        fr_no_two=(FrameLayout) findViewById(R.id.fr_on_two);
        ll_nodata=(LinearLayout) findViewById(R.id.ll_nodata);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
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
        //adminUserId
        String adminUserId = SharedUtils.getString("adminUserId");
        String lockuesr;
        if (adminUserId.equals(uid)){
            lockuesr="";
        }else {
            lockuesr=uid;
        }
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = re.create(apiManager.class);
        Map<String,String>map=new HashMap<>();
        map.put("pageSize","100");
        map.put("currentPage","1");
        map.put("key",key);
        map.put("lockId",lockid);
        map.put("userId",lockuesr);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        data3.clear();
        Call<String> call = manager.getopenLockRecoder(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)) {


                    Log.d("TAG", body);
                    openLockRecoderBean bean = gson.fromJson(body, new TypeToken<openLockRecoderBean>() {
                    }.getType());

                    int code = bean.getCode();
                    if (code == 1001) {
                    openLockRecoderBean.DataBeanX data = bean.getData();
                    List<openLockRecoderBean.DataBeanX.DataBean> data1 = data.getData();
                    if (data1.size() == 0) {
                        ll_no_noe.setVisibility(View.GONE);
                        fr_no_two.setVisibility(View.GONE);
                        ll_nodata.setVisibility(View.VISIBLE);
                        iv_no_data.setImageResource(R.mipmap.no_open_door);
                        tv_no_data.setText("暂无开锁记录");

                    } else if (data1.size() > 0) {
                        ll_nodata.setVisibility(View.GONE);
                        ll_no_noe.setVisibility(View.VISIBLE);
                        fr_no_two.setVisibility(View.VISIBLE);

                        Iterator<openLockRecoderBean.DataBeanX.DataBean> iterator = data1.iterator();
                        while (iterator.hasNext()) {
                            openLockRecoderBean.DataBeanX.DataBean next = iterator.next();
                            data3.add(next);

                        }
                        adapter = new openLockRecodeAdapter(data3, openLockRecodeActivity.this);
                        rc.setAdapter(adapter);
                        re_reach.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainApplication.getInstence(), "没有数据", Toast.LENGTH_SHORT).show();
                    }
                }
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

    /**
     * 得到ID
     */
    List< xuzhuBean.DataBean > idData;
    List typeData;

    public void getIDData() {
        idData=new ArrayList();
        typeData=new ArrayList();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())

                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Call<String> call = manager.getxuzhuID(lockid, uid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (StringUtils.isEmpty(body)){

                }else {
                    Log.d("TAGT", "续租id" + body);
                    Gson gson = new Gson();
                    xuzhuBean bean = gson.fromJson(body, new TypeToken<xuzhuBean>() {}.getType());
                    int code = bean.getCode();
                    if (code==1001){
                        List<xuzhuBean.DataBean> data = bean.getData();
                        Iterator<xuzhuBean.DataBean> iterator = data.iterator();
                        idData.clear();
                        typeData.clear();
                        while (iterator.hasNext()){
                            xuzhuBean.DataBean next = iterator.next();
                            String addType = next.getAddType()+"";   //  0 ic   1省份在   2指纹
                            String unlockFlag = next.getUnlockFlag();   //id
                             idData.add(next);
                           //  typeData.add(addType);

                            if (addType.equals("0")){   //  0 ic   1身份证   2指纹     2：获取指纹开锁记录；3：获取用户IC卡开锁记录；4：获取用户身份证开锁记录；
                                ICData.add(unlockFlag);

                            }else if (addType.equals("1")){
                                shenfenzData.add(unlockFlag);

                            }else if (addType.equals("2")){

                                ziwenData.add(unlockFlag);
                                Log.d("TAGT", "指纹走了");

                            }



                        }
                        if (idData.size()>0){   // 有id 才连接蓝牙
                            Log.d("TAG","蓝牙方法走了");
                            initReceiveData();
                            initConnectBle();
                        }


                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    /**
     * 上传开锁记录
     *
     */
    private void upDataOpenRecoder(String time,String unLocktype) {



        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Map<String,Map<String,String>>map=new HashMap<>();
        Map<String,String>map1=new HashMap<>();
        map1.put("unlockPwd","");
        map1.put("unlockTime",time);
        map1.put("unlockType",unLocktype);
        map1.put("lockId",lockid);
        map1.put("userId",uid);
        map.put("operatinList",map1);
        Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG","拼接json上传开锁记录"+s);
        Call<String> call = manager.upDataOpenlockRecoder(s);
      call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int code = response.code();
                Log.d("TAG","上传"+code);
                String body = response.body();
                Log.d("TAG","上传"+body);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG","错误"+t.toString());

            }
        });
    }
}
