package fangzuzu.com.ding.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;
import com.hansion.h_ble.callback.ScanCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.DeviceListAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.bleBean;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/5/21.
 */

public class addSmartActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    Toolbar toolbar;
    ImageView add_smart_iv;
    RecyclerView add_smart_lv;
    String name;
    ImageView iv_no_data;
    TextView tv_no_data;
    LinearLayout ll_no_data;
    ProgressDialog progressDialog;
    //搜索结果列表
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
    public List bledata=new ArrayList();
    private ListView mDeviceList;
    private BleController mBleController;
    DeviceListAdapter adapter;
    TextView tv;
    private ArrayList<byte[]> mRecords;//新加
    public  int REQUEST_ACCESS_COARSE_LOCATION=1;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "addSmartActivity";
    byte []src=new byte[20];
    byte[]lockid=new byte[6];
    byte[]blemanager=new byte[8];
    byte[]aesk=new byte[16];
    byte[]shengfenjiaoyan=new byte[4];
    byte[]jiesouTock=new byte[16];
    byte[]token2=new byte[4];
    final  byte[]token=new byte[4];

    String uid;
   String partid;
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
        setContentView(R.layout.add_smart_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        uid=SharedUtils.getString("uid");
        partid=SharedUtils.getString("partid");
        Log.d("TAG","uid"+uid);
        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(addSmartActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
//向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(addSmartActivity.this,"打开权限才能用哦", Toast.LENGTH_SHORT).show();
                }
            }
        }

        initlize();
        initEvent();
        // TODO  第一步：初始化
        mBleController = BleController.getInstance().init(this);
        // TODO  第二步：搜索设备，获取列表后进行展示
        mBleController.openBle();
        initreceiveBleData();
        scanDevices();

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
     * 接收蓝牙数据
     */
    private void initreceiveBleData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                if (value.length!=0){
                mReciveString.append(mBleController.bytesToHexString(value) + "\r\n");
                Log.d("TAG",mReciveString.toString());
                src=value;
                Log.d("TAG","解密"+mBleController.bytesToHexString(value) + "\r\n");

                if (value[2]==0x06){
                    System.arraycopy(value,3,lockid,0,lockid.length);
                    byteCunchu.put(lockid,"lockid");
                    allowSucess();
                    for (int i = 0; i < lockid.length; i++) {
                        Log.d("TAG","id"+lockid[i]);

                    }
                }

                if (value[1]==03){
                    System.arraycopy(value,3,blemanager,0,blemanager.length);
                    byteCunchu.put(blemanager,"blemanager");
                    adminSucess();
                    for (int i = 0; i < blemanager.length; i++) {
                        Log.d("TAG","idh"+blemanager[i]);
                    }
                }

                if (value[1]==04){
                    System.arraycopy(value,3,aesk,0,aesk.length);
                    byteCunchu.put(aesk,"aesk");
                    askSucess();
                //
                    for (int i = 0; i < aesk.length; i++) {
                        Log.d("TAG","aesk"+aesk[i]);
                    }
                }


                if (value[2]==04){
                    System.arraycopy(value,3,shengfenjiaoyan,0,shengfenjiaoyan.length);
                    for (int i = 0; i < shengfenjiaoyan.length; i++) {
                        Log.d("TAG","shengfenjiaoyan"+shengfenjiaoyan[i]);
                    }
                }
            if (value[0]==01&&value[1]==04&&value[2]==01&&value[3]==0){
            mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                mBleController.closeBleConn();


            }
                if (value[0]!=01){
                    jiesouTock=value;
                    for (int i = 0; i <jiesouTock.length; i++) {
                        Log.d("TAG","接收密钥"+jiesouTock[i]);
                    }
                    byte[] aesks = byteCunchu.getbyte("aesk");
                    byte[] decrypt = jiamiandjiemi.Decrypt(jiesouTock, aesks);

                    Log.d("TAG","解密"+mBleController.bytesToHexString(decrypt) + "\r\n");

                }

            }
            }
        });


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




    private StringBuffer mReciveString = new StringBuffer();
    String strbiaozhi; //广播数据的标志  00  01   02
    private void scanDevices() {

    //    showProgressDialog("", "正在搜索设备");

        mBleController.scanBle(0, new ScanCallback() {
            @Override
            public void onSuccess() {
                hideProgressDialog();
               if (bluetoothDevices.size() > 0) {
                   ll_no_data.setVisibility(View.GONE);
                       mDeviceList.setAdapter(new DeviceListAdapter(addSmartActivity.this, bledata));
                    //   tv.setText("设备列表");
                       mDeviceList.setOnItemClickListener(addSmartActivity.this);

                } else {
                 //  lv.setVisibility(View.GONE);
                   ll_no_data.setVisibility(View.VISIBLE);
                   iv_no_data.setImageResource(R.mipmap.un_shebei);
                   tv_no_data.setText("暂无设备");
                    Toast.makeText(addSmartActivity.this, "未搜索到Ble设备", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {


                String string1 = mBleController.bytesToHexString(scanRecord);
                final String address = device.getAddress();
                final String name1 = device.getName();

                Log.d("TAG", "蓝牙设备" + name1 +"地址"+address+"广播数据"+string1);
                String str = string1.replaceAll(" ", "").trim();
                if (str.indexOf("5453")!=-1){
                        if (!bluetoothDevices.contains(device)) {
                            addSmartActivity.this.name = device.getName();
                            int length = str.length();
                            Log.d("TAG", "长度" + length);
                            Log.d("TAG", "蓝牙设备" + addSmartActivity.this.name);
                           bluetoothDevices.add(device);
                           String[] split = str.split("5453");
                            Log.d("TAG","切割后面的"+split[1]);
                         strbiaozhi= split[1].substring(14, 16);
                            Log.d("TAG","要的"+strbiaozhi);
                            bleBean bean=new bleBean();
                            bean.setName(device.getName());
                            bean.setMac(device.getAddress());
                            bean.setBiaozhi(strbiaozhi);
                            bledata.add(bean);
                        }
                    }



            }

        });
    }


    private void initEvent() {

    }

    //str 源字符串
//strStart 起始字符串
//strEnd 结束字符串


    public  String  getInsideString(String  str, String strStart, String strEnd ) {
        if ( str.indexOf(strStart) < 0 ){
            return "";
        }
        if ( str.indexOf(strEnd) < 0 ){
            return "";
        }
        return str.substring(str.indexOf(strStart) + strStart.length(), str.indexOf(strEnd));
    }


    private void initlize() {
       /* tv= (TextView) findViewById(R.id.tv);
        tv.setText("搜索设备");*/
      //  add_smart_iv= (ImageView) findViewById(R.id.add_smart_iv);

        ll_no_data=(LinearLayout) findViewById(R.id.ll_nodata);
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        mDeviceList= (ListView) findViewById(R.id.add_smart_lv);
       adapter=new DeviceListAdapter(this, bluetoothDevices);
        //lv.setAdapter(adapter);
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
        mBleController.closeBleConn();
        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
        hideProgressDialog();

    }



    public void showProgressDialog(String title, String message) {
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
    String contentName;
    String address;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        NetWorkTesting net=new NetWorkTesting(MainApplication.getInstence());
        bleBean o1 = (bleBean) bledata.get(i);
        String guangboshuju = o1.getBiaozhi();
        Log.d("TAG","得到广播数据"+guangboshuju );
        if (net.isNetWorkAvailable()){
        Log.d("TAG","得到数据"+strbiaozhi);
          if (guangboshuju.equals("00")){
                Log.d("TAG","请激活");
            }else if (guangboshuju.equals("02")){
                Log.d("TAG","已添加");
            }else if (guangboshuju.equals("01")){

        showProgressDialog("", "正在连接设备");

        // TODO 第三步：点击条目后,获取地址，根据地址连接设备
        //  address = bluetoothDevices.get(i).getAddress();


              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      bleBean o = (bleBean)bledata.get(i);
                      String mac = o.getMac();
                      address=mac;
                      Log.d("TAG","对应地址"+mac+":"+i);
                mBleController.connect(0, mac , new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                View view = getLayoutInflater().inflate(R.layout.lock_diaage_layout, null);
                final EditText editText = (EditText) view.findViewById(R.id.dialog_editname);
               TextView tv_cancle= (TextView) view.findViewById(R.id.add_cancle);
                TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(addSmartActivity.this)
                        .setView(view)
                    .create();
                dialog.setCanceledOnTouchOutside(false);
                Window window=dialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                WindowManager manager=getWindowManager();
                Display defaultDisplay = manager.getDefaultDisplay();
                android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
                p.width= (int) (defaultDisplay.getWidth()*0.85);
                dialog.getWindow().setAttributes(p);     //设置生效
                tv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        hideProgressDialog();
                        mBleController.closeBleConn();
                    }
                });
                tv_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentName = editText.getText().toString().trim();
                        if (StringUtils.isEmpty(contentName)){
                            Toast.makeText(addSmartActivity.this, "请给设备命名", Toast.LENGTH_SHORT).show();
                        }else {
                            hideProgressDialog();
                            Toast.makeText(addSmartActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        createBleManager();//创建蓝牙管理员
                        /* Intent intent=new Intent(addSmartActivity.this,addBleManageActivity.class);
                                intent.putExtra("name",contentName);
                                intent.putExtra("address",address);
                                intent.putExtra("userId",uid);
                                startActivity(intent);*/
                            dialog.dismiss();
                        }
                    }
                });


            }
                    @Override
                    public void onConnFailed() {
                            Toast.makeText(addSmartActivity.this, "连接超时，请重试", Toast.LENGTH_SHORT).show();
                            mBleController.closeBleConn();
                            hideProgressDialog();




                    }

        });

                  }
              });
            }
        }else {
            Toast.makeText(addSmartActivity.this, "当前网络不可用，请检查您的网络!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建蓝牙管理员
     */
    private void createBleManager() {

        final byte[]data={0x01,0x01,0x02,0x06,0x08,0x08,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf};

        mBleController.writeBuffer(data, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","发送成功");

            }
            @Override
            public void onFailed(int state) {

            }
        });

    }

public  void allowSucess(){
            //锁身份标识成功
            final byte[]data1={0x01,0x02,0x01,0x00,0x00,0x00,0x00,0x00,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf};
            mBleController.writeBuffer(data1, new OnWriteCallback() {
                @Override
                public void onSuccess() {
                    Log.d("TAG","发送成功");

                }
                @Override
                public void onFailed(int state) {

                }
            });

}

public void adminSucess(){
    //管理员密码成功
    final byte[]data2={0x01,0x03,0x01,0x00,0x00,0x00,0x00,0x00,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf};
    mBleController.writeBuffer(data2, new OnWriteCallback() {
        @Override
        public void onSuccess() {
            Log.d("TAG","发送成功");
        }
        @Override
        public void onFailed(int state) {

        }
    });
}

public void askSucess(){
    //返回密钥成功
    final byte[]data3={0x01,0x04,0x01,0x00,0x00,0x00,0x00,0x00,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf};
    mBleController.writeBuffer(data3, new OnWriteCallback() {
        @Override
        public void onSuccess() {
            Log.d("TAG","发送返回密钥成功");
           upData();
          //
            Timer timer =new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mBleController.closeBleConn();
                }
            },1000);
        }
        @Override
        public void onFailed(int state) {
        }
    });
}



    /**
     * 上传蓝牙管理员数据
     */
    public  void upData(){
        byte[] blemanagers = byteCunchu.getbyte("blemanager");
        StringBuffer bufferblemanager=new StringBuffer();
        for (int i = 0; i < blemanagers.length; i++) {
            Log.d("TAG","管理员"+blemanagers[i]);
            bufferblemanager.append((blemanagers[i]));
        }
        StringBuffer bufferlock=new StringBuffer();
        byte[] lockid = byteCunchu.getbyte("lockid");
        for (int i = 0; i < lockid.length; i++) {
            bufferlock.append(lockid[i]);
        }


        final String aesk1 =mBleController.bytesToHexString(aesk).toString().trim();
        String aesk11 = aesk1.replaceAll(" ", "");


        //上传数据
        Map<String,String> map=new HashMap();
        map.put("adminUserId",uid);
        map.put("lockNumber",address);
        map.put("lockName",contentName);
        map.put("allow",bufferlock+"");
        map.put("electricity","80");
        map.put("roomId","");
        map.put("adminPsw",bufferblemanager+"");
        map.put("secretKey",aesk11);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG","上传json"+s);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Call<String> call = manager.initLockMolde(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                //调用权限
                Log.d("TAG",body);
               msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                String lockid = s.getData(); //锁id
                authPeople(lockid);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    private void authPeople(String lockid) {
        String[]str={"f37618bc-686d-11e8-b04f-00163e0c1269","83a3378a-7b89-11e8-9505-00163e06d99e"};
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        final Gson gson=new Gson();
       String id=null;
        if (StringUtils.isEmpty(partid)){
            id=uid;
        }else {
            id=partid;
        }
        Call<String> call = manager.BindingPermissions(id,lockid);
                call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG", "授权" + body);
                msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = s.getCode();
                if (code==1001){
                    //  Toast.makeText(sendKeyActivity.this,"发送成功",Toast.LENGTH_LONG).show();
                    Log.d("TAG", "绑定权限成功" + body);
                    Toast.makeText(addSmartActivity.this, "创建蓝牙管理员成功", Toast.LENGTH_SHORT).show();
                    //界面跳转
                    Intent intent =new Intent(addSmartActivity.this,lockListActivity.class);
                    startActivity(intent);
                    finish();

                }else if (code==1002){
                    // Toast.makeText(sendKeyActivity.this,"发送失败",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
