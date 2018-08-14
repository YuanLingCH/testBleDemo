package fangzuzu.com.ding.ui.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.PermissionLockhomeAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.permisonBean;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.ui.activity.ElectKeyManagerActivity;
import fangzuzu.com.ding.ui.activity.PasswordManagementActivity;
import fangzuzu.com.ding.ui.activity.addICCardActivity;
import fangzuzu.com.ding.ui.activity.keySetActivity;
import fangzuzu.com.ding.ui.activity.openLockRecodeActivity;
import fangzuzu.com.ding.ui.activity.sendKeyActivity;
import fangzuzu.com.ding.ui.activity.sendPassWordActivity;
import fangzuzu.com.ding.utils.ScreenSizeUtils;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by yuanling on 2018/5/12.
 */

public class HomeFragment extends BaseFragment {
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "HomeFragment";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    ProgressDialog progressDialog;
    Toolbar toolbar;
   TextView tv_ding;
    private byte[] token3=new byte[4];
    byte[]jiesouTock=new byte[16];
    byte[]token2=new byte[4];
    MediaPlayer mediaPlayer01;
    TextView tv_lock_name,elect;
     RecyclerView re_auth_list;
    PermissionLockhomeAdapter adapter;
    List authData;
    boolean isKitKat = false;

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBleController = BleController.getInstance().init(getActivity());
        initgetData();
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(bleStateMessage event){
        hideProgressDialog();
        Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败,请重试", Toast.LENGTH_SHORT).show();
        Log.d("TAG","状态刷新");
    }

    /**
     * 拿到权限列表
     */
    private void initgetAuthor() {
        //拿到权限
        authData=new ArrayList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())

                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Log.d("TAG","uid"+uid);
        Log.d("TAG","lock"+Lockid);
        Call<String> call = manager.queryPermison(uid, Lockid);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG", "permison" + body);
                if (StringUtils.isEmpty(body)){

                }else {
                Gson gson = new Gson();
                permisonBean bean = gson.fromJson(body, new TypeToken<permisonBean>() {}.getType());
                List<permisonBean.DataBean> data = bean.getData();
                authData.clear();
                if (data!=null&&!data.isEmpty()){
                    Iterator<permisonBean.DataBean> iterator = data.iterator();
                    while (iterator.hasNext()){
                        permisonBean.DataBean next = iterator.next();
                        authData.add(next);
                    }
                }
                }
                GridLayoutManager gridLayoutManager = new GridLayoutManager(MainApplication.getInstence(), 4);
                gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
                re_auth_list.setLayoutManager(gridLayoutManager);
                adapter=new PermissionLockhomeAdapter( authData,MainApplication.getInstence());
                re_auth_list.setAdapter(adapter);
                authClick();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });




    }

    private void authClick() {
        adapter.setItemClickListener(new PermissionLockhomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String id) {
                Log.d("TAG", "点击权限" + id);
                if (id.equals("发送钥匙")){
                    Intent intent =new Intent(MainApplication.getInstence(), sendKeyActivity.class);
                    intent.putExtra("id",Lockid);
                    startActivity(intent);
                }else if (id.equals("发送密码")){
                    Intent intent=new Intent(MainApplication.getInstence(), sendPassWordActivity.class);
                    startActivity(intent);
                }else if (id.equals("钥匙管理")){
                    Intent intent=new Intent(MainApplication.getInstence(), ElectKeyManagerActivity.class);
                    intent.putExtra("id",Lockid);
                    startActivity(intent);
                }else if (id.equals("密码管理")){
                    Intent intent=new Intent(MainApplication.getInstence(), PasswordManagementActivity.class);
                    intent.putExtra("id",Lockid);
                    startActivity(intent);
                }else if (id.equals("IC卡")){
                    Intent intent=new Intent(MainApplication.getInstence(), addICCardActivity.class);
                    startActivity(intent);
                }else if (id.equals("指纹")){

                }else if (id.equals("操作记录")){
                    Intent intent=new Intent(MainApplication.getInstence(),  openLockRecodeActivity.class);
                    intent.putExtra("Lockid",Lockid);
                    startActivity(intent);
                }else if (id.equals("设置")){
                    Intent intent=new Intent(MainApplication.getInstence(),  keySetActivity.class);
                    intent.putExtra("id",Lockid);
                    startActivity(intent);
                }
            }
        });
    }







    @Override
    protected void initViews() {
        setHasOptionsMenu(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        toolbar = (Toolbar)root. findViewById(R.id.toolbar);
        toolbar.setTitle("");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()). getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        ((AppCompatActivity) getActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        re_auth_list= (RecyclerView) root.findViewById(R.id.re_auth_list);
        initgetAuthor();

        tv_lock_name= (TextView) root.findViewById(R.id.tv_lock_name);
        tv_lock_name.setText(lockName);
        elect= (TextView) root.findViewById(R.id.elect);
        elect.setText(electricity+"%");
        setStatusBar();
    }

    protected void setStatusBar() {
        if (isKitKat){
            int statusH = screenAdapterUtils.getStatusHeight(MainApplication.getInstence());
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getActivity().getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }

    /**
     * 蓝牙接收数据
     */
    private void initReceiveData() {

        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
              /*  mReciveString.append(mBleController.bytesToHexString(value) + "\r\n");
                Log.d("TAG","接收数据HomeFragment"+mReciveString.toString());*/
                byte[] decrypt = jiamiandjiemi.Decrypt(value, secretKeyBytes);
                Log.d("TAG","解密homeFragment"+mBleController.bytesToHexString(decrypt) + "\r\n");

                if (value[0]!=01){
                    jiesouTock=value;
                    byte[] decrypt1 = jiamiandjiemi.Decrypt(jiesouTock, secretKeyBytes);

                    Log.d("TAG","解密"+mBleController.bytesToHexString(decrypt1) + "\r\n");

                    if (decrypt1[0]==02&&decrypt1[1]==01&&decrypt1[2]==04){
                        System.arraycopy(decrypt,3,token2,0,token2.length);
                        byte[]token1=new byte[4];
                        token1[0]=02;
                        token1[1]=03;
                        token1[2]=04;
                        token1[3]=05;
                        token3[0]= (byte) (token2[0]^token1[0]);
                        token3[1]= (byte) (token2[1]^token1[1]);
                        token3[2]= (byte) (token2[2]^token1[2]);
                        token3[3]= (byte) (token2[3]^token1[3]);
                        hideProgressDialog();
                        Log.d("TAG","token3"+mBleController.bytesToHexString(token3) + "\r\n");
                        for (int i = 0; i < token2.length; i++) {
                            Log.d("TAG","token2Fr"+token2[i]);
                        }

                      sendFirstCode(); //发送锁标识

                    }if (decrypt[0]==02&&decrypt[1]==02&&decrypt[2]==04&&decrypt[3]==00) {
                        sendSecond();//开锁

                    }if (decrypt[0]==6&&decrypt[1]==1&&decrypt[2]==1&&decrypt[3]==0){
                        hideProgressDialog();
                        Log.d("TAG","开锁成功");

                        mediaPlayer01 = MediaPlayer.create(getActivity(), R.raw.sound_for_connect);
                        mediaPlayer01.start();
                        upDataOpenRecoder();
                        //弹出对话框  yyyy年MM月dd日
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
//获取当前时间
                        Date date = new Date(System.currentTimeMillis());

                        View view = View.inflate(getActivity(), R.layout.dialog_normal, null);

                        tv_time= (TextView) view.findViewById(R.id.time_tv);
                        tv_time.setText(simpleDateFormat.format(date));
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity(),R.style.NoBackGroundDialog);
                        final AlertDialog alertDialog = adb.setView(view).create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        //设置对话框的大小
                     //   view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.23f));
                        Window dialogWindow = alertDialog.getWindow();
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        lp.width = (int) (ScreenSizeUtils.getInstance(getActivity()).getScreenWidth() * 0.80f);
                        lp.height =(int) (ScreenSizeUtils.getInstance(getActivity()).getScreenHeight() * 0.40f);
                        lp.gravity = Gravity.CENTER;
                        dialogWindow.setAttributes(lp);


                        Timer timer=new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                                mediaPlayer01.release();
                              mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                                mBleController.closeBleConn();

                            }
                        },3000);

                    }if (decrypt[0]==03&&decrypt[1]==04&&decrypt[2]==01&&decrypt[3]==01){
                        tongbuTime();  //同步时间
                    }


                }
            }
        });
    }



    TextView tv_time;
    String Lockid;
    String secretKey;  //密钥
    String lockNumber; //mac地址
    String adminPsw;  //管理员密码
    String allow; //锁标识
    byte[] secretKeyBytes;
    String electricity;
    byte[] adminPswBytes;
    byte[]allowbyt;
    String   lockName;
    String uid;
    String  adminPsw1;
    private void initgetData() {
        Lockid =getActivity(). getIntent().getStringExtra("id");
        secretKey= getActivity().getIntent().getStringExtra("secretKey");
        lockNumber= getActivity().getIntent().getStringExtra("lockNumber").trim();
      adminPsw1= getActivity().getIntent().getStringExtra("adminPsw");
        String   allow1 = getActivity().getIntent().getStringExtra("allow");
        lockName = getActivity().getIntent().getStringExtra("lockName");
        electricity = getActivity().getIntent().getStringExtra("electricity");
        String adminUserId = getActivity().getIntent().getStringExtra("adminUserId");
        SharedUtils.putString("adminUserId",adminUserId);
        uid= SharedUtils.getString("uid");

        Log.d("TAG","传过来的Id"+Lockid);
        Log.d("TAG","传过来的secretKey"+secretKey);
        Log.d("TAG","lockNumber"+lockNumber);
        Log.d("TAG","adminPsw"+adminPsw);
        Log.d("TAG"," allow"+allow);
        MainApplication.getInstence().setAllow(allow1);
        MainApplication.getInstence().setLockid(Lockid);
        MainApplication.getInstence().setLockName(lockName);
        MainApplication.getInstence().setPasword(adminPsw1);
        MainApplication.getInstence().setElect(electricity);
        MainApplication.getInstence().setMac(lockNumber);

        Log.d("TAG","adminPsw1"+adminPsw1);

        String sb=new String();
        for (int i = 0; i < adminPsw1.length(); i++) {
            sb = adminPsw1.replace("", "0");
        }
        adminPsw = sb.substring(0, sb.length() - 1);
        Log.d("TAG","拼接paw"+adminPsw);


        String sb1=new String();
        for (int i = 0; i < allow1.length(); i++) {
            sb1 = allow1.replace("", "0");
        }
        allow = sb1.substring(0, sb1.length() - 1);
        Log.d("TAG","拼接allow"+allow);



        secretKeyBytes= StringUtils.toByteArray(secretKey);
        adminPswBytes=StringUtils.toByteArray(adminPsw);
        byteCunchu.put(adminPswBytes,"adminPswBytes");
        allowbyt= StringUtils.toByteArray(allow);
        byteCunchu.put(secretKeyBytes,"secretKeyBytes");
        byteCunchu.put( allowbyt,"allowbyt");//存锁标识

    }
    /**
     * 连接蓝牙
     */
    List   bledata=new ArrayList();
    private void initConnectBle() {
        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            if (!bledata.contains(lockNumber)){
                mBleController.scanBleone(0, new ScanCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","蓝牙扫描结束");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bledata.size()==0){
                                    Toast.makeText(MainApplication.getInstence(), "没有扫描到锁，请重新扫描", Toast.LENGTH_SHORT).show();
                                }else{

                                    connect();
                                }

                            }
                        });
                     //
                        hideProgressDialog();
                    }

                    @Override
                    public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        String address = device.getAddress();
                        if (address.equals(lockNumber)){
                            if (!bledata.contains(lockNumber)){
                                bledata.add(address);
                                Log.d("TAG","蓝牙扫描"+address);
                            }

                        }

                    }
                });
            }else  {
                Log.d("TAG","没扫描");
                connect();
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
                byte[] encrypt = jiamiandjiemi.Encrypt(data4, secretKeyBytes);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt) + "\r\n");

                mBleController.writeBuffer(encrypt, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","身份校验成功");
                       // sendFirstCode();  //发送锁标识符
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
                byte[] encrypt1 = jiamiandjiemi.Encrypt(data5, secretKeyBytes);
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
        },800);


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

        byte[] encrypt40 = jiamiandjiemi.Encrypt(data80,secretKeyBytes);
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


    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(getActivity(), title, message, true, false);
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
     * 上传开锁记录
     */

    private void upDataOpenRecoder() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        // time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));

        Log.d("TAG","当前时间"+simpleDateFormat.format(date));

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                 .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Map<String,Map<String,String>>map=new HashMap<>();
        Map<String,String>map1=new HashMap<>();
        map1.put("unlockPwd",adminPsw1);
        map1.put("unlockTime",simpleDateFormat.format(date));
        map1.put("unlockType","1");
        map1.put("lockId",Lockid);
        map.put("operatinList",map1);
        Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG","拼接json"+s);
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

    /**
     * 蓝牙开锁
     */
    @Override
    protected void initEvents() {
        tv_ding= (TextView) root.findViewById(R.id.tv_ding);
        tv_ding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    initReceiveData(); //接收数据
                  initConnectBle();  //连接蓝牙
                showProgressDialog("","正在连接蓝牙...");
           /*     final bleConnectUtils utils=new bleConnectUtils();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.bleConnect(mBleController,secretKeyBytes,allowbyt,lockNumber);
                    }
                });*/




            }
        });
    }

    @Override
    protected void initData() {

    }


    private void sendSecond(){

        // 5.开锁
        String length = adminPswBytes.length+"";
        String pas=new String();
        pas = length.replace("", "0");
        pas =  pas.substring(0,  pas.length() - 1);
        Log.d("TAG","拼接pas"+ pas);
        byte[] pasbyteslength = StringUtils.toByteArray(pas);
        Log.d("TAG","密码长度"+length);
        byte[]header=new byte[3];
        header[0]=0x06;
        header[1]=0x01;
        header[2]=pasbyteslength[0];
        byte[] bytes = byteCunchu.unitByteArray(header, adminPswBytes);
        Log.d("TAG","加密"+mBleController.bytesToHexString(bytes) + "\r\n");
        byte[] bytes1 = byteCunchu.unitByteArray(bytes, token3);
        Log.d("TAG","加密"+mBleController.bytesToHexString( bytes1) + "\r\n");
        byte[]data16=new byte[16];
        for (int i = 0; i < bytes1.length; i++) {
            data16[i]= bytes1[i];
        }
        Log.d("TAG","赋值"+mBleController.bytesToHexString( data16) + "\r\n");

        byte[] encrypt11 = jiamiandjiemi.Encrypt(data16, secretKeyBytes);
        Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt11) + "\r\n");

        mBleController.writeBuffer(encrypt11, new OnWriteCallback() {
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
     * 解除接收数据
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
        if (mediaPlayer01 !=null) {
            mediaPlayer01.release();
        }
        mBleController.closeBleConn();

       EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity(). finish();


                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
