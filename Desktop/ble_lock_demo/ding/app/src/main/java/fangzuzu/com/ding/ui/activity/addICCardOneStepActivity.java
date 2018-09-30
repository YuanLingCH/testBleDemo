package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;
import com.hansion.h_ble.callback.ScanCallback;
import com.hansion.h_ble.event.bleStateMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    RelativeLayout re_type;
String adminUserId;
    String uid;
   TextView tv_toolbar;
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
        EventBus.getDefault().register(this);
        getValue();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.parseColor("#9D9D9D"));
        adminUserId = SharedUtils.getString("adminUserId");
       uid = SharedUtils.getString("uid");
initHide();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initlize();


        mBleController = BleController.getInstance();

        //获取密钥
           aesks = getbyte("secretKeyBytes");
          allowbyt= byteCunchu.getbyte("allowbyt");


        DatePicier.initDatePicker(currentDate, currentTime, addICCardOneStepActivity.this);
        initEvent();

    }

    private void initHide() {
        if (!uid.equals(adminUserId)){
            re_type=(RelativeLayout) findViewById(R.id.re_type);
            re_type.setVisibility(View.GONE);
        }

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
    String lockNumber;
    public void getValue() {
         addType = getIntent().getStringExtra("addType");
         toolbarvalue = getIntent().getStringExtra("toolbar");
        String str = getIntent().getStringExtra("byte");
        lockNumber = MainApplication.getInstence().getMac();
        Log.d("TAG","value"+str);
        String sb=new String();
        for (int i = 0; i <str.length(); i++) {
            sb = str.replace("", "0");
        }
        str = sb.substring(0, sb.length() - 1);
        Log.d("TAG","拼接paw"+str);
        bytestype = StringUtils.toByteArray(str);

    }
 String lockName;
    byte[] bytesstartTime;
    byte[] bytesstartendTime;
    private void initEvent() {
        electfrg_key_name=(EditText) findViewById(R.id.electfrg_key_name);
        Log.d("TAG","存储数据 aesks"+mBleController.bytesToHexString(aesks) + "\r\n");

        Log.d("TAG","存储数据 token3"+mBleController.bytesToHexString(token3) + "\r\n");
        //点击确定按钮
        but_next= (Button) findViewById(R.id.but_next);
        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockName = electfrg_key_name.getText().toString().trim();



                String time = currentTime.getText().toString().trim();
                String s = unixTime.dateToStampone(time);
                Log.d("TAG","开始时间戳"+s);
                String substring1 = s.substring(0, s.length() - 3);
                int startTime = Integer.parseInt(substring1);
                Log.d("TAG","开始时间"+ startTime);

                String endtime = currentDate.getText().toString().trim();
                String send = unixTime.dateToStampone(endtime);
                Log.d("TAG","结束时间戳"+send);
                String substring1end = send.substring(0, send.length() - 3);
                int endTime = Integer.parseInt(substring1end);
                Log.d("TAG","结束时间"+ endTime);
                if (!StringUtils.isEmpty(lockName)){
                    if ( tglSound.isChecked()){
                        Log.d("TAG","永久走了");
                        addICAndshengfenzheng();
                    }
                    else {


                        if (StringUtils.isEmpty(endtime)) {
                            Toast.makeText(MainApplication.getInstence(), "请输入失效时间", Toast.LENGTH_SHORT).show();
                        }else if (startTime<endTime&&startTime!=endTime){
                            addICAndshengfenzheng();

                        }else {
                            Toast.makeText(MainApplication.getInstence(),"失效时间不能小于生效时间，并且2个时间不能相同",Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Toast.makeText(MainApplication.getInstence(),"请给IC卡命名",Toast.LENGTH_LONG).show();
                }






            }
        });
    }

  public  void   addICAndshengfenzheng(){
      showProgressDialog("","正在连接蓝牙...");
      initReceiveData();
      initConnectBle();
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

            String trim = currentTime.getText().toString().trim();
            String s = unixTime.dateToStampone(trim);
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
    String  strbiaozhi;
    List bledata=new ArrayList();
    private void initConnectBle() {
        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            if (!bledata.contains(lockNumber)){
                mBleController.scanBleone(0, new ScanCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","蓝牙扫描结束");
                       runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bledata.size()==0){
                                    hideProgressDialog();
                                    mBleController.closeBleConn();
                                    Toast.makeText(MainApplication.getInstence(), "没有扫描到锁，请重新扫描", Toast.LENGTH_SHORT).show();
                                }else{
                                    if (strbiaozhi.equals("02")){

                                        connect();
                                    }

                                }

                            }
                        });
                        //

                    }

                    @Override
                    public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {

                        String address = device.getAddress();
                        if (address.equals(lockNumber)){
                            if (!bledata.contains(lockNumber)){
                                bledata.add(address);
                                Log.d("TAG","蓝牙扫描"+address);
                                String string1 = mBleController.bytesToHexString(scanRecord);
                                Log.d("TAG", "蓝牙设备" + string1);
                                String str = string1.replaceAll(" ", "").trim();
                                if (str.indexOf("5453")!=-1){
                                    int length = str.length();
                                    String[] split = str.split("5453");
                                    Log.d("TAG","切割后面的"+split[1]);
                                    strbiaozhi= split[1].substring(14, 16);
                                    if (!strbiaozhi.equals("02")){
                                        hideProgressDialog();
                                        Toast.makeText(MainApplication.getInstence(), "你的锁已被初始化,请联系管理员", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                            }

                        }

                    }
                });
            }else  {
                Log.d("TAG","没扫描");
                if (strbiaozhi.equals("00")||strbiaozhi.equals("01")){
                    Log.d("TAG","标准"+strbiaozhi);
                    Toast.makeText(MainApplication.getInstence(), "你的锁已被初始化,请联系管理员", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    return;
                }else {
                    Log.d("TAG","连接");

                    connect();
                }

            }


        }

    }


    public void connect(){

        mBleController.connect(0, lockNumber, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                jiaoyan();
            }

            @Override
            public void onConnFailed() {

                    Log.d("TAG","蓝牙状态码不对");
                Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败", Toast.LENGTH_SHORT).show();
                    mBleController.closeBleConn();
                    hideProgressDialog();


            }
        });
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
                sendicorAoth();
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

                    byte[] decrypt = jiamiandjiemi.Decrypt(encrypt11, aesks);
                    Log.d("TAG", "解密类型" + mBleController.bytesToHexString(decrypt) + "\r\n");

                    mBleController.writeBuffer(encrypt11, new OnWriteCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("TAG", "身份证发送成功");

                        }

                        @Override
                        public void onFailed(int state) {
                            hideProgressDialog();
                        }
                    });
                }
            },500);
        }


    }


    byte []idCard; //ic卡的id
    byte[]pingjieLength=new byte[1];
    byte [] changdu;
    byte[] cunzai=new byte[1]; //用户已经存在
    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] aesks = getbyte("secretKeyBytes");

                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","解密addICCardOneStepActivity"+mBleController.bytesToHexString(decrypt) + "\r\n");
                if (decrypt[0]==04&&decrypt[1]==02&&decrypt[2]==01&&decrypt[3]==00){
                    hideProgressDialog();
                    Toast.makeText(addICCardOneStepActivity.this,"请刷卡片",Toast.LENGTH_LONG).show();
                     //   dialog("请刷卡片");
                } if (decrypt[0]==02&&decrypt[1]==01&&decrypt[2]==04){
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
                if (decrypt[0]==04&&decrypt[1]==04&&decrypt[2]==01&&decrypt[3]==00){
                //设置成功  提交数据到服务器

                    mBleController.closeBleConn();  //断掉蓝牙
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    String s = mBleController.bytesToHexString(idCard);
                    Log.d("TAG","截取结果s"+s);

                        dialog("添加卡片成功");
                        upData();
                }
                    //合并身份证和ic卡
                if (decrypt[0]==04&&decrypt[1]==03&&decrypt[3]!=02&&decrypt[3]!=03&&decrypt[3]!=05&&decrypt[15]==00){  //第2条数据回来

                            Log.d("TAG", "身份证租期走了" );
                            if (decrypt[4]==-16){
                                dialog("用户已存在");
                                mBleController.closeBleConn();  //断掉蓝牙
                                mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                            }else {
                                System.arraycopy(decrypt, 2, pingjieLength, 0, pingjieLength.length); //设置租期
                                changdu=new byte[pingjieLength[0]];
                                System.arraycopy(decrypt, 3, changdu, 0, changdu.length); //设置
                                setzuqiTime();//设置租期范围
                            }



                }
                if (decrypt[0]==04&&decrypt[3]==01){
                    Toast.makeText(addICCardOneStepActivity.this,"设置失败",Toast.LENGTH_LONG).show();
                } if (decrypt[0]==04&&decrypt[3]==0xFF){
                    mBleController.closeBleConn();
                    Toast.makeText(addICCardOneStepActivity.this,"无权限操作",Toast.LENGTH_LONG).show();
                }   if (decrypt[0]==04&&decrypt[1]==03&&decrypt[3]==03){  //Ic卡  身份证  第一条数据
                    System.arraycopy(decrypt,0,bytezuqi,0,bytezuqi.length); //设置租期
                    System.arraycopy(decrypt, 4, cunzai, 0, cunzai.length);
                    setzuqiTime();//设置租期范围
                 //   Toast.makeText(addICCardOneStepActivity.this,"添加卡片成功",Toast.LENGTH_LONG).show();
                   byte []idLength=new byte[1];
                    for (int i = 0; i < decrypt.length; i++) {
                        idLength[0]=decrypt[2];

                    }
                    Log.d("TAG","转换结果idLength[0]"+idLength[0]);
                   idCard=new byte[idLength[0]-1];   //定义长度
                    System.arraycopy(decrypt,4,idCard,0,idCard.length);
                    for (int i = 0; i < idCard.length; i++) {
                        Log.d("TAG","转换结果idicard"+idCard[i]);

                    }
                    String s = mBleController.bytesToHexString(idCard);
                    if (idCard[0]==-16){
                        dialog("用户已存在");
                        mBleController.closeBleConn();  //断掉蓝牙
                        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    }else if (decrypt[15]==0x00){
                        setzuqiTime();    // ic卡
                    }
                } /*if (decrypt[0]==04&&decrypt[1]==03&&decrypt[3]==04&&decrypt[15]==01) {
                    Log.d("TAG", "身份证走了" );
                    System.arraycopy(decrypt, 0, bytezuqi, 0, bytezuqi.length); //设置租期
                    System.arraycopy(decrypt, 4, cunzai, 0, cunzai.length);
                    for (int i = 0; i < cunzai.length; i++) {
                        Log.d("TAG", "截取身份证" + cunzai[i]);
                    }

                    byte[] idLength = new byte[1];
                    for (int i = 0; i < decrypt.length; i++) {
                        idLength[0] = decrypt[2];

                    }
                    Log.d("TAG", "转换结果idLength[0]" + idLength[0]);
                    idCard = new byte[idLength[0] - 1];   //定义长度
                    System.arraycopy(decrypt, 4, idCard, 0, idCard.length);
                    for (int i = 0; i < idCard.length; i++) {
                        Log.d("TAG", "转换结果idicard" + idCard[i]);

                    }


                }*/  if (decrypt[0]==04&&decrypt[3]==0xFE){
                    Toast.makeText(addICCardOneStepActivity.this,"操作超时，请重试",Toast.LENGTH_LONG).show();
                    mBleController.closeBleConn();
                }










              //--------------------------------------------------------------------------------------

                /*if (decrypt[0]==04&&decrypt[1]==02&&decrypt[2]==01&&decrypt[3]==00){
                    hideProgressDialog();
                    Toast.makeText(addICCardOneStepActivity.this,"请刷卡片",Toast.LENGTH_LONG).show();
                    //   dialog("请刷卡片");
                } if (decrypt[0]==02&&decrypt[1]==01&&decrypt[2]==04){
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
                if (decrypt[0]==04&&decrypt[1]==04&&decrypt[2]==01&&decrypt[3]==00){
                    //设置成功  提交数据到服务器

                    mBleController.closeBleConn();  //断掉蓝牙
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    String s = mBleController.bytesToHexString(idCard);
                    Log.d("TAG","截取结果s"+s);

                    dialog("添加卡片成功");
                    upData();
                }

                if (decrypt[0]==04&&decrypt[1]==03&&decrypt[15]==00){  //第2条数据回来
                    if (bytestype[0]==04){
                        Log.d("TAG", "身份证租期走了" );
                        if (decrypt[4]==-16){
                            dialog("用户已存在");
                            mBleController.closeBleConn();  //断掉蓝牙
                            mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                        }else {
                            System.arraycopy(decrypt, 2, pingjieLength, 0, pingjieLength.length); //设置租期
                            changdu=new byte[pingjieLength[0]];
                            System.arraycopy(decrypt, 3, changdu, 0, changdu.length); //设置
                            setzuqiTime();//设置租期范围
                        }

                    }




                }
                if (decrypt[0]==04&&decrypt[3]==01){
                    Toast.makeText(addICCardOneStepActivity.this,"设置失败",Toast.LENGTH_LONG).show();


                } if (decrypt[0]==04&&decrypt[3]==0xFF){
                    mBleController.closeBleConn();
                    Toast.makeText(addICCardOneStepActivity.this,"无权限操作",Toast.LENGTH_LONG).show();
                }   if (decrypt[0]==04&&decrypt[1]==03&&decrypt[3]==03){  //Ic卡
                    System.arraycopy(decrypt,0,bytezuqi,0,bytezuqi.length); //设置租期
                    System.arraycopy(decrypt, 4, cunzai, 0, cunzai.length);
                    setzuqiTime();//设置租期范围
                    //   Toast.makeText(addICCardOneStepActivity.this,"添加卡片成功",Toast.LENGTH_LONG).show();
                    byte []idLength=new byte[1];
                    for (int i = 0; i < decrypt.length; i++) {
                        idLength[0]=decrypt[2];

                    }
                    Log.d("TAG","转换结果idLength[0]"+idLength[0]);
                    idCard=new byte[idLength[0]-1];   //定义长度
                    System.arraycopy(decrypt,4,idCard,0,idCard.length);
                    for (int i = 0; i < idCard.length; i++) {
                        Log.d("TAG","转换结果idicard"+idCard[i]);

                    }
                    String s = mBleController.bytesToHexString(idCard);
                    if (idCard[0]==-16){
                        dialog("用户已存在");
                        mBleController.closeBleConn();  //断掉蓝牙
                        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    }else {
                        setzuqiTime();
                    }
                } if (decrypt[0]==04&&decrypt[1]==03&&decrypt[3]==04&&decrypt[15]==01) {
                    Log.d("TAG", "身份证走了" );
                    System.arraycopy(decrypt, 0, bytezuqi, 0, bytezuqi.length); //设置租期
                    System.arraycopy(decrypt, 4, cunzai, 0, cunzai.length);
                    for (int i = 0; i < cunzai.length; i++) {
                        Log.d("TAG", "截取身份证" + cunzai[i]);
                    }
                  *//*  if (cunzai[0]==-16){
                        setzuqiTime();
                    }*//*
                    // setzuqiTime();//设置租期范围
                    //   Toast.makeText(addICCardOneStepActivity.this,"添加卡片成功",Toast.LENGTH_LONG).show();
                    byte[] idLength = new byte[1];
                    for (int i = 0; i < decrypt.length; i++) {
                        idLength[0] = decrypt[2];

                    }
                    Log.d("TAG", "转换结果idLength[0]" + idLength[0]);
                    idCard = new byte[idLength[0] - 1];   //定义长度
                    System.arraycopy(decrypt, 4, idCard, 0, idCard.length);
                    for (int i = 0; i < idCard.length; i++) {
                        Log.d("TAG", "转换结果idicard" + idCard[i]);

                    }


                }  if (decrypt[0]==04&&decrypt[3]==0xFE){
                    Toast.makeText(addICCardOneStepActivity.this,"操作超时，请重试",Toast.LENGTH_LONG).show();
                    mBleController.closeBleConn();
                }
*/















            }
        });
    }




    private void dialog(String conncet) {
        View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_deviceslayut, null);
        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
        tv.setText(conncet);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(14);
        tv.setGravity(Gravity.CENTER);
        TextView tv_de_me= (TextView)viewDialog.findViewById(R.id.tv_de_me);
      LinearLayout  ll_duihuakuang=(LinearLayout) viewDialog.findViewById(R.id.ll_duihuakuang);
        final AlertDialog  dialogD = new AlertDialog.Builder(addICCardOneStepActivity.this)
                .setView(viewDialog)
                .create();
      // dialogD.setCanceledOnTouchOutside(false);
        Window window=dialogD.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogD.show();
        WindowManager.LayoutParams params = dialogD.getWindow().getAttributes();
        WindowManager manager=getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        android.view.WindowManager.LayoutParams p = dialogD.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width= (int) (defaultDisplay.getWidth()*0.85);
        dialogD.getWindow().setAttributes(p);     //设置生效
        ll_duihuakuang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "点击了取消按钮" );
                dialogD.dismiss();

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
        StringBuffer icID = new StringBuffer();
        for (int i = 0; i < idCard.length; i++) {
            Log.d("TAG", "管理员" + idCard[i]);
            icID.append((idCard[i]));
        }
            String s = mBleController.bytesToHexString(idCard).trim().toLowerCase();


        String s8=null;
        if (changdu!=null){
            String s7= mBleController.bytesToHexString(changdu).trim().toLowerCase();
            s8 = s7.replaceAll(" ", "");
        }

        String idIC = s.replaceAll(" ", "");
        String s9=null;
        if (StringUtils.isEmpty(s8)){
            s9= idIC;
        }else {
         s9= idIC + s8;
        }

        Log.d("TAG", "转化数据" + idIC);
        final String allow = MainApplication.getInstence().getAllow();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid = SharedUtils.getString("uid");
        Log.d("TAG", "接收数据" + lockName);
        Log.d("TAG", "接收数据" + allow);
        Log.d("TAG", "接收数据" + lockid);


            if (!StringUtils.isEmpty(endtime)){
                s3 = endtime.replaceAll(" ", "-");
                s4 = s3.replaceAll(":", "-");
                unlockType = "0";
                s1 = currentTime.getText().toString().trim().replaceAll(" ", "-");
                Log.d("TAG", "s1" + s1);
                s2 = s1.replaceAll(":", "-");
                Log.d("TAG", "s2" + s2);
                String trim = s2.replaceAll("-", "").trim();
                Log.d("TAG", "s2Time" + trim);

            }else {
                if ( (StringUtils.isEmpty(s2) && StringUtils.isEmpty(s4))){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");// HH:mm:ss
            //获取当前时间
            Date date = new Date(System.currentTimeMillis());
            // time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));
            Log.d("TAG", "当前时间" + simpleDateFormat.format(date));
            s2 = simpleDateFormat.format(date);
            s4 = simpleDateFormat.format(date);
            unlockType = "1";
            }
            }
            Map<String, String> map = new HashMap<>();
            map.put("lockId", lockid);
        map.put("unlockName", lockName);
        map.put("unlockFlag", s9);
        map.put("allow", allow);
        map.put("addPerson", uid);
        map.put("forWay", "");
        map.put("startTime", s2);
        map.put("endTime", s4);
        map.put("addType", addType);
        map.put("unlockType", unlockType);
        Gson gson = new Gson();
        String value = gson.toJson(map);
        Log.d("TAG", "上传数据" + value);

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

    private void initlize() {
        currentTime = (TextView) findViewById(R.id.electfrg_effect_time);
        create_time = (LinearLayout) findViewById(R.id.create_time);
        currentDate = (TextView) findViewById(R.id.electfrg_lose_time);
        loseTime = (LinearLayout) findViewById(R.id.lose_time);
        tglSound= (ToggleButton) findViewById(R.id.tglSound);
        tv_toolbar=(TextView)findViewById(R.id.tv_toolbar);
        tv_toolbar.setText(toolbarvalue);

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
