package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;
import com.hansion.h_ble.event.bleStateMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.widget.DatePicier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.utils.byteCunchu.getbyte;

/**
 * Created by lingyuan on 2018/6/5.
 */

public class addICCardOneStepActivity extends BaseActivity {
    ProgressDialog progressDialog;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "addICCardOneStepActivity";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    private byte[] token3;
    byte[] aesks;
    Toolbar toolbar;
    LinearLayout create_time;
    LinearLayout loseTime;
    private TextView currentDate, currentTime;
    ToggleButton tglSound;
    Button but_next;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
    byte[]bytezuqi=new byte[5];
    EditText electfrg_key_name;
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
        setContentView(R.layout.add_ic_one_activity_layout);

        getValue();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarvalue);
        toolbar.setTitleTextColor(Color.parseColor("#9D9D9D"));


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initlize();
        DatePicier.initDatePicker(currentDate, currentTime, addICCardOneStepActivity.this);
        EventBus.getDefault().register(this);
        mBleController = BleController.getInstance();

        //获取密钥
           aesks = getbyte("secretKeyBytes");
          allowbyt= byteCunchu.getbyte("allowbyt");



        initEvent();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(bleStateMessage event){
        hideProgressDialog();
        Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败,请重试", Toast.LENGTH_SHORT).show();
        Log.d("TAG","状态刷新");
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



    String addType;
    String toolbarvalue;
    byte[] bytestype;  //身份证  还是ic卡
    public void getValue() {
         addType = getIntent().getStringExtra("addType");
         toolbarvalue = getIntent().getStringExtra("toolbar");
        String str = getIntent().getStringExtra("byte");
        Log.d("TAG","value"+str);
        String sb=new String();
        for (int i = 0; i <str.length(); i++) {
            sb = str.replace("", "0");
        }
        str = sb.substring(0, sb.length() - 1);
        Log.d("TAG","拼接paw"+str);
        bytestype = StringUtils.toByteArray(str);

    }

    byte[] bytesstartTime;
    byte[] bytesstartendTime;
    private void initEvent() {

        Log.d("TAG","存储数据 aesks"+mBleController.bytesToHexString(aesks) + "\r\n");

        Log.d("TAG","存储数据 token3"+mBleController.bytesToHexString(token3) + "\r\n");
        //点击确定按钮
        but_next= (Button) findViewById(R.id.but_next);
        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initReceiveData();
                initConnectBle();


            }
        });
    }

    /**
     * 设置租期范围
     */
    private void setzuqiTime(){
        //事件分类
        if(StringUtils.isEmpty(createtime)&&StringUtils.isEmpty(endtime)){
            //永久  开始时间和结束时间相等

            //获取当前时间戳
            long timeStampSec = System.currentTimeMillis()/1000;
            String timestamp = String.format("%010d", timeStampSec);
            Log.d("TAG",""+timestamp);
            String string1 = Integer.toHexString((int) timeStampSec);
            Log.d("TAG","..."+string1);
            bytesstartTime = jiamiandjiemi.hexString2Bytes(string1);
            bytesstartendTime=bytesstartTime;
            for (int i = 0; i <  bytesstartTime.length; i++) {
                Log.d("TAG","."+ bytesstartTime[i]);
            }


        }else {


            String s = unixTime.dateToStampone(createtime);
            Log.d("TAG","开始时间戳"+s);
            String substring1 = s.substring(0, s.length() - 3);
            int startTime = Integer.parseInt(substring1);
            Log.d("TAG","开始时间"+ startTime);


            String string1 = Integer.toHexString(startTime);
            Log.d("TAG","..."+string1);
            bytesstartTime = jiamiandjiemi.hexString2Bytes(string1);
            for (int i = 0; i < bytesstartTime.length; i++) {
                Log.d("TAG",".."+bytesstartTime[i]);
            }

            String s1 = unixTime.dateToStampone(endtime);
            Log.d("TAG","结束时间戳"+s);
            String endtime = s1.substring(0, s1.length() - 3);
            int endtime1 = Integer.parseInt(endtime);
            Log.d("TAG","结束时间"+ endtime1 );

            String string11 = Integer.toHexString(endtime1);
            Log.d("TAG","..."+string1);
            bytesstartendTime = jiamiandjiemi.hexString2Bytes(string11);
            for (int i = 0; i < bytesstartendTime.length; i++) {
                Log.d("TAG",".."+bytesstartendTime[i]);
            }
        }
            //发送租期范围
            byte[] data15 = new byte[16];
            data15[0] = 0x04;
            data15[1] = 0x04;
            data15[2] = 0x08;
            data15[3] =  bytesstartTime[0];
            data15[4] =  bytesstartTime[1];
            data15[5] =  bytesstartTime[2];
            data15[6] =  bytesstartTime[3];
            data15[7] =  bytesstartendTime[0];
            data15[8] =  bytesstartendTime[1];
            data15[9] =  bytesstartendTime[2];
            data15[10] =  bytesstartendTime[3];
            data15[11] = token3[0];
            data15[12] = token3[1];
            data15[13] = token3[2];
            data15[14] = token3[3];
            byte[] encrypt11 = jiamiandjiemi.Encrypt(data15,aesks);
        byte[] decrypt = jiamiandjiemi.Decrypt(encrypt11, aesks);
            Log.d("TAG", "加密" + mBleController.bytesToHexString(encrypt11) + "\r\n");
        Log.d("TAG", "租期解密" + mBleController.bytesToHexString(decrypt) + "\r\n");
            mBleController.writeBuffer(encrypt11, new OnWriteCallback() {
                @Override
                public void onSuccess() {
                    Log.d("TAG", "发送成功");

                }

                @Override
                public void onFailed(int state) {

                }
            });





    }

    /**
     * 连接蓝牙
     */
    private void initConnectBle() {
        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {

        showProgressDialog("","正在连接蓝牙...");

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
                hideProgressDialog();

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
                        sendicorAoth();
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

    private void sendicorAoth() {
        //添加IC卡
      //  token3=byteCunchu.getbyte("token");
        if (token3.length>0){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    byte[] data15 = new byte[16];
                    data15[0] = 0x04;
                    data15[1] = 0x02;
                    data15[2] = 0x01;
                    data15[3] = bytestype[0];  //卡片类型ic 身份证
                    data15[4] = token3[0];
                    data15[5] = token3[1];
                    data15[6] = token3[2];
                    data15[7] = token3[3];

                    byte[] encrypt11 = jiamiandjiemi.Encrypt(data15, aesks);
                    Log.d("TAG", "加密" + mBleController.bytesToHexString(encrypt11) + "\r\n");

                    mBleController.writeBuffer(encrypt11, new OnWriteCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("TAG", "发送成功");

                        }

                        @Override
                        public void onFailed(int state) {

                        }
                    });
                }
            },500);
        }


    }



    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] aesks = getbyte("secretKeyBytes");

                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","解密addICCardOneStepActivity"+mBleController.bytesToHexString(decrypt) + "\r\n");
                if (decrypt[0]==04&&decrypt[2]==01&&decrypt[3]==00){
                    hideProgressDialog();
                    Toast.makeText(addICCardOneStepActivity.this,"请刷卡",Toast.LENGTH_LONG).show();

                }if (decrypt[0]==02&&decrypt[1]==01&&decrypt[2]==04){
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
                }
                if (decrypt[0]==04&&decrypt[1]==04&&decrypt[3]==00){
                //设置成功  提交数据到服务器
                    mBleController.closeBleConn();  //断掉蓝牙
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                  upData();
                }else if (decrypt[0]==04&&decrypt[3]==01){
                    Toast.makeText(addICCardOneStepActivity.this,"设置失败",Toast.LENGTH_LONG).show();
                }else if (decrypt[0]==04&&decrypt[3]==0xFF){
                    Toast.makeText(addICCardOneStepActivity.this,"无权限操作",Toast.LENGTH_LONG).show();
                }else if (decrypt[0]==04&&decrypt[1]==03&&decrypt[2]==06&&decrypt[3]==03&&decrypt[4]==01){
                    System.arraycopy(decrypt,0,bytezuqi,0,bytezuqi.length); //设置租期
                    setzuqiTime();//设置租期范围
                    Toast.makeText(addICCardOneStepActivity.this,"添加卡片成功",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    /**
     * 提交数据到服务器
     */
    String s2;
    String s4;
    String s1;
    String s3;
    String unlockType;
    private void upData() {

        //接收数据
        final String lockName = electfrg_key_name.getText().toString().trim();
        final String allow = MainApplication.getInstence().getAllow();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid = SharedUtils.getString("uid");
        Log.d("TAG","接收数据"+lockName);
        Log.d("TAG","接收数据"+allow);
        Log.d("TAG","接收数据"+lockid );

        s1 = currentTime.getText().toString().trim().replaceAll(" ", "-");

        Log.d("TAG","s1"+ s1);
        s2 = s1.replaceAll(":", "-");

        Log.d("TAG","s2"+ s2);
        String trim = s2.replaceAll("-", "").trim();
        Log.d("TAG","s2Time"+ trim );

        s3 =   endtime.replaceAll(" ", "-");
        s4 = s3.replaceAll(":", "-");
        unlockType="0";

        Map<String,String> map= new HashMap<>();

                //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}
        if (StringUtils.isEmpty(s2)&&StringUtils.isEmpty(s4)){

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");// HH:mm:ss
            //获取当前时间
            Date date = new Date(System.currentTimeMillis());
            // time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));

            Log.d("TAG","当前时间"+simpleDateFormat.format(date));
            s2=simpleDateFormat.format(date);
            s4=simpleDateFormat.format(date);
            unlockType="1";
        }else {


        map.put("lockId",lockid);
        map.put("unlockName",lockName);
        map.put("unlockFlag","");
        map.put("allow",allow);
        map.put("addPerson",uid);
        map.put("forWay","");
        map.put("startTime",s2);
        map.put("endTime",s4);
        map.put("addType",addType);
        map.put("unlockType", unlockType);
        Gson gson=new Gson();
        String value = gson.toJson(map);
        Log.d("TAG","上传数据"+value);

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

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        }
    }

    private void initlize() {
        currentTime = (TextView) findViewById(R.id.electfrg_effect_time);
        create_time = (LinearLayout) findViewById(R.id.create_time);
        currentDate = (TextView) findViewById(R.id.electfrg_lose_time);
        loseTime = (LinearLayout) findViewById(R.id.lose_time);
        tglSound= (ToggleButton) findViewById(R.id.tglSound);

        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(currentTime.getText().toString());

            }
        });

        loseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePicier.getCustomDatePicker1().show(currentTime.getText().toString());

            }
        });
        tglSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    loseTime.setVisibility(View.INVISIBLE);
                    create_time.setVisibility(View.INVISIBLE);
                  //  unlockType="1";

                }else {
                    loseTime.setVisibility(View.VISIBLE);
                    create_time.setVisibility(View.VISIBLE);
                  //  unlockType="0";
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mBleController!=null){


        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
        mBleController.closeBleConn();

        }
    }

    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime().toString().trim();
        Log.d("TAG","event1"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime().toString().trim();
        Log.d("TAG","event"+ createtime);


    }

    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(addICCardOneStepActivity.this, title, message, true, false);
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
