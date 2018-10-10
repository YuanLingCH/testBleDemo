package fangzuzu.com.ding.ui.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;
import com.hansion.h_ble.callback.ScanCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.dfu.DfuService;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static fangzuzu.com.ding.utils.byteCunchu.getbyte;

/**
 * Created by lingyuan on 2018/8/21.
 */

public class dfuActivity extends BaseActivity {
    Toolbar toolbar;
    boolean isKitKat = false;
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    private byte[] token3;
    byte[] aesks;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
    byte[] adminPswBytes;//管理员
    ProgressDialog progressDialog;

    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "dfuActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.dfu_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        lockNumber = MainApplication.getInstence().getMac();
        initlize();
        //初始化蓝牙
        mBleController = BleController.getInstance();


    }

  private void initlize() {
      //获取密钥
      aesks = getbyte("secretKeyBytes");
      allowbyt= getbyte("allowbyt");
      adminPswBytes = byteCunchu.getbyte("adminPswBytes");

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
    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
        if (value!=null&&value.length!=0){
                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","解密dfuActivity"+mBleController.bytesToHexString(decrypt) + "\r\n");
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
                if (decrypt[0]==03&&decrypt[1]==03&&decrypt[2]==01&&decrypt[3]==00){
               //同步时间完成后 进入dfu模式
                    bleDfu();


                }
                if (decrypt[1]==01&&decrypt[2]==01&&decrypt[3]==00){
                    // 进入成功后 重新扫描
                   scanBle();
                    Log.d("TAG","从新扫描");
                }
            }
            }
        });

    }

    /**
     * 进入成功后 重新扫描
     */
    String name;
    List dataname=new ArrayList();

List addressDfu=new ArrayList();
    private void scanBle() {
        Log.d("TAG","扫描开始");
        mBleController.scanBle(4000, new ScanCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","扫描结束");
                // 进入升级模式  名字会改变    DfuTarg
                hideProgressDialog();

                if (dataname.contains("H_DFU")){

            startDFU();  // 启动升级

                }else {
                    Log.d("TAG","正常情况");
                    connectBle();  //  正常模式
                }


            }

            @Override
            public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {

                String named = device.getName();
                Log.d("TAG","升级扫描"+"名字"+ named);
                if (!dataname.contains(named)){
                    dataname.add(named);
                }

                if (!StringUtils.isEmpty(named)){
                    if (named.equals("H_DFU")){
                    String address1 = device.getAddress();

                    if (!addressDfu.contains(address1)){
                        addressDfu.add(address1 );
                    }

                        Log.d("TAG","重新扫描"+"名字"+ named+"蓝牙地址"+address1 );
                }

                }

            }
        });
    }

    /**
     * 设备进入dfu模式
     */
    private void bleDfu() {
        byte[]datadfu=new byte[16];
        datadfu[0]=0x10;
        datadfu[1]=0x01;
        datadfu[2]=0x06;
        datadfu[3]=adminPswBytes[0];
        datadfu[4]=adminPswBytes[1];
        datadfu[5]=adminPswBytes[2];
        datadfu[6]=adminPswBytes[3];
        datadfu[7]=adminPswBytes[4];
        datadfu[8]=adminPswBytes[5];
        datadfu[9]=token3[0];
        datadfu[10]=token3[1];
        datadfu[11]=token3[2];
        datadfu[12]=token3[3];
        byte[] encrypt = jiamiandjiemi.Encrypt(datadfu,  aesks);
        Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt) + "\r\n");

        mBleController.writeBuffer(encrypt, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","dfu发送成功");
            //  scanBle();

            }
            @Override
            public void onFailed(int state) {
                Log.d("TAG","dfu发送失败"+state);
                hideProgressDialog();
            }
        });
    }

    /**
     * 连接蓝牙
     */
    String lockNumber;
    private void initConnectBle() {

        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            scanBle();


        }
    }


    public void connectBle(){

       // showProgressDialog("","正在连接蓝牙...");
     //   mStringBuilder.append("正在连接蓝牙...");
        tv.setText("正在连接蓝牙...");
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

                    Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败，确认手机在锁旁边", Toast.LENGTH_SHORT).show();
                    mBleController.closeBleConn();
                    hideProgressDialog();
                    tv.setText("蓝牙连接失败,请重试");




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
                        hideProgressDialog();
                        mBleController.closeBleConn();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleController!=null){
            mBleController.closeBleConn();
            mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
        }

    }
    /**
     * 点击连接蓝牙检查版本号  固件版本小于服务器版本就升级  ，否则提示用户已是最新版本
     *
     * 连接蓝牙进去dfu模式
     * @param view
     */
    private StringBuilder mStringBuilder;
 TextView tv;
    AlertDialog dialog;
    public void butClick(View view) {
        mStringBuilder = new StringBuilder();
        Log.d("TAG","点击升级");
        // 弹出对话框  提示用户 正在升级


        View viewname = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
       tv = (TextView) viewname.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) viewname.findViewById(R.id.add_cancle);
        EditText et_yanzhenpasw= (EditText) viewname.findViewById(R.id.et_yanzhenpasw);
        et_yanzhenpasw.setVisibility(View.INVISIBLE);
        TextView tv1= (TextView) viewname.findViewById(R.id.tv);
        tv1.setVisibility(View.INVISIBLE);
      //  mStringBuilder.append("给锁升级,需要一分钟左右，请勿离开锁");
        tv.setText("给锁升级,需要一分钟左右，请勿离开锁");
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER);
        TextView tv_submit= (TextView) viewname.findViewById(R.id.add_submit);
        dialog = new AlertDialog.Builder(dfuActivity.this)
                .setView(viewname)
                .create();
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

            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initReceiveData();
                initConnectBle();

            }
        });










    }



    /**
     * ota升级的监听
     */
    private final DfuProgressListener dfuproListenner=new DfuProgressListener() {
    @Override
    public void onDeviceConnecting(String deviceAddress) {
        //DFU服务开始与DFU目标连接
        Log.d("TAG","DFU服务开始与DFU目标连接");
    }
// 开始连接设备
    @Override
    public void onDeviceConnected(String deviceAddress) {
        Log.d("TAG","开始连接设备");
    }
        //升级准备开始的时候调用
    @Override
    public void onDfuProcessStarting(String deviceAddress) {
        Log.d("TAG","升级准备开始的时候调用");
      //  mStringBuilder.append("准备升级");
        tv.setText("准备升级");
    }
        //设备开始升级
    @Override
    public void onDfuProcessStarted(String deviceAddress) {
        Log.d("TAG","设备开始升级");
    }

    @Override
    public void onEnablingDfuMode(String deviceAddress) {
        Log.d("TAG","当服务发现DFU目标处于应用程序模式并且必须切换到DFU模式时调用的方");
    }
        //升级过程中的回调
    @Override
    public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
        Log.d("TAG","升级过程中的回调"+"percent"+percent+"speed"+speed+"avgSpeed"+avgSpeed+"currentPart"+currentPart+"partsTotal"+partsTotal);
       // mStringBuilder.append("正在升级"+percent+"%");
        tv.setText("正在升级"+percent+"%");



    }
        //固件验证
    @Override
    public void onFirmwareValidating(String deviceAddress) {
        Log.d("TAG","固件验证");
    }
        //设备正在断开
    @Override
    public void onDeviceDisconnecting(String deviceAddress) {
        Log.d("TAG","设备正在断开");
    }
        //设备已经断开
    @Override
    public void onDeviceDisconnected(String deviceAddress) {
        Log.d("TAG","设备已经断开");
      mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
    }
        //升级完成
    @Override
    public void onDfuCompleted(String deviceAddress) {
        Log.d("TAG","升级完成");
        Toast.makeText(MainApplication.getInstence(), "锁升级完成", Toast.LENGTH_SHORT).show();
        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
      //  mStringBuilder.append("升级完成");
        tv.setText("升级完成");
        dialog.dismiss();
    }

    @Override
    public void onDfuAborted(String deviceAddress) {
        Log.d("TAG","当DFU进程已中止时调用的方法");
        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);

    }
        //升级失败
    @Override
    public void onError(String deviceAddress, int error, int errorType, String message) {
        Log.d("TAG","升级失败");
        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
        Toast.makeText(MainApplication.getInstence(), "锁升级失败", Toast.LENGTH_SHORT).show();
        tv.setText("升级失败");
        dialog.dismiss();
    }
};


    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuproListenner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuproListenner);
    }
    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(dfuActivity.this, title, message, true, false);
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
     * 启动DFU升级服务
     *
     * @param //bluetoothDevice 蓝牙设备
     * @param //keepBond        升级后是否保持连接
     * @param //force           将DFU设置为true将防止跳转到DFU Bootloader引导加载程序模式
     * @param //PacketsReceipt  启用或禁用数据包接收通知（PRN）过程。
     *                        默认情况下，在使用Android Marshmallow或更高版本的设备上禁用PEN，并在旧设备上启用。
     * @param //numberOfPackets 如果启用分组接收通知过程，则此方法设置在接收PEN之前要发送的分组数。 PEN用于同步发射器和接收器。
     * @param //filePath        约定匹配的ZIP文件的路径。
     */
    private void startDFU() {
        String addDFU=null;
        Iterator iterator = addressDfu.iterator();
        while (iterator.hasNext()){
            addDFU = (String) iterator.next();
        }
        Log.d("TAG","升级蓝牙地址"+addDFU);
        final DfuServiceInitiator stater = new DfuServiceInitiator(addDFU)
                .setDeviceName("H_DFU"+addDFU)
                .setKeepBond(true)
                .setForceDfu(true);
             // .setPacketsReceiptNotificationsEnabled(PacketsReceipt);
              //  .setPacketsReceiptNotificationsValue(numberOfPackets);
        stater.setZip(R.raw.app_dfu_android);//这个方法可以传入raw文件夹中的文件、也可以是文件的string或者url路径。
        stater.start(this, DfuService.class);
    }

}
