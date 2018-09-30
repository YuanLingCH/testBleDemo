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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.ble.jiamiandjiemi;
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
 * Created by lingyuan on 2018/6/22.
 */

public class upDataManagerPaswActivity extends BaseActivity {
    Toolbar toolbar;
    EditText et_pasw;
    Button but_submit;
    ProgressDialog progressDialog;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "upDataManagerPaswActivity";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    private byte[] token3=new byte[4];;
    byte[] aesks;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
    byte[] adminPswBytes;
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
        setContentView(R.layout.up_manager_pasw_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        aesks = getbyte("secretKeyBytes");
        allowbyt= getbyte("allowbyt");
        adminPswBytes = getbyte("adminPswBytes");
        mBleController = BleController.getInstance().init(this);
        initlize();
        setStatusBar();

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



    private void initlize() {
        et_pasw= (EditText) findViewById(R.id.et_pasw);
        but_submit= (Button) findViewById(R.id.but_submit);  //确定修改蓝牙管理员密码
        but_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pasw = et_pasw.getText().toString().trim();
                if (!StringUtils.isEmpty(pasw)){
                    initReceiveData();
                    initConnectBle();
                }else {
                    Toast.makeText(upDataManagerPaswActivity.this, "密码输入为空", Toast.LENGTH_SHORT).show();
                }


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

                    byte[]token1=new byte[4];
                    token1[0]=02;
                    token1[1]=03;
                    token1[2]=04;
                    token1[3]=05;
                    token3[0]= (byte) (token2[0]^token1[0]);
                    token3[1]= (byte) (token2[1]^token1[1]);
                    token3[2]= (byte) (token2[2]^token1[2]);
                    token3[3]= (byte) (token2[3]^token1[3]);
                    Log.d("TAG","token"+mBleController.bytesToHexString(token3) + "\r\n");
                }if (decrypt[0]==02&&decrypt[1]==02&&decrypt[2]==04&&decrypt[3]==00){
                    xiugaimima();
                }if (decrypt[0]==03&&decrypt[1]==01&&decrypt[2]==01&&decrypt[3]==00){
                    submitPasw();
                }if (decrypt[0]==03&&decrypt[1]==02&&decrypt[2]==01&&decrypt[3]==00){
                    //修改服务器数据
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    mBleController.closeBleConn();

                    hideProgressDialog();
                    upDataPasw();
                }

            }
        });
    }

    private void upDataPasw() {
        /**
         * 修改锁名
         */

            String lockid = MainApplication.getInstence().getLockid();
            String uid = MainApplication.getInstence().getUid();
            String elect = MainApplication.getInstence().getElect();

        String lockName = MainApplication.getInstence().getLockName();
        Map<String,String> map=new HashMap<>();
            map.put("id",lockid );
            map.put("adminUserId",uid );
            map.put("lockName",lockName );
            map.put("adminPsw",newpasw );
            map.put("electricity",elect );
            final Gson gson=new Gson();
            String s = gson.toJson(map);
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
                        Toast.makeText(upDataManagerPaswActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

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
    public void  xiugaimima(){
        String length = adminPswBytes.length+"";
        String pas=new String();
        pas = length.replace("", "0");
        pas =  pas.substring(0,  pas.length() - 1);
        Log.d("TAG","拼接pas"+ pas);
        byte[] pasbyteslength = StringUtils.toByteArray(pas);
        Log.d("TAG","密码长度"+length);
        byte[]header=new byte[3];
        header[0]=0x03;
        header[1]=0x01;
        header[2]=pasbyteslength[0];
        byte[] bytes = byteCunchu.unitByteArray(header, adminPswBytes);
        Log.d("TAG","加密"+mBleController.bytesToHexString(bytes) + "\r\n");
        byte[] bytes1 = byteCunchu.unitByteArray(bytes, token3);
        Log.d("TAG","加密token3old"+mBleController.bytesToHexString( token3) + "\r\n");
        Log.d("TAG","加密"+mBleController.bytesToHexString( bytes1) + "\r\n");
        byte[]data16=new byte[16];
        for (int i = 0; i < bytes1.length; i++) {
            data16[i]= bytes1[i];
        }
        Log.d("TAG","赋值"+mBleController.bytesToHexString( data16) + "\r\n");

        byte[] encrypt11 = jiamiandjiemi.Encrypt(data16, aesks);
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
    String newpasw;
    public void submitPasw(){
        newpasw = et_pasw.getText().toString().trim();
        String length = newpasw.length()+"";
        Log.d("TAG",length+"密码长度");
        String pas=new String();
        pas = length.replace("", "0");
        pas =  pas.substring(0,  pas.length() - 1);
        Log.d("TAG","拼接pas"+ pas);
        byte[] pasbyteslength = StringUtils.toByteArray(pas);
        Log.d("TAG","密码长度"+length);

        String sb=new String();
        for (int i = 0; i < newpasw.length(); i++) {
            sb = newpasw.replace("", "0");
        }
       String  adminPsw = sb.substring(0, sb.length() - 1);
        Log.d("TAG","拼接paw"+adminPsw);


        byte[] byteNewpasw = StringUtils.toByteArray(adminPsw);
        Log.d("TAG","加密,,"+mBleController.bytesToHexString(byteNewpasw) + "\r\n");
        byte[]header=new byte[3];
        header[0]=0x03;
        header[1]=0x02;
        header[2]=pasbyteslength[0];
        byte[] bytes = byteCunchu.unitByteArray(header, byteNewpasw);
        Log.d("TAG","加密"+mBleController.bytesToHexString(bytes) + "\r\n");
        byte[] bytes1 = byteCunchu.unitByteArray(bytes, token3);
        Log.d("TAG","加密token3new"+mBleController.bytesToHexString( token3) + "\r\n");
        Log.d("TAG","加密"+mBleController.bytesToHexString( bytes1) + "\r\n");
        byte[]data16=new byte[16];
        for (int i = 0; i < bytes1.length; i++) {
            data16[i]= bytes1[i];
        }
        Log.d("TAG","赋值new"+mBleController.bytesToHexString( data16) + "\r\n");

        byte[] encrypt11 = jiamiandjiemi.Encrypt(data16, aesks);
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
            progressDialog = ProgressDialog.show(upDataManagerPaswActivity.this, title, message, true, false);
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

    }

}
