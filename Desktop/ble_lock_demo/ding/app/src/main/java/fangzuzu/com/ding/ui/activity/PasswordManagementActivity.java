package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.hansion.h_ble.callback.ScanCallback;
import com.hansion.h_ble.event.bleStateMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import fangzuzu.com.ding.adapter.passwordManagerListAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.bean.passwordManagerBean;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.bean.passwordManagerBean.DataBeanX;

/**
 * Created by lingyuan on 2018/7/3.
 */

public class PasswordManagementActivity extends BaseActivity {
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "PasswordManagementActivity";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    Toolbar toolbar;
  RecyclerView pasw_rc;
    String lockid;
    List data3;
    passwordManagerListAdapter adapter;
    SwipeRefreshLayout srf;
     int page=1;
    private byte[] token3;
    byte[] aesks;
    String lockType;
    String lockFlag;
    private final int PAGE_COUNT = 100;
    String mac; //蓝牙地址
    byte[]token2=new byte[4];
    ProgressDialog progressDialog;
    byte[] allowbyt;
    TextView tv_delet_quanbuPaws;//清空密码
    private Handler mHandler = new Handler(Looper.getMainLooper());
    boolean isKitKat = false;
 LinearLayout   ll_nodata;
    ImageView iv_no_data;
    TextView tv_no_datae;
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
        setContentView(R.layout.password_management_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        lockid = getIntent().getStringExtra("id");
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        mBleController = BleController.getInstance().init(this);
        //2.身份校验
        aesks = byteCunchu.getbyte("secretKeyBytes");
        mac = MainApplication.getInstence().getMac();
        allowbyt = byteCunchu.getbyte("allowbyt"); //得到锁标识符


        getdata(1);  //默认加载第一页
        initlize();

        EventBus.getDefault().register(this);
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



    /**
     * 蓝牙接收
     */
    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","解密密码管理"+mBleController.bytesToHexString(decrypt) + "\r\n");
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
                }if (decrypt[0]==04&&decrypt[1]==05&&decrypt[2]==01&&decrypt[3]==00){
                    //断开蓝牙
                    mBleController.closeBleConn();
                    hideProgressDialog();
                    if(!StringUtils.isEmpty(lockFlag)){
                        delectData(idlock,"",""); //删除服务器数据 单个删除
                    }else if(StringUtils.isEmpty(lockFlag)) {
                        String lockid = MainApplication.getInstence().getLockid();
                        Log.d("TAG","lockid.."+lockid);
                        delectData("","3",lockid); //删除服务器数据 全部
                    }


                }if (decrypt[0]==02&&decrypt[1]==02&&decrypt[2]==04&&decrypt[3]==00){
                    if(!StringUtils.isEmpty(lockFlag)){
                        initSendDelectData(); //删除蓝牙数据
                    }else  if (StringUtils.isEmpty(lockFlag)){
                        delectAllPasw();//删除蓝牙数据
                    }
                }

            }
        });

    }


    /**
     * 蓝牙发送数据
     */
    private void initSendDelectData() {
        Log.d("TAG","存储数据 aesks"+mBleController.bytesToHexString(aesks) + "\r\n");
        Log.d("TAG","存储数据 token3"+mBleController.bytesToHexString(token3) + "\r\n");
        Log.d("TAG","获取数据"+lockType);
        Log.d("TAG","获取数据"+lockFlag);
        int length = lockFlag.length();
        Log.d("TAG","获取数据"+length);
        String sb=new String();
        for (int i = 0; i < lockFlag.length(); i++) {
            sb = lockFlag.replace("", "0");
        }
        lockFlag = sb.substring(0, sb.length() - 1);
        Log.d("TAG","拼接paw"+lockFlag);
        byte[] bytes = StringUtils.toByteArray(lockFlag); //转为字节数组

        if (lockFlag.length()>16){
            //分包
            String substring = lockFlag.substring(0, (lockFlag.length() - (lockFlag.length() - 14)));
            String substringdata = lockFlag.substring(14,lockFlag.length());
            Log.d("TAG","substring"+substring);
            Log.d("TAG","substring1"+substringdata);
            byte[] bytesData1 = StringUtils.toByteArray(substring); //转为字节数组
            byte[] bytesData2 = StringUtils.toByteArray(substringdata); //转为字节数组
            Timer timer=new Timer();
            if (lockFlag.length()==18){

                byte[]data81=new byte[16];
                data81[0]=0x04;
                data81[1]=0x05;
                data81[2]=0x08;
                data81[3]=0x06;
                data81[4]=bytesData1[0];
                data81[5]=bytesData1[1];
                data81[6]=bytesData1[2];
                data81[7]=bytesData1[3];
                data81[8]=bytesData1[4];
                data81[9]=bytesData1[5];
                data81[10]=bytesData1[6];
                data81[11]=token3[0];
                data81[12]=token3[1];
                data81[13]=token3[2];
                data81[14]=token3[3];
                data81[15]=0x01;
                final byte[] encrypt41 = jiamiandjiemi.Encrypt(data81,aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt41) + "\r\n");


                        mBleController.writeBuffer(encrypt41, new OnWriteCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG","发送成功");

                            }
                            @Override
                            public void onFailed(int state) {

                            }
                        });


                byte[]data80=new byte[16];
                data80[0]=0x04;
                data80[1]=0x05;
                data80[2]=0x02;
                data80[3]=bytesData2[0];
                data80[4]=bytesData2[1];
                data80[5]=token3[0];
                data80[6]=token3[1];
                data80[7]=token3[2];
                data80[8]=token3[3];
                data80[9]=0x00;
                final byte[] encrypt40 = jiamiandjiemi.Encrypt(data80,aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt40) + "\r\n");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
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
                },1000);


            }else if (lockFlag.length()==20){

                byte[]data81=new byte[16];
                data81[0]=0x04;
                data81[1]=0x05;
                data81[2]=0x08;
                data81[3]=0x06;
                data81[4]=bytesData1[0];
                data81[5]=bytesData1[1];
                data81[6]=bytesData1[2];
                data81[7]=bytesData1[3];
                data81[8]=bytesData1[4];
                data81[9]=bytesData1[5];
                data81[10]=bytesData1[6];
                data81[11]=token3[0];
                data81[12]=token3[1];
                data81[13]=token3[2];
                data81[14]=token3[3];
                data81[15]=0x01;
                final byte[] encrypt41 = jiamiandjiemi.Encrypt(data81,aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt41) + "\r\n");


                        mBleController.writeBuffer(encrypt41, new OnWriteCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG","发送成功");

                            }
                            @Override
                            public void onFailed(int state) {

                            }
                        });


                byte[]data80=new byte[16];
                data80[0]=0x04;
                data80[1]=0x05;
                data80[2]=0x03;
                data80[3]=bytesData2[0];
                data80[4]=bytesData2[1];
                data80[5]=bytesData2[2];
                data80[6]=token3[0];
                data80[7]=token3[1];
                data80[8]=token3[2];
                data80[9]=token3[3];
                data80[10]=0x00;
                final byte[] encrypt40 = jiamiandjiemi.Encrypt(data80,aesks);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt40) + "\r\n");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
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
                },1000);


            }
        }else { //小于等于16 不用分
            byte[]header=new byte[4];
            String length1 = bytes.length+1+"";
            String pas=new String();
            pas = length1.replace("", "0");
            pas =  pas.substring(0,  pas.length() - 1);
            Log.d("TAG","拼接pas"+ pas);
            byte[] pasbyteslength = StringUtils.toByteArray(pas);
            Log.d("TAG","密码长度"+length1);
            header[0]=0x04;
            header[1]=0x05;
            header[2]= pasbyteslength[0];
            header[3]=0x06;
            byte[] bytesData = byteCunchu.unitByteArray(header, bytes);
            byte[] bytesData1 = byteCunchu.unitByteArray(bytesData, token3);
            byte[]data80=new byte[16];
            for (int i = 0; i < bytesData1.length; i++) {
                data80[i]= bytesData1[i];
            }

            byte[] encrypt40 = jiamiandjiemi.Encrypt(data80,aesks);
            byte[] decrypt = jiamiandjiemi.Decrypt(encrypt40, aesks);
            Log.d("TAG","加密不分包"+mBleController.bytesToHexString(decrypt) + "\r\n");

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




    }
    private void initlize() {
        ll_nodata=(LinearLayout) findViewById(R.id.ll_nodata);
         iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_datae=(TextView) findViewById(R.id.tv_no_data);
        tv_delet_quanbuPaws= (TextView) findViewById(R.id.tv_delet_quanbuPaws);
        srf= (SwipeRefreshLayout) findViewById(R.id.srf);
        pasw_rc= ( RecyclerView) findViewById(R.id.pasw_rc);
        final LinearLayoutManager lin=new LinearLayoutManager(PasswordManagementActivity.this);
        lin.setOrientation(OrientationHelper.VERTICAL);
        pasw_rc.setLayoutManager(lin);

        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //  adapter.resetDatas();
                getdata(1);
            }
        });
 /*       //滑动监听
        pasw_rc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                   if ((adapter.isFadeTips() == false && lastVisibleItem + 1 == adapter.getItemCount())){


                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("TAG", "滑动1");

                                    if (totalPage >= page) {
                                        getdata(++page);
                                    }
                                    page++;


                                    updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                                }
                            }, 500);




                   }
                }
                if (adapter.isFadeTips() == true && lastVisibleItem + 2 == adapter.getItemCount()) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TAG","滑动2");

                            if (totalPage >= page) {
                                getdata(++page);
                            }
                            page++;
                          //  updateRecyclerView(adapter.getRealLastPosition(), adapter.getRealLastPosition() + PAGE_COUNT);
                        }
                    }, 500);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = lin.findLastVisibleItemPosition();
            }
        });*/
        //清空密码
        tv_delet_quanbuPaws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接蓝牙  删除服务器数据

                View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                EditText et_yanzhenpasw= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                et_yanzhenpasw.setVisibility(View.GONE);
               // tv.setText("谨慎操作，导致数据丢失...");
              //  tv.setTextColor(Color.RED);
              //  tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(PasswordManagementActivity.this)
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
                        initReceiveData();
                        //连接蓝牙
                        initConnectBle(0,"");




                    }
                });

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

    public void getdata(final int page) {
        data3=new ArrayList();
        String uid =  SharedUtils.getString("uid");
        Log.d("TAG","uid"+uid);
        Log.d("TAG","LOCKID"+lockid);
        Map<String,String> map=new HashMap<>();
        map.put("pageSize",PAGE_COUNT+"");
        map.put("currentPage",page+"");
        map.put("lockId",lockid);
        map.put("addType","3");
        map.put("addPerson",uid );
        final Gson gson=new Gson();
        String s = gson.toJson(map);

        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.paswManager(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){
                Log.d("TAG",body);
                passwordManagerBean bean = gson.fromJson(body, new TypeToken<passwordManagerBean>() {}.getType());
                DataBeanX data = bean.getData();
                    List<passwordManagerBean.DataBeanX.DataBean> data1 = data.getData();
                    if (data1.size()==0){
                        ll_nodata.setVisibility(View.VISIBLE);
                        pasw_rc.setVisibility(View.GONE);
                        tv_no_datae.setText("暂无密码");
                        Log.d("TAG","没有数据哦 ");
                        iv_no_data.setImageResource(R.mipmap.no_miam);

                    }else if (data1.size()>0){
                        ll_nodata.setVisibility(View.GONE);
                        pasw_rc.setVisibility(View.VISIBLE);


                Iterator<passwordManagerBean.DataBeanX.DataBean> iterator = data1.iterator();
                while (iterator.hasNext()){
                    DataBeanX.DataBean next = iterator.next();
                    data3.add(next);
                }

                adapter=new passwordManagerListAdapter(data3, PasswordManagementActivity.this);
                adapter.setOnItemLongClickListener(new passwordManagerListAdapter.OnItemLongClickListener() {
           @Override
           public void onItemLongClick(View view, final int position, final String id,String unlcokflag,String unlockType) {
               lockType=unlockType;
               lockFlag=unlcokflag;
               Log.d("TAG","点击我了"+position+"id:"+id+unlcokflag);
               View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
               final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
               // et_yanzhenpasw
               EditText et_yanzhenpasw= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
               et_yanzhenpasw.setVisibility(View.GONE);
               TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
              // tv.setText("谨慎操作，导致数据丢失...");
              // tv.setTextColor(Color.RED);
              // tv.setGravity(Gravity.CENTER);
               TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
               final AlertDialog dialog = new AlertDialog.Builder(PasswordManagementActivity.this)
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
                       initReceiveData();
                       initConnectBle(position,id);


                   }
               });


           }
       });
                pasw_rc.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                srf.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 连接蓝牙
     */
    String  strbiaozhi;
    List   bledata=new ArrayList();
    String idlock;
    int p;   // item 位置
    private void initConnectBle(int postin,String id) {
        p=postin;
        idlock=id;
        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            if (!bledata.contains(mac)){
                mBleController.scanBleone(0, new ScanCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","蓝牙扫描结束");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bledata.size()==0){

                                    Toast.makeText(MainApplication.getInstence(), "没有扫描到锁，请重新扫描", Toast.LENGTH_SHORT).show();
                                }else{
                                    if (strbiaozhi.equals("02")){

                                        connect();
                                    }

                                }

                            }
                        });
                        //
                        hideProgressDialog();
                    }

                    @Override
                    public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        showProgressDialog("","正在连接蓝牙...");
                        String address = device.getAddress();
                        if (address.equals(mac)){
                            if (!bledata.contains(mac)){
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
        showProgressDialog("","正在连接蓝牙...");
        mBleController.connect(0, mac, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                jiaoyan();
            }

            @Override
            public void onConnFailed() {

                      hideProgressDialog();
                mBleController.closeBleConn();


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

            }
            @Override
            public void onFailed(int state) {

            }
        });

    }

public void  delectAllPasw(){
    byte[]data80=new byte[16];
    data80[0]=0x04;
    data80[1]=0x05;
    data80[2]=0x01;
    data80[3]=0x06;
    data80[4]=token3[0];
    data80[5]=token3[1];
    data80[6]=token3[2];
    data80[7]=token3[3];
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
     * 删除密码
     */
    private void delectData(String id, String type,String lock) {
        Log.d("TAG","走了id"+id);
        Log.d("TAG","走了type"+type);
        Log.d("TAG","走了lock"+lock);
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager api= retrofit.create(apiManager.class);
        Call<String> call = api.delectunlock(type,id,lock);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){
                    Log.d("TAG","删除"+body);
                    Gson gson=new Gson();
                    msg s= gson.fromJson(body, new TypeToken<msg>() {}.getType());
                    if (s.getCode()==1001){
                        Toast.makeText(MainApplication.getInstence(), "密码删除成功", Toast.LENGTH_SHORT).show();
                        if (StringUtils.isEmpty(lockFlag)){
                            data3.clear();
                        }else {
                            data3.remove(p);
                        }

                        adapter.notifyDataSetChanged();
                    }
                }



            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleController!=null){
            mBleController.closeBleConn();
            mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(bleStateMessage event){
        hideProgressDialog();
        Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败,请重试", Toast.LENGTH_SHORT).show();
        Log.d("TAG","状态刷新");
    }
}
