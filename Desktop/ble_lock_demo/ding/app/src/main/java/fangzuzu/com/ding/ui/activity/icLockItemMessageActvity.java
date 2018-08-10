package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;
import com.hansion.h_ble.event.bleStateMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.utils.byteCunchu.getbyte;

/**
 * Created by lingyuan on 2018/7/20.
 */

public class icLockItemMessageActvity extends BaseActivity {
    Toolbar toolbar;
    TextView tv_name,tv_addphone,tv_time;
    Button but_delet;
    ProgressDialog progressDialog;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "icLockItemMessageActvity";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    private byte[] token3;
    byte[] aesks;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
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
        setContentView(R.layout.ic_item_message_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        mBleController = BleController.getInstance().init(this);
        //获取密钥
        aesks = getbyte("secretKeyBytes");
        allowbyt= getbyte("allowbyt");
        getData();//得到数据从上一个界面传过来的

        EventBus.getDefault().register(this);

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


    String id;
    public void getData() {
        tv_name= (TextView) findViewById(R.id.tv_ic_name);
        tv_addphone= (TextView) findViewById(R.id.tv_ic_phone);
        tv_time= (TextView) findViewById(R.id.tv_ic_time);
        but_delet= (Button) findViewById(R.id.but_ic_delect);
        String unlockName = getIntent().getStringExtra("unlockName");
        String addPerson = getIntent().getStringExtra("addPerson");
        final String unlockType = getIntent().getStringExtra("unlockType");
      id = getIntent().getStringExtra("id");
        Log.d("TAG",unlockName+addPerson+":"+unlockType);
        tv_name.setText(unlockName);
        tv_addphone.setText(addPerson);
        if (unlockType.equals("0")){
            tv_time.setText("限时");
        }else {
            tv_time.setText("永久");
        }
        //删除
        but_delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                tv.setText("谨慎操作，导致数据丢失...");
                tv.setTextColor(Color.RED);
                tv.setGravity(Gravity.CENTER);
                TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                final AlertDialog dialog = new AlertDialog.Builder(icLockItemMessageActvity.this)
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
                        if (unlockType.equals("0")){
                            //删除ic
                            String lockType="3";
                            String sb1=new String();
                            sb1 = lockType.replace("", "0");

                            String type = sb1.substring(0, sb1.length() - 1);
                            Log.d("TAG","拼接allow"+type);

                            final byte[]  lockTp= StringUtils.toByteArray(type);
                            initConnectBle(lockTp,"0");
                        }else if (unlockType.equals("1")){
                            //删除身份证
                            String lockType="4";
                            String sb1=new String();
                            sb1 = lockType.replace("", "0");

                            String type = sb1.substring(0, sb1.length() - 1);
                            Log.d("TAG","拼接allow"+type);

                            final byte[]  lockTp= StringUtils.toByteArray(type);
                            initConnectBle(lockTp,"1");
                        }



                    }
                });

            }
        });
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
                }
                if (decrypt[0]==04&&decrypt[1]==05&&decrypt[3]==00){
                    //设置成功  提交数据到服务器
                    hideProgressDialog();
                    Toast.makeText(MainApplication.getInstence(), "删除数据成功", Toast.LENGTH_SHORT).show();
                    mBleController.closeBleConn();  //断掉蓝牙
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    delectupData(); //删除服务器数据
                }
            }
        });
    }

    /**
     * 删除服务器数据
     */
    private void delectupData() {
        Log.d("TAG","unlock"+unlock);
        String lockid = MainApplication.getInstence().getLockid();
        Log.d("TAG","ockid"+lockid);
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager api= retrofit.create(apiManager.class);
        Call<String> call = api.delectunlock("3",id,"");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG","删除"+body);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 连接蓝牙
     */
    byte[] bylock=new byte[1];
    String unlock; //开锁方式
    private void initConnectBle( byte[]  lockTp,String unlockTY) {
        bylock[0]=lockTp[0];
        unlock=unlockTY;
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
                        delectBleData();
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
    public void  delectBleData(){
        byte[]data80=new byte[16];
        data80[0]=0x04;
        data80[1]=0x05;
        data80[2]=0x01;
        data80[3]=bylock[0];
        data80[4]=token3[0];
        data80[5]=token3[1];
        data80[6]=token3[2];
        data80[7]=token3[3];
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
    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(icLockItemMessageActvity.this, title, message, true, false);
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
    }
}
