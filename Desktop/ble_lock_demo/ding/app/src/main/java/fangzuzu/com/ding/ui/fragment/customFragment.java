package fangzuzu.com.ding.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;
import com.hansion.h_ble.event.bleStateMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.event.passwordMessage;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.widget.DatePicier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static fangzuzu.com.ding.utils.byteCunchu.getbyte;

/**
 * Created by yuanling on 2018/5/15.
 */

public class customFragment extends BaseFragment {
    Button but_set_pasw;
   EditText tv_pasw;
    LinearLayout create_time ,lose_time;
    private TextView currentDate, currentTime;


    CheckBox app_input,lock_input;
    ProgressDialog progressDialog;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "customFragment";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    private byte[] token3=new byte[4];
    byte[] aesks;
    byte[] allowbyt;//锁标识
    byte[]token2=new byte[4];
    @Override
    protected int getLayoutId() {
        return R.layout.custome_fragment_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBleController = BleController.getInstance().init(MainApplication.getInstence());
        aesks = getbyte("secretKeyBytes");
        allowbyt= getbyte("allowbyt");


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(bleStateMessage event){
        hideProgressDialog();
        Toast.makeText(MainApplication.getInstence(), "蓝牙连接失败,请重试", Toast.LENGTH_SHORT).show();
        Log.d("TAG","状态刷新");
    }
    StringBuffer buf;
    private void initReceiveData() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                Log.d("TAG","customFragment"+mBleController.bytesToHexString(decrypt) + "\r\n");
                if (decrypt[0]==04&&decrypt[1]==04&&decrypt[2]==01&&decrypt[3]==00){

                    hideProgressDialog();
                 //   Toast.makeText(MainApplication.getInstence(), "设置密码成功", Toast.LENGTH_SHORT).show();
                    dialog("密码设置成功，请牢记");
                    mBleController.closeBleConn();
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                        String pasw=null;
                        if (!StringUtils.isEmpty(blepasw)){
                            pasw=senddata;
                        }else if (!StringUtils.isEmpty(buf+"")){
                            pasw=buf+"";
                            tv_pasw.setVisibility(View.VISIBLE);
                            tv_pasw.setEnabled(false);
                            tv_pasw.setText(pasw);
                        }
                    updataPasw(pasw);

                }
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
                    hideProgressDialog();
                    Log.d("TAG","token3"+mBleController.bytesToHexString(token3) + "\r\n");
                    for (int i = 0; i < token2.length; i++) {
                        Log.d("TAG","token2Fr"+token2[i]);
                    }
                }
                if (decrypt[0]==04&&decrypt[1]==02&&decrypt[2]==01&&decrypt[3]==00){
                    dialog("请在锁端连续输入2次相同的密码，并以#键结束");
                }
                if (decrypt[0]==04&&decrypt[1]==03&&decrypt[3]==05){
                 byte []byteData=new byte[1];
                    System.arraycopy(decrypt,2,byteData,0,byteData.length);
                    for (int i = 0; i < byteData.length; i++) {
                        Log.d("TAG","长度锁"+byteData[i]);
                    }
                    byte [] jiequmima=new byte[byteData[0]-1];
                    Log.d("TAG","jiequmima集合大小"+jiequmima.length);
                    System.arraycopy(decrypt,4,jiequmima,0,jiequmima.length);
                    for (int i = 0; i < jiequmima.length; i++) {
                        Log.d("TAG","截取密码"+jiequmima[i]);
                    }
                  buf=new StringBuffer();
                    for (int i = 0; i < jiequmima.length; i++) {
                        buf.append(jiequmima[i]);
                    }

                    Log.d("TAG","密码回来值"+buf);
                    sumbitmima(byteData);

                }
            }
        });
    }

    private void sumbitmima(byte []byteData) {
        byte [] data=new byte[16];
        data[0]=0x04;
        data[1]=0x03;
        data[2]=byteData[0];
        data[3]=0x05;
        data[4]=0x00;
        data[5]=token3[0];
        data[6]=token3[1];
        data[7]=token3[2];
        data[8]=token3[3];
        byte[] encrypt11 = jiamiandjiemi.Encrypt(data, aesks);
        byte[] decrypt = jiamiandjiemi.Decrypt(encrypt11, aesks);
        Log.d("TAG", "加密lock" + mBleController.bytesToHexString(decrypt) + "\r\n");

        mBleController.writeBuffer(encrypt11, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "发送成功");
                sendBlezuqi();

            }

            @Override
            public void onFailed(int state) {

            }
        });
    }

    String s2;
    String s4;
    String s1;
    String s3;
    /**
     * 密码传到服务器
     */
    private void updataPasw(String flag) {
        //接收数据
        final String lockName = MainApplication.getInstence().getLockName();
        final String allow = MainApplication.getInstence().getAllow();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid= SharedUtils.getString("uid");
        Log.d("TAG","接收数据"+lockName);
        Log.d("TAG","接收数据"+allow);
        Log.d("TAG","接收数据"+lockid );
        //  s1 = createtime.replaceAll(" ", "-");
        s1 = currentTime.getText().toString().trim().replaceAll(" ", "-");

        Log.d("TAG","s1"+ s1);
        s2 = s1.replaceAll(":", "-");

        Log.d("TAG","s2"+ s2);
        String trim = s2.replaceAll("-", "").trim();
        Log.d("TAG","s2Time"+ trim );

        s3 =   endtime.replaceAll(" ", "-");
        s4 = s3.replaceAll(":", "-");
        Log.d("TAG","s4"+s4);



        Map<String,String> map= new HashMap<>();

        //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}

        map.put("lockId",lockid);
        map.put("unlockName",lockName);
        map.put("unlockFlag",flag);
        map.put("allow",allow);
        map.put("addPerson",uid);
        map.put("forWay","");
        map.put("startTime",s2);
        map.put("endTime",s4);
        map.put("addType","3");
        map.put("unlockType","2");
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
                Gson gson1=new Gson();
                msg s = gson1.fromJson(body, new TypeToken<msg>() {}.getType());
                String data = s.getData();
         //   String regex = "(.{5})";
               // String s1 = data.replaceAll(regex, "$1\t");
                tv_pasw.setText(data);

                Log.d("TAG","上传数据1");
                passwordMessage pamsg=new passwordMessage();
                pamsg.setPassWord(s.getData());
                pamsg.setTimeStart(s2);
                pamsg.setTimeEnd(s4);
                pamsg.setType("2");
                pamsg.setPaswType("限时");
                Log.d("TAG", pamsg.getPassWord());
                EventBus.getDefault().post(pamsg);
                Log.d("TAG","上传数据2");

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime();
        currentDate.setText(endtime);
        Log.d("TAG","event"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime();
        currentTime.setText( createtime);
        Log.d("TAG","event"+currentTime);


    }
    String senddata; //获取输入密码
    @Override
    protected void initViews() {
        currentTime= (TextView) root.findViewById(R.id.electfrg_effect_time);
        currentDate= (TextView) root.findViewById(R.id.electfrg_lose_time);
        create_time= (LinearLayout) root.findViewById(R.id.create_time);
        lose_time=(LinearLayout) root.findViewById(R.id.lose_time);
        DatePicier.initDatePicker(currentDate, currentTime, getActivity());
        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(currentTime.getText().toString());
            }
        });
        lose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker1().show(currentTime.getText().toString());
            }
        });



        tv_pasw= (EditText) root.findViewById(R.id.tv_pasw);
        but_set_pasw= (Button) root.findViewById(R.id.but_set_pasw);
        //输入数字
        but_set_pasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = currentTime.getText().toString().trim();
                String s = unixTime.dateToStampone(time);
                Log.d("TAG","开始时间戳"+s);
                String substring1 = s.substring(0, s.length() - 3);
                int startTime = Integer.parseInt(substring1);
                Log.d("TAG","开始时间"+ startTime);

//  currentDate


                String endtime = currentDate.getText().toString().trim();
                String send = unixTime.dateToStampone(endtime);
                Log.d("TAG","结束时间戳"+send);
                String substring1end = send.substring(0, send.length() - 3);
                int endTime = Integer.parseInt(substring1end);
                Log.d("TAG","结束时间"+ endTime);
                if (StringUtils.isEmpty(endtime)) {
                    Toast.makeText(MainApplication.getInstence(), "请输入失效时间", Toast.LENGTH_SHORT).show();
                }else if (startTime<endTime&&startTime!=endTime){


            // 点击生成密码 连接蓝牙
                String loseTime = currentDate.getText().toString().trim();
                if (app_input.isChecked()){

                senddata = tv_pasw.getText().toString().trim(); //获取输入密码
                Log.d("TAG","senddata"+senddata);

             if (StringUtils.isEmpty(senddata)){

                    Toast.makeText(MainApplication.getInstence(), "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else if(!StringUtils.isEmpty(senddata)&&!StringUtils.isEmpty(endtime)) {
                    initReceiveData();
                    initConnectBle(senddata);

                }
                }else if (lock_input.isChecked()){
                    Log.d("TAG","锁端输入");
                    initReceiveData();
                    initConnectBle("");
                }

                }else {
                    Toast.makeText(getActivity(),"失效时间不能小于生效时间，并且2个时间不能相同",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**
     * 连接蓝牙
     */
String blepasw;
    private void initConnectBle(String senddata) {
        Log.d("TAG","senddata"+senddata);
        blepasw=senddata;
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
                Log.d("TAG","加密身份自定义校验"+mBleController.bytesToHexString(encrypt) + "\r\n");
                byte[] decrypt = jiamiandjiemi.Decrypt(encrypt,aesks);
                Log.d("TAG","解密身份自定义校验"+mBleController.bytesToHexString(decrypt) + "\r\n");
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
        Log.d("TAG","加密token3"+mBleController.bytesToHexString(token3) + "\r\n");
        mBleController.writeBuffer(encrypt40, new OnWriteCallback() {
            @Override
            public void onSuccess() {

                if (!StringUtils.isEmpty(blepasw)){
                    Log.d("TAG","App端");
                    sendDataToBle(blepasw);
                }else {
                    Log.d("TAG","锁端");
                    senddataTOLock();
                }

            }
            @Override
            public void onFailed(int state) {

            }
        });

    }

    //锁端输入
    private void senddataTOLock() {
        byte [] data=new byte[16];
        data[0]=0x04;
        data[1]=0x02;
        data[2]=0x01;
        data[3]=0x05;
        data[4]=token3[0];
        data[5]=token3[1];
        data[6]=token3[2];
        data[7]=token3[3];
        byte[] encrypt11 = jiamiandjiemi.Encrypt(data, aesks);
        byte[] decrypt = jiamiandjiemi.Decrypt(encrypt11, aesks);
        Log.d("TAG", "加密lock" + mBleController.bytesToHexString(decrypt) + "\r\n");

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

    byte[] bytesstartTime;
    byte[] bytesstartendTime;

    private void sendDataToBle(String senddata) {


          String pas1=new String();
        pas1 = senddata.replace("", "0");
        pas1 =  pas1.substring(0,  pas1.length() - 1);
        Log.d("TAG","拼接pas密码"+ pas1);
      //  byte[] datapas = StringUtils.toByteArray(senddata); //转为字节数组

        byte[] datapas = StringUtils.toByteArray(pas1); //转为字节数组

        String paslenth = senddata.length()+1+"";
        String pas=new String();
        pas = paslenth.replace("", "0");
        pas =  pas.substring(0,  pas.length() - 1);
        Log.d("TAG","拼接pas"+ pas);
        byte[] pasbyteslength = StringUtils.toByteArray(pas);
        Log.d("TAG","密码长度"+paslenth);

        Log.d("TAG","存储数据 aesks"+mBleController.bytesToHexString(aesks) + "\r\n");
        Log.d("TAG","存储数据 token3"+mBleController.bytesToHexString(token3) + "\r\n");

        byte [] head=new byte[4];
        head[0]=0x04;
        head[1]=0x01;
        head[2]=pasbyteslength[0];
        head[3]=0x05;
        byte[] bytes = byteCunchu.unitByteArray(head, datapas);
        byte[] bytes1 = byteCunchu.unitByteArray(bytes, token3);
        byte [] data=new byte[16];
        for (int i = 0; i < bytes1.length; i++) {
            data[i]= bytes1[i];
        }
        Log.d("TAG","赋值"+mBleController.bytesToHexString( data) + "\r\n");

        byte[] encrypt11 = jiamiandjiemi.Encrypt(data, aesks);
        Log.d("TAG", "加密" + mBleController.bytesToHexString(encrypt11) + "\r\n");

        mBleController.writeBuffer(encrypt11, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "发送成功");
                sendBlezuqi();

            }

            @Override
            public void onFailed(int state) {

            }
        });



    }

  public  void   sendBlezuqi(){


//设置租期
      String time = currentTime.getText().toString().trim();
      String s = unixTime.dateToStampone(time);
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
        byte[] aesks = byteCunchu.getbyte("secretKeyBytes");
        final byte[] encrypt10 = jiamiandjiemi.Encrypt(data15, aesks);
        Log.d("TAG", "加密" + mBleController.bytesToHexString(encrypt10) + "\r\n");
        Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mBleController.writeBuffer(encrypt10, new OnWriteCallback() {
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




    @Override
    protected void initEvents() {
        app_input= (CheckBox) root.findViewById(R.id.app_input_check);
        lock_input= (CheckBox) root.findViewById(R.id.lock_input_check);

        app_input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lock_input.setChecked(false);
                }else {
                    lock_input.setChecked(true);
                }
            }
        });
        lock_input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    app_input.setChecked(false);
                    dialog("请在锁端连续输入2次相同的密码，并以#键结束");
                    tv_pasw.setVisibility(View.INVISIBLE);

                }else {
                    app_input.setChecked(true);
                    tv_pasw.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    private void dialog(String text) {
        View viewDialog = getActivity().getLayoutInflater().inflate(R.layout.custom_diaglog_deviceslayut, null);
        final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
        TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
        tv.setText(text);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        TextView tv_de_me= (TextView)viewDialog.findViewById(R.id.tv_de_me);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(viewDialog)
                .create();
        dialog.show();
        tv_de_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void initData() {

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
}
