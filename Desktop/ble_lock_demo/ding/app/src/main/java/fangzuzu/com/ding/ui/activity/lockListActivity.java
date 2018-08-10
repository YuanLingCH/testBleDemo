package fangzuzu.com.ding.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.lockListAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.bean.userLockBean;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.callback.ConnectCallBackHandler;
import fangzuzu.com.ding.callback.MqttCallbackHandler;
import fangzuzu.com.ding.callback.SubcribeCallBackHandler;
import fangzuzu.com.ding.event.MessageEvent;
import fangzuzu.com.ding.utils.HandleBackUtil;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/6/27.
 */

public class lockListActivity extends BaseActivity {
    private static MqttAndroidClient client;
    RecyclerView lock_list_rc;
    String partid;
    String uid;
    lockListAdapter adapter;
    List data3;
    ImageView iv;
    LinearLayout ll;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    SwipeRefreshLayout swipe_refresh;
    public  int REQUEST_ACCESS_COARSE_LOCATION=1;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    TextView tv_lock_list,tv_lock_listone;
    boolean isKitKat = false;
    public  String clientID;
    public String serverIP="www.fzhuzhu.cn";
    public String port="1883";
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "lockListActivity";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    private byte[] token3;
    byte[] aesks;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
    TextView tv_toolbar;
    List dataPart=new ArrayList();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  * 沉浸模式
        *
        * api等级>=19
     */

     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
         window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
         isKitKat = true;
        }

        setContentView(R.layout.lock_list_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        tv_toolbar= (TextView) findViewById(R.id.tv_toolbar);
        setStatusBar();

        MainApplication app = (MainApplication )getApplication();
         EventBus.getDefault().register(this);
        /**获取client对象*/
        partid = app.getPartid();
   // uid = app.getUid();
     uid= SharedUtils.getString("uid");
        clientID="az"+uid;

      //  Log.d("TAG",partid);
  // Log.d("TAG",""uid);
        iv= (ImageView) findViewById(R.id.iv);
        ll= (LinearLayout) findViewById(R.id.ll);
        tv_lock_list= (TextView) findViewById(R.id.tv_lock_list);
        tv_lock_listone= (TextView) findViewById(R.id.tv_lock_listone);
        swipe_refresh= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        initlize();
        getUserLockList();

        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(lockListActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
//向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(lockListActivity.this,"打开权限才能用哦", Toast.LENGTH_SHORT).show();
                }
            }
        }
           // initauthor();
        Log.d("TAG","  "+clientID);
        mBleController = BleController.getInstance().init(lockListActivity.this);


        Log.d("TAG","密钥"+mBleController.bytesToHexString(aesks) + "\r\n");
        Log.d("TAG","锁标识"+mBleController.bytesToHexString(allowbyt) + "\r\n");
        deleteLock();
        startConnect(clientID,serverIP,port);
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




    @Override
    protected void onStart() {
        super.onStart();

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
                    upDataDeletLock(id,p);//删除服务器数据
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    mBleController.closeBleConn();
                    hideProgressDialog();
                }
            }
        });

    }

    /**
     * 删除蓝牙管理员
     * @param id
     * @param p
     */
    private void upDataDeletLock(String id, final int p) {
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.delctLock(id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Gson gson=new Gson();
                msg m = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = m.getCode();
                if (code==1001){
                    Log.d("TAG","删除成功");
                    Toast.makeText(lockListActivity.this,"删除数据成功", Toast.LENGTH_SHORT).show();
                    data3.remove(p);
                    adapter.notifyDataSetChanged();
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


    private void deleteLock() {

        adapter.setOnItemLongClickListener(new lockListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position, final String id, final String lockusid, final String lockNumber, final String secretKey, final String allow, final String keyId) {
                Log.d("TAG",""+position+"id"+id);//弹出对话框提示傻逼用户
                Log.d("TAG",""+secretKey);
                Log.d("TAG",""+allow);
                String sb1=new String();
                for (int i = 0; i < allow.length(); i++) {
                    sb1 = allow.replace("", "0");
                }
               String allow2 = sb1.substring(0, sb1.length() - 1);
                Log.d("TAG","拼接allow"+allow2);
                allowbyt= StringUtils.toByteArray(allow2);
                aesks= StringUtils.toByteArray(secretKey);

                View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                EditText et= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                tv.setText("谨慎操作，导致数据丢失...");
                tv.setTextColor(Color.RED);
                tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(lockListActivity.this)
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

                        //1 根据当前uid 来判断  和锁里面的uid 是不是蓝牙管理员和普通用户
                        //连接蓝牙 和删除服务器数据
                        if (uid.equals(lockusid)){
                            initReceiveData();
                            initConnectBle(lockNumber,id,position);
                        }else {
                            //普通直接删除钥匙
                            upDataDelet(keyId,position);
                        }


                    }
                });

            }
        });
    }



  ;public void   upDataDelet(String id, final int postion){
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
                    Toast.makeText(lockListActivity.this,"删除数据成功", Toast.LENGTH_SHORT).show();
                    data3.remove(postion);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    /**
     * 连接蓝牙
     *
     */
        String id;//锁id；
    int p; //位置
    private void initConnectBle(String lockNumber,String idlock,int postion) {
     id=idlock;
        p=postion;
        Log.d("TAG","idlock"+idlock);
        Log.d("TAG","postion"+postion);

        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            showProgressDialog("","正在连接蓝牙...");
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

    private void startConnect(String clientID, String serverIP, String port) {
        //服务器地址   192.168.1.3
        NetWorkTesting net=new NetWorkTesting(this);
        if (net.isNetWorkAvailable()){

        String  uri ="tcp://";
        uri=uri+serverIP+":"+port;
        Log.d("MainActivity",uri+"  "+clientID+serverIP+port);
        /**
         * 连接的选项
         */
        MqttConnectOptions conOpt = new MqttConnectOptions();
        /**设计连接超时时间*/
        conOpt.setConnectionTimeout(3000);
        /**设计心跳间隔时间300秒*/
        conOpt.setKeepAliveInterval(300);
        /**
         * 创建连接对象
         */
        client = new MqttAndroidClient(this,uri, clientID);
        /**
         * 连接后设计一个回调
         */
        client.setCallback(new MqttCallbackHandler(this, clientID));
        /**
         * 开始连接服务器，参数：ConnectionOptions,  IMqttActionListener
         */
        try {
            client.connect(conOpt, null, new ConnectCallBackHandler(this));
        } catch (MqttException e) {
            e.printStackTrace();
        }
        }else {
        Toast.makeText(this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
        }
    }



    /**
     * 获取MqttAndroidClient实例
     * @return
     */
    public static MqttAndroidClient getMqttAndroidClientInstace(){
        if(client!=null)
            return  client;
        return null;
    }

    private void initauthor() {
        setLocationService();
    }
    public static final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    private void setLocationService() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (isLocationEnable(this)) {
                //定位已打开的处理


            } else {
                //定位依然没有打开的处理
                Toast.makeText(lockListActivity.this,"请打开权限", Toast.LENGTH_SHORT).show();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (permissions[0] .equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意使用该权限
            } else {
                // 用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //showTipDialog("用来扫描附件蓝牙设备的权限，请手动开启！");
                    return;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                subscibe();
            }
        },2000);
    }


//  fzz.PTP
   //
// public String topic="topicTest";
//public String topic="fzz\\PTP";
public String topic="fzzchat/PTP";
    private void subscibe() {
        NetWorkTesting netWorkTesting=new NetWorkTesting(this);
        if (netWorkTesting.isNetWorkAvailable()){
        if(client!=null){
            /**订阅一个主题，服务的质量默认为0*/
            try {
                client.subscribe(topic,0,null,new SubcribeCallBackHandler(lockListActivity.this));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(lockListActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    /**
     * 运行在主线程
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String string = event.getString();
        Log.d("TAG","收到消息"+topic);

            getUserLockList();




    }


    private void initlize() {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(lockListActivity.this,addSmartActivity.class);
                startActivity(intent);
              //  finish();
            }
        });

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserLockList();
            }
        });
        data3=new ArrayList();

        adapter=new lockListAdapter(data3,lockListActivity.this);
        lock_list_rc= (RecyclerView) findViewById(R.id.lock_list_rc);
        LinearLayoutManager lin=new LinearLayoutManager(lockListActivity.this);
        lin.setOrientation(OrientationHelper.VERTICAL);
        lock_list_rc.setLayoutManager(lin);
        lock_list_rc.setAdapter(adapter);



    }

    /**
     * 请求网络数据
     */
    public void getUserLockList() {
        String partid = SharedUtils.getString("partid");
        Log.d("TAG","partid"+partid);
        showProgressDialog("","正在加载数据...");
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
                    hideProgressDialog();
                }else {
                Log.d("TAG",body );
                Gson gson=new Gson();
                userLockBean bean = gson.fromJson(body, new TypeToken<userLockBean>() {}.getType());
                int code = bean.getCode();

                if (code==1001){
                    userLockBean.DataBean data = bean.getData();
                    List<?> parentLock = data.getParentLock();
                    Iterator<?> iterator = parentLock.iterator();
                    while (iterator.hasNext()){
                        Object next = iterator.next();
                        dataPart.add(net);
                    }

                    data3.clear();// 防止加载重复数据
                    List<userLockBean.DataBean.UserLockBean> userLock = data.getUserLock();
                    Iterator<userLockBean.DataBean.UserLockBean> iterator1 = userLock.iterator();
                    while (iterator1.hasNext()){
                        userLockBean.DataBean.UserLockBean next = iterator1.next();
                        data3.add(next);
                    }
                    data3.addAll(dataPart);
                    lock_list_rc.setAdapter(adapter);
                    swipe_refresh.setRefreshing(false); //刷新结束
                    adapter.notifyDataSetChanged();
                    hideProgressDialog();
                    Log.d("TAG",body);
                }else if(code==1002){
                    Log.d("TAG","网络错误");
                    hideProgressDialog();
                }
                if (!data3.isEmpty()){
                    iv.setVisibility(View.GONE);
                    lock_list_rc.setVisibility(View.VISIBLE);
                    tv_lock_list.setVisibility(View.GONE);
                    tv_lock_listone.setVisibility(View.GONE);
                }else {

                    iv.setVisibility(View.VISIBLE);
                    lock_list_rc.setVisibility(View.GONE);
                    tv_lock_list.setVisibility(View.VISIBLE);
                    tv_lock_listone.setVisibility(View.VISIBLE);

                }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideProgressDialog();
            }
        });
        }else {
            hideProgressDialog();
            Toast.makeText(lockListActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
            swipe_refresh.setRefreshing(false); //刷新结束


        }
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(client!=null)
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        MqttService service=new MqttService();
            unbindService(service);

    }

    private void unbindService(MqttService service) {
            service.onDestroy();
    }

    /**
     * 退出程序
     */
    private  static boolean isExit=false;
    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit=false;
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            exit();

            return  false;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mhandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            removeALLActivity();
            System.exit(0);
        }
    } @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meunlock,menu);
        return  true;
        //  return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){

            case R.id.seting_change_numbler:
                UserBean user = SharedUtils.getUser();
                Intent intent=new Intent(lockListActivity.this,LoginActivity.class);
                if (user!=null) {
                    String name = user.name;
                    name = "";
                    SharedPreferences shared = MainApplication.getInstence().getSharedPreferences("shared", Context.MODE_PRIVATE);
                    shared.edit().clear().commit();
                    MainApplication.getInstence().removeALLActivity_();  //清掉全部Activity
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.seting_add_admini:
                Intent intent1=new Intent(lockListActivity.this,addSmartActivity.class);
                startActivity(intent1);
                break;
        }
        return true;
        //  return super.onOptionsItemSelected(item);
    }


}
