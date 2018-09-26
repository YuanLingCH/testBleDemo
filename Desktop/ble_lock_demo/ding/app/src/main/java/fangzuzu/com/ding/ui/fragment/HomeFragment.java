package fangzuzu.com.ding.ui.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.bean.permisonBean;
import fangzuzu.com.ding.bean.xuzhuBean;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.ui.activity.ElectKeyManagerActivity;
import fangzuzu.com.ding.ui.activity.FingerPrintManageActivity;
import fangzuzu.com.ding.ui.activity.PasswordManagementActivity;
import fangzuzu.com.ding.ui.activity.addICCardActivity;
import fangzuzu.com.ding.ui.activity.keySetActivity;
import fangzuzu.com.ding.ui.activity.openLockRecodeActivity;
import fangzuzu.com.ding.ui.activity.sendKeyActivity;
import fangzuzu.com.ding.ui.activity.sendPassWordActivity;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.ScreenSizeUtils;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.view.RoundProgressBarWidthNumber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.R.attr.x;

/**
 * Created by yuanling on 2018/5/12.
 */

public class HomeFragment extends BaseFragment {
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "HomeFragment";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    ProgressDialog progressDialog;
    Toolbar toolbar;
    Button tv_ding;
    public  int REQUEST_ACCESS_COARSE_LOCATION=1;
    private byte[] token3=new byte[4];
    byte[]jiesouTock=new byte[16];
    byte[]token2=new byte[4];
    MediaPlayer mediaPlayer01;
    TextView tv_lock_name,elect;
     RecyclerView re_auth_list;
    PermissionLockhomeAdapter adapter;    //Type 0 为正常  1为过期
    List authData;
    boolean isKitKat = false;
    LinearLayout ll_home;
    String type="0";
    RoundProgressBarWidthNumber  mRoundProgressBar;
   // private HorizontalProgressBarWithNumber mProgressBar;
    private static final int MSG_PROGRESS_UPDATE = 0x110;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
          //  int progress = mProgressBar.getProgress();
            int roundProgress = mRoundProgressBar.getProgress();
         //   mProgressBar.setProgress(++progress);
            mRoundProgressBar.setProgress(++roundProgress);
         /*   if (progress >= 100) {
                mHandler.removeMessages(MSG_PROGRESS_UPDATE);
            }*/
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
        };
    };

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

        //  initThinkTime();

    }


    /**
     * 判断时间 是不是过期
     */
    private void initPanDuanTime() {
        final int[] current = {0};
        new Thread(){
            @Override
            public void run() {
                super.run();

                String websiteDatetime = unixTime.getWebsiteDatetime("http://www.baidu.com")+"";
                String substring = websiteDatetime.substring(0, websiteDatetime.length() - 3);
               current[0] = Integer.parseInt(substring);
                Log.d("TAG","北京时间撮"+substring);
            }
        }.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    /*    long timeStampSec = System.currentTimeMillis()/1000;
        String timestamp = String.format("%010d", timeStampSec);
        Log.d("TAG",""+timestamp);
        int current = Integer.parseInt(timestamp);*/
        if (!StringUtils.isEmpty(StartTime)&&!StringUtils.isEmpty(endTime)){

        String substring = StartTime.substring(0,StartTime.length()-2);
        Log.d("TAG","时间"+ substring);
        String s = unixTime.dateToStamp(substring);
        String substring1 = s.substring(0, s.length() - 3);
        int startTime = Integer.parseInt(substring1);
        Log.d("TAG","时间"+ startTime);
        String substringt = endTime.substring(0,endTime.length()-2);
        Log.d("TAG","时间"+ substring);
        String st = unixTime.dateToStamp(substringt);
        String substring1t = st.substring(0, s.length() - 3);
        int end = Integer.parseInt(substring1t);
        Log.d("TAG","时间"+ end);

        if (startTime- current[0] >0){
        //未生效

            tv_ding.setEnabled(false);
            tv_ding.setBackgroundResource(R.mipmap.ding_unable);
            type="1";
            View view = getLayoutInflater().inflate(R.layout.xuzhu_dialog, null);
            final TextView tv = (TextView) view.findViewById(R.id.tv);
            tv.setText("锁未生效，暂时不能使用");
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
            tv_submit.setText("我知道了");

            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

     /*       tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });*/
           tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      dialog.dismiss();
                 //   xuzhuGetdata();


                }
            });

        }else if (end- current[0] <0&&!StartTime.equals(endTime)){
    //已过期
            type="1";
            if (!StringUtils.isEmpty(updataFlag)){


                if (updataFlag.equals("2")){
                    //  弹出对话框

                    zhuqiBiaozhi="1";   //
                    View view = getLayoutInflater().inflate(R.layout.xuzhu_dialog, null);
                    final TextView tv = (TextView) view.findViewById(R.id.tv);
                    tv.setText("续租请激活锁,需要一分钟左右,请靠近锁");
                    tv.setTextSize(16);
                    tv.setGravity(Gravity.CENTER);
                    TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
                    tv_submit.setText("确认续租");
                    tv_submit.setGravity(Gravity.CENTER);
                    final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setView(view)
                            .create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

     /*       tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });*/
                    tv_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //  dialog.dismiss();
                            xuzhuGetdata();


                        }
                    });
                }
            }else {



            tv_ding.setEnabled(false);
            tv_ding.setBackgroundResource(R.mipmap.ding_unable);
         /*   re_auth_list.setVisibility(View.INVISIBLE);*/

            View view = getLayoutInflater().inflate(R.layout.xuzhu_dialog, null);
            final TextView tv = (TextView) view.findViewById(R.id.tv);
            tv.setText("锁已过期，不能使用");
            tv.setTextColor(Color.RED);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
            tv_submit.setText("我知道了");

            tv_submit.setGravity(Gravity.CENTER);

            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

     /*       tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });*/
          tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     dialog.dismiss();
                   // xuzhuGetdata();


                }
            });
            }
        }
        }


    }

    /**
     * 判断是不是过期   updataFlag 去判断   0  正常   1过期   2续租
     */
    String zhuqiBiaozhi=null;
    private void initThinkTime() {
        updataFlag="2";
        if (updataFlag.equals("2")){
            //  弹出对话框

            //
            View view = getLayoutInflater().inflate(R.layout.xuzhu_dialog, null);
            final TextView tv = (TextView) view.findViewById(R.id.tv);
            tv.setText("续租请激活锁,需要一分钟左右,请靠近锁");
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
            tv_submit.setText("确认续租");
            tv_submit.setGravity(Gravity.CENTER);
            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

     /*       tv_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });*/
            tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  dialog.dismiss();
                    xuzhuGetdata();


                }
            });
        }

    }

    /**
     * 请求服务器 拿到IC 指纹  身份证id
     */
    List ic_data;
    List ic_shengfenzheng;
    List ic_zhiwen;
    private void xuzhuGetdata() {
        ic_data=new ArrayList();
        ic_shengfenzheng=new ArrayList();
        ic_zhiwen=new ArrayList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())

                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Call<String> call = manager.getxuzhuID(Lockid, uid);
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
                        while (iterator.hasNext()){
                            xuzhuBean.DataBean next = iterator.next();
                            String addType = next.getAddType()+"";   //  0 ic   1省份在   2指纹
                            String unlockFlag = next.getUnlockFlag();   //id
                          if (addType.equals("0")){
                              if (!ic_data.contains(unlockFlag)){
                                  ic_data.add(unlockFlag);
                              }

                          }else if (addType.equals("1")){
                              if (!ic_shengfenzheng.contains(unlockFlag)){
                                  ic_shengfenzheng.add(unlockFlag);
                              }
                          }else if (addType.equals("2")){
                              if (!ic_zhiwen.contains(unlockFlag)){
                                  ic_zhiwen.add(unlockFlag);
                              }
                          }

                        }
                        for (int i = 0; i < ic_data.size(); i++) {
                            Log.d("TAGT", "续租ic" +ic_data.get(i) );
                        }
                        for (int i = 0; i < ic_shengfenzheng.size(); i++) {
                            Log.d("TAGT", "续租shengfenz" +ic_shengfenzheng.get(i) );
                        }


                        // 连接蓝牙  改变租期   租期 服务器传过来  统一改为一样的
                        initReceiveData(); //接收数据
                        initConnectBle();  //连接蓝牙

                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(bleStateMessage event){
       // hideProgressDialog();
        mRoundProgressBar.setVisibility(View.GONE);
        flag=false;
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
                adapter=new PermissionLockhomeAdapter( authData,MainApplication.getInstence(),type);
                re_auth_list.setAdapter(adapter);
                if (type.equals("0")){
                    authClick();
                }


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
                    Log.d("TAG", "点击权限指纹");
                    Intent intent=new Intent(MainApplication.getInstence(), FingerPrintManageActivity.class);
                    startActivity(intent);

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
        ll_home=(LinearLayout) root.findViewById(R.id.ll_home);
        toolbar = (Toolbar)root. findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (!StringUtils.isEmpty(jihe)){
            ((AppCompatActivity) getActivity()). getSupportActionBar().setHomeButtonEnabled(false); //设置返回键可用
            ((AppCompatActivity) getActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }else {
            ((AppCompatActivity) getActivity()). getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
            ((AppCompatActivity) getActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        re_auth_list= (RecyclerView) root.findViewById(R.id.re_auth_list);
        re_auth_list.setNestedScrollingEnabled(false);
        initgetAuthor();

        tv_lock_name= (TextView) root.findViewById(R.id.tv_lock_name);
        tv_lock_name.setText(lockName);
        elect= (TextView) root.findViewById(R.id.elect);
        elect.setText(electricity+"%");
        tv_ding= (Button) root.findViewById(R.id.tv_ding);

        setStatusBar();
        initPanDuanTime();

    //  mProgressBar = (HorizontalProgressBarWithNumber)root. findViewById(R.id.id_progressbar01);
     mRoundProgressBar = (RoundProgressBarWidthNumber)root. findViewById(R.id.id_progress02);
        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
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
    byte []batt=new byte[1]; //电量
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
                        if (StringUtils.isEmpty(zhuqiBiaozhi)){
                            sendSecond();//开锁
                        }else if (zhuqiBiaozhi.equals("1")){
                            sendxuZuData(); // 续租
                        }


                    }  if (decrypt[0]==02&&decrypt[1]==02&&decrypt[2]==04&&decrypt[3]==00){
                        //上传电量
                        System.arraycopy(decrypt,4,batt,0,batt.length);
                        String s = mBleController.bytesToHexString(batt).trim();
                        Log.d("TAG","电量"+  s);
                        int iValue = Integer.parseInt(s, 16);
                        Log.d("TAG","电量s"+  iValue);
                   //     long dec_num = Long.parseLong(s, 16);
                     //   Log.d("TAG","电量"+dec_num);s
                if (!electricity.equals(iValue+"")){
                    sendBattData(iValue);
                }

                    }

                    if (decrypt[0]==6&&decrypt[1]==1&&decrypt[2]==1&&decrypt[3]==0){
                        hideProgressDialog();
                        Log.d("TAG","开锁成功");

                        flag=false;
                            mRoundProgressBar.setVisibility(View.GONE);
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

                    if (decrypt[0]==04&&decrypt[1]==01&&decrypt[4]==00){
                        Log.d("TAG","续租租期");
                        sendToBleZuQiTime();
                    }

                }
            }
        });
    }

    /**
     * 上传电量
     */
    private void sendBattData(int b) {





        Log.d("TAG","上传电量"+x );
        String lockid = MainApplication.getInstence().getLockid();
        String uid = MainApplication.getInstence().getUid();

        String pasword = MainApplication.getInstence().getPasword();
        String lockName = MainApplication.getInstence().getLockName();
        Map<String,String>map=new HashMap<>();
        map.put("id",lockid );
        map.put("adminUserId",uid );
        map.put("lockName",lockName );
        map.put("adminPsw",pasword );
        map.put("electricity",b+"" );
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG","电量json"+s);
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
                   // Toast.makeText(MainApplication.getInstence(), "修改电量成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 续租发送租期
     * 这个租期是服务器统一给的时间  开始时间  结束时间
     */
    private void sendToBleZuQiTime() {
        byte[] head=new byte[3];
        head[0]=0x04;
        head[1]=0x04;
        head[2]=0x08;


        long startTime=1536288422;
        String  start = Integer.toHexString((int) startTime);
        Log.d("TAG","续租租时间"+ start);

        long endTime=1536288722;
        String  end = Integer.toHexString((int) endTime);

       byte[] byteStart = jiamiandjiemi.hexString2Bytes(start);
        byte[] byteEnd = jiamiandjiemi.hexString2Bytes(end);
        byte[] byteOne = byteCunchu.unitByteArray(head, byteStart);
        byte[] byteTow = byteCunchu.unitByteArray( byteOne, byteEnd);
        byte[] datathree = byteCunchu.unitByteArray(byteTow , token3);
        byte []data=new byte[16];
        for (int i = 0; i < datathree.length; i++) {
            data[i]= datathree[i];
        }
        byte[] encrypt = jiamiandjiemi.Encrypt(data, secretKeyBytes);

        mBleController.writeBuffer(encrypt, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","续租租期发送成功");
                // sendFirstCode();  //发送锁标识符
            }
            @Override
            public void onFailed(int state) {
                Log.d("TAG","续租租期发送失败"+state);
            }
        });
    }

    /**
     * 向锁端发送续租命令
     */
    private void sendxuZuData() {
        Log.d("TAG","续租方法走了");
        // ic  0  身份证 1  指纹 2
        byte []sendIC;

        for (int i = 0; i < ic_data.size(); i++) {
            String o = (String) ic_data.get(i);
            Log.d("TAG","续租"+o);
            byte[] bytes = jiamiandjiemi.hexString2Bytes(o);


            for (int i1 = 0; i1 < bytes.length; i1++) {
                Log.d("TAG","续租数组"+bytes[i1]);
            }
            // 长度
            byte []icLength=new byte[1];
            icLength[0]= (byte) (bytes.length+1);
            byte []type=new byte[1];
            type[0]=0x03;
            sendDataXuzuToBle(bytes,icLength,type);
        }

    }

    private void sendDataXuzuToBle( byte[] bytes,byte []icLength, byte []type) {
        byte []head=new byte[4];
        head[0]=0x04;
        head[1]=0x01;
        head[2]=icLength[0];
        head[3]=type[0];
        byte[] byteOne = byteCunchu.unitByteArray(head, bytes);
         byte[] byteTwo = byteCunchu.unitByteArray(byteOne, token3);
        byte[]data=new byte[16];
        for (int i = 0; i < byteTwo.length; i++) {
            data[i]= byteTwo[i];
        }
        byte[] encrypt = jiamiandjiemi.Encrypt(data, secretKeyBytes);
        byte[] decrypt = jiamiandjiemi.Decrypt(encrypt, secretKeyBytes);
        Log.d("TAG","续租"+mBleController.bytesToHexString(data) + "\r\n");
        mBleController.writeBuffer(encrypt, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","续租发送成功");
                // sendFirstCode();  //发送锁标识符
            }
            @Override
            public void onFailed(int state) {
                Log.d("TAG","续租发送失败"+state);
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
    String jihe;
    String StartTime;
    String endTime;
    String updataFlag;
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
        jihe = getActivity().getIntent().getStringExtra("jihe");
        StartTime= getActivity().getIntent().getStringExtra("startTime");
        endTime = getActivity().getIntent().getStringExtra("endTime");  //updataFlag
        updataFlag = getActivity().getIntent().getStringExtra("updataFlag");
        uid= SharedUtils.getString("uid");

        Log.d("TAG","传过来的Id"+Lockid);
        Log.d("TAG","传过来的secretKey"+secretKey);
        Log.d("TAG","lockNumber"+lockNumber);
        Log.d("TAG","adminPsw"+adminPsw);
        Log.d("TAG"," allow"+allow);
        Log.d("TAG","  endTime "+ endTime );
        Log.d("TAG"," StartTime"+StartTime);
        MainApplication.getInstence().setAllow(allow1);
        MainApplication.getInstence().setLockid(Lockid);
        MainApplication.getInstence().setLockName(lockName);
        MainApplication.getInstence().setPasword(adminPsw1);
        MainApplication.getInstence().setElect(electricity);
        MainApplication.getInstence().setMac(lockNumber);
        MainApplication.getInstence().setStartTime(StartTime);
        MainApplication.getInstence().setEndTime(endTime);
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
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (permissions[0] .equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意使用该权限
            } else {
                // 用户不同意，向用户展示该权限作用
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //showTipDialog("用来扫描附件蓝牙设备的权限，请手动开启！");
                    return;
                }
            }
        }
    }


    /**
     * 连接蓝牙
     */
    String  strbiaozhi;
    List   bledata=new ArrayList();
    List blename=new ArrayList();
    private void initConnectBle() {

        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(MainApplication.getInstence(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
//向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(MainApplication.getInstence(),"打开权限才能用哦", Toast.LENGTH_SHORT).show();
                }
            }
        }


        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            if (!bledata.contains(lockNumber)){
                mBleController.scanBleone(0, new ScanCallback() {
                    @Override
                    public void onSuccess() {
                      //  hideProgressDialog();
                        Log.d("TAG","蓝牙扫描结束ces");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bledata.size()==0){
                              mRoundProgressBar.setVisibility(View.GONE);
                                   flag=false;

                                    Toast.makeText(MainApplication.getInstence(), "没有扫描到锁，请重新扫描", Toast.LENGTH_SHORT).show();
                                }else{
                                    if (strbiaozhi.equals("02")){
                                        hideProgressDialog();
                                        if (!blename.contains("H_DFU")){
                                            connect();
                                        }

                                    }

                                }

                            }
                        });
                     //
                       // hideProgressDialog();
                    }

                    @Override
                    public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                       // showProgressDialog("","正在连接蓝牙...");
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
                                      //  hideProgressDialog();
                                        mRoundProgressBar.setVisibility(View.GONE);
                                        flag=false;
                                        Toast.makeText(MainApplication.getInstence(), "你的锁已被初始化,请联系管理员", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                            }

                        }
                        String name = device.getName();
                        if (!StringUtils.isEmpty(name)){
                            if (name.equals("H_DFU")){
                                blename.add(name);
                                mRoundProgressBar.setVisibility(View.GONE);
                                flag=false;
                                Toast.makeText(MainApplication.getInstence(), "你的锁已处于升级模式，请升级完成才能正常使用", Toast.LENGTH_SHORT).show();
                            return;
                            }
                        }

                    }
                });
            }else  {
                Log.d("TAG","没扫描");
                if (strbiaozhi.equals("00")||strbiaozhi.equals("01")){
                    Log.d("TAG","标准"+strbiaozhi);
                    mRoundProgressBar.setVisibility(View.GONE);
                    flag=false;
                    Toast.makeText(MainApplication.getInstence(), "你的锁已被初始化,请联系管理员", Toast.LENGTH_SHORT).show();
                         //   hideProgressDialog();
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

                   hideProgressDialog();
                    mRoundProgressBar.setVisibility(View.GONE);
                    flag=false;
                    Toast.makeText(MainApplication.getInstence(), "连接蓝牙失败", Toast.LENGTH_SHORT).show();

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
                        hideProgressDialog();
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
        map1.put("userId",uid);
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
    boolean flag=false;
    @Override
    protected void initEvents() {

        tv_ding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!flag){
                        int tv_width = tv_ding.getMeasuredWidth();
                        int tv_height = tv_ding.getMeasuredHeight();
                        Log.d("TAG","控件的宽度"+tv_width+"控件的高度"+tv_height);

                        ViewGroup.LayoutParams layoutParams = mRoundProgressBar.getLayoutParams();
                        layoutParams.width=tv_width-70;
                        layoutParams.height=tv_height-70;
                        mRoundProgressBar.setLayoutParams(layoutParams);
                        mRoundProgressBar.setVisibility(View.VISIBLE);
                        mRoundProgressBar.setProgress(0);
                        initReceiveData(); //接收数据
                        initConnectBle();  //连接蓝牙
                        flag=true;
                    }







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
