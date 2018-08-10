package fangzuzu.com.ding.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;

import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.utils.byteCunchu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/5/30.
 *
 *
 *
 *
 */

public class addBleManageActivity extends BaseActivity {
    Toolbar toolbar;
    private BleController mBleController;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "addBleManageActivity";
    private StringBuffer mReciveString = new StringBuffer();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ble_manage_activity);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initgetuid();
        // TODO 在新的界面要获取实例，无需init
        mBleController = BleController.getInstance();
        initview();
        initlize();






    }


    String uid;
    String name;
    String address;
    private void initgetuid() {
       // MainApplication app = (MainApplication )getApplication();
        // uid = app.getUid();
        name = getIntent().getStringExtra("name");
        Log.d("TAG","名字"+name);
        address = getIntent().getStringExtra("address");
        uid=getIntent().getStringExtra("userId");
    }

    private void initview() {
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {

                mReciveString.append(mBleController.bytesToHexString(value) + "\r\n");
                Log.d("TAG",mReciveString.toString());
                src=value;
                if (value[2]==0x06){
                    System.arraycopy(value,3,lockid,0,lockid.length);
                    byteCunchu.put(lockid,"lockid");
                  upData();
                    for (int i = 0; i < lockid.length; i++) {
                        Log.d("TAG","id"+lockid[i]);

                    }
                }

                if (value[1]==03){
                    System.arraycopy(value,3,blemanager,0,blemanager.length);
                    byteCunchu.put(blemanager,"blemanager");
                    for (int i = 0; i < blemanager.length; i++) {
                        Log.d("TAG","idh"+blemanager[i]);
                    }
                }

                if (value[1]==04){
                    System.arraycopy(value,3,aesk,0,aesk.length);
                    byteCunchu.put(aesk,"aesk");


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
                if (value[0]!=01){
                    jiesouTock=value;
                    for (int i = 0; i <jiesouTock.length; i++) {
                        Log.d("TAG","接收密钥"+jiesouTock[i]);
                    }
                    byte[] aesks = byteCunchu.getbyte("aesk");
                    byte[] decrypt = jiamiandjiemi.Decrypt(jiesouTock, aesks);
                    Log.d("TAG","解1密"+mBleController.bytesToHexString(decrypt) + "\r\n");
                    if (decrypt[0]==02&&decrypt[1]==01&&decrypt[2]==04){
                        System.arraycopy(decrypt,3,token2,0,token2.length);
                        byte[]token1=new byte[4];
                        token1[0]=02;
                        token1[1]=03;
                        token1[2]=04;
                        token1[3]=05;
                        token[0]= (byte) (token2[0]^token1[0]);
                        token[1]= (byte) (token2[1]^token1[1]);
                        token[2]= (byte) (token2[2]^token1[2]);
                        token[3]= (byte) (token2[3]^token1[3]);
                        Log.d("TAG","解密aaatoken"+mBleController.bytesToHexString(token) + "\r\n");

                        byteCunchu.put(token,"token");
                        for (int i = 0; i < token2.length; i++) {
                            Log.d("TAG","token2"+token2[i]);
                        }
                    }

                    if (decrypt[0]==03&&decrypt[1]==01&&decrypt[2]==01){
                        System.arraycopy(decrypt,3,mimastate,0,mimastate.length);
                        for (int i = 0; i < decrypt.length; i++) {
                            Log.d("TAG","mimastateff"+decrypt[i]);
                        }
                        for (int i = 0; i < mimastate.length; i++) {
                            Log.d("TAG","mimastate"+mimastate[i]);
                        }

                    }

                }

                if (value[0]==04&&value[1]==02){
                    wuanjian=value;
                }



            }
        });


    }




    byte []zhuqifanwei=new byte[20];
    byte []src=new byte[20];
    byte[]lockid=new byte[6];
    byte[]blemanager=new byte[8];
     byte[]aesk=new byte[16];
    byte[]shengfenjiaoyan=new byte[4];
    byte[]jiesouTock=new byte[16];
    byte[]token2=new byte[4];
    final  byte[]token=new byte[4];
    byte[]batt=new byte[6];
    byte[]mimastate=new byte[1];
    byte [] wuanjian=new byte[5];
     //接收返回数据监听
    private void initlize() {
        //  01 04 10 39 42 33 25 70 6D 54 00 4D 60 5A 0D 7C 63 3C 34


    }



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
            Log.d("TAG","管理员"+lockid[i]);
            bufferlock.append((lockid[i]));
        }

        final String ble = mBleController.bytesToHexString(blemanager).toString().trim();
        String ble1 = ble.replaceAll(" ", "");

        final String aesk1 =mBleController.bytesToHexString(aesk).toString().trim();
        String aesk11 = aesk1.replaceAll(" ", "");
        final String lock = mBleController.bytesToHexString(lockid).toString().trim();
        String lock1 = lock.replaceAll(" ", "");

        //上传数据
        Map<String,String> map=new HashMap();
        map.put("adminUserId",uid);
        map.put("lockNumber",address);
        map.put("lockName",name);
        map.put("allow",bufferlock+"");
        map.put("electricity","80");
        map.put("roomId","");
        map.put("adminPsw",bufferblemanager+"");
        map.put("secretKey",aesk11);
        Gson gson=new Gson();
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

             Log.d("TAG",body);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 点击添加蓝牙管理员
     * @param view
     */
    public void butClick(View view) {
        switch (view.getId()){
            case R.id.bleManager:
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


              //锁身份标识成功
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


                //返回密钥成功
                final byte[]data3={0x01,0x04,0x01,0x00,0x00,0x00,0x00,0x00,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf};
                mBleController.writeBuffer(data3, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");



                    }
                    @Override
                    public void onFailed(int state) {
                    }
                });
              /*  01 01 01 00 00 00 00 00 00 00 00 00 00 00 00 00
                01 03 09 07 07 03 09 09 01 03 03 05 00 00 00 00
                01 04 10 17 0A 55 5F 6B 60 76 6B 5C 28 30 6D 6A 29 0B 44
                01 02 07 4B DA 6D 07 0A 86 1C 00 00 00 00 00 00*/
                break;

            case R.id.shengfenjiaoyan:
                //返回密钥成功a
                byte[] aesks = byteCunchu.getbyte("aesk");
                byte[] lockids = byteCunchu.getbyte("lockid");
                byte[] blemanagers = byteCunchu.getbyte("blemanager");
                Log.d("TAG","存储数据 aesks"+mBleController.bytesToHexString(aesks) + "\r\n");
                Log.d("TAG","存储数据 lockids"+mBleController.bytesToHexString(lockids) + "\r\n");
                Log.d("TAG","存储数据 blemanagers"+mBleController.bytesToHexString(blemanagers) + "\r\n");
                Log.d("TAG","存储数据"+mBleController.bytesToHexString(aesk) + "\r\n");

             //   6D 21 41 14 7D 37 2B 4B 13 1F 0A 69 12 6B 5C 7C


                  byte[]data4={0x02,0x01,0x04,0x02,0x03,0x04,0x05,0x00,0xc,0xf,0xc,0xf,0xc,0xf,0xc,0xf};
                  byte[] encrypt = jiamiandjiemi.Encrypt(data4, aesks);
               Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt) + "\r\n");

              mBleController.writeBuffer(encrypt, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;
            //发送锁标识
            case R.id.fasongsuobiaoshi:
               byte[] aesks1 = byteCunchu.getbyte("aesk");
              byte[] lockids1 = byteCunchu.getbyte("lockid");
                //  01 02 07 D7 CA 7D D2 73 04 A8
            /*  byte[] lockids1=new byte[7];
                lockids1[0]=(byte)0xD7;
                lockids1[1]=(byte) 0xCA;
                lockids1[2]=0x7D;
                lockids1[3]=(byte)0xD2;
                lockids1[4]=0x73;
                lockids1[5]=(byte)0x04;
                lockids1[6]=(byte)0xA8;*/
              //  byte[] lockids1 = byteCunchu.getbyte("lockid");
                byteCunchu.put(lockids1,"lockid");
                for (int i = 0; i < token.length; i++) {
                    Log.d("TAG","Token+biaoshi"+token[i]);
                }
              //  存储数据 lockids33 8A 3F 5F 02 B1 E3
               byte[]data5=new byte[16];
                data5[0]=0x02;
                data5[1]=0x02;
                data5[2]=0x06;
                data5[3]=lockids1[0];
                data5[4]=lockids1[1];
                data5[5]=lockids1[2];
                data5[6]=lockids1[3];
                data5[7]=lockids1[4];
                data5[8]=lockids1[5];
                data5[9]=token[0];
                data5[10]=token[1];
                data5[11]=token[2];
                data5[12]=token[3];
//  04 E5 35 94 B0 CF 37
              /*  byte[]data5=new byte[16];
                data5[0]=0x02;
                data5[1]=0x02;
                data5[2]=0x07;
                data5[3]=(byte)0x04;
                data5[4]=(byte)0xE5;
                data5[5]=0x35 ;
                data5[6]=(byte)0x94;
                data5[7]=(byte)0xB0;
                data5[8]=(byte)0xCF;
                data5[9]=(byte)0x37;
                data5[10]=token[0];
                data5[11]=token[1];
                data5[12]=token[2];
                data5[13]=token[3];*/
//  5A 19 3F 12 0B 71 3C 30 62 05 5E 39 5A 7E 47 1A


                byte[] encrypt1 = jiamiandjiemi.Encrypt(data5, aesks1);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt1) + "\r\n");
                Log.d("TAG","aaaaaa"+mBleController.bytesToHexString(data5) + "\r\n");
                mBleController.writeBuffer(encrypt1, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });



                break;

            case R.id.xiugaimima:
                byte[] aesks2 = byteCunchu.getbyte("aesk");
                byte[] blemanagers1 = byteCunchu.getbyte("blemanager");
                for (int i = 0; i < token.length; i++) {
                    Log.d("TAG","data6"+token[i]);
                }
                byte[]data6=new byte[16];
                data6[0]=0x03;
                data6[1]=0x01;
                data6[2]=0x09;
                data6[3]=blemanagers1[0];
                data6[4]=blemanagers1[1];
                data6[5]=blemanagers1[2];
                data6[6]=blemanagers1[3];
                data6[7]=blemanagers1[4];
                data6[8]=blemanagers1[5];
                data6[9]=blemanagers1[6];
                data6[10]=blemanagers1[7];
                data6[11]=blemanagers1[8];
                data6[12]=token[0];
                data6[13]=token[1];
                data6[14]=token[2];
                data6[15]=token[3];
                for (int i = 0; i < data6.length; i++) {
                    Log.d("TAG","aa"+data6[i]);
                }
                byte[] encrypt2 = jiamiandjiemi.Encrypt(data6, aesks2);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt2) + "\r\n");

                mBleController.writeBuffer(encrypt2, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;

            case R.id.submit_mima:
                if (mimastate[0]==0){
                    byte[]data7=new byte[16];
                    data7[0]=0x03;
                    data7[1]=0x02;
                    data7[2]=0x09;
                    data7[3]=0x01;
                    data7[4]=0x02;
                    data7[5]=0x03;
                    data7[6]=0x02;
                    data7[7]=0x04;
                    data7[8]=0x05;
                    data7[9]=0x06;
                    data7[10]=0x08;
                    data7[11]=0x06;
                    data7[12]=token[0];
                    data7[13]=token[1];
                    data7[14]=token[2];
                    data7[15]=token[3];
                    byte[] aesks3 = byteCunchu.getbyte("aesk");
                    byte[] encrypt3 = jiamiandjiemi.Encrypt(data7, aesks3);
                    Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt3) + "\r\n");

                    mBleController.writeBuffer(encrypt3, new OnWriteCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d("TAG","发送成功");

                        }
                        @Override
                        public void onFailed(int state) {

                        }
                    });


                }
                break;
            case R.id.tongbushijian:
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
                data80[7]=token[0];
                data80[8]=token[1];
                data80[9]=token[2];
                data80[10]=token[3];
                byte[] aesks4 = byteCunchu.getbyte("aesk");
                byte[] encrypt40 = jiamiandjiemi.Encrypt(data80,aesks4);
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



                break;




            case R.id.dongjiekaisuo:
                byte[]data8=new byte[16];
                data8[0]=0x03;
                data8[1]=0x05;
                data8[2]=0x01;
                data8[3]=0x00;
                data8[4]=token[0];
                data8[5]=token[1];
                data8[6]=token[2];
                data8[7]=token[3];
                byte[] aesks5 = byteCunchu.getbyte("aesk");
                byte[] encrypt4 = jiamiandjiemi.Encrypt(data8, aesks5);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt4) + "\r\n");

                mBleController.writeBuffer(encrypt4, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;

            case R.id.jiedonglanya:
                byte[]byte0012=new byte[6];
                byte0012[0]=04;
                byte0012[1]=05;
                byte0012[2]=(byte)0x08;
                byte0012[3]=05;
                byte0012[4]=06;
                byte0012[5]=07;
                byte[]data91=new byte[16];
                data91[0]=0x03;
                data91[1]=0x06;
                data91[2]=0x07;
                data91[3]=0x01;
                data91[4]=byte0012[0];
                data91[5]=byte0012[1];
                data91[6]=byte0012[2];
                data91[7]=byte0012[3];
                data91[8]=byte0012[4];
                data91[9]=byte0012[5];
                data91[10]=token[0];
                data91[11]=token[1];
                data91[12]=token[2];
                data91[13]=token[3];
                byte[] aesks61 = byteCunchu.getbyte("aesk");
                byte[] encrypt51 = jiamiandjiemi.Encrypt(data91, aesks61);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt51) + "\r\n");

                mBleController.writeBuffer(encrypt51, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });
                break;
            case R.id.jiechulanyaguanliyuan:
                byte[]data9=new byte[16];
                data9[0]=0x03;
                data9[1]=0x06;
                data9[2]=0x01;
                data9[3]=0x00;
                data9[4]=token[0];
                data9[5]=token[1];
                data9[6]=token[2];
                data9[7]=token[3];
                byte[] aesks6 = byteCunchu.getbyte("aesk");
                byte[] encrypt5 = jiamiandjiemi.Encrypt(data9, aesks6);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt5) + "\r\n");

                mBleController.writeBuffer(encrypt5, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;
            case R.id.jiechu:
                byte[]data10=new byte[16];
                data10[0]=0x03;
                data10[1]=0x07;
                data10[2]=0x01;
                data10[3]=0x00;
                data10[4]=token[0];
                data10[5]=token[1];
                data10[6]=token[2];
                data10[7]=token[3];
                byte[] aesks7 = byteCunchu.getbyte("aesk");
                byte[] encrypt6 = jiamiandjiemi.Encrypt(data10, aesks7);
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
                break;
            case R.id.addyongMeaaage:
                byte[]byte01=new byte[6];
                byte01[0]=04;
                byte01[1]=05;
                byte01[2]=(byte)0x08;
                byte01[3]=05;
                byte01[4]=06;
                byte01[5]=07;
                byte[]data11=new byte[16];
                data11[0]=0x04;
                data11[1]=0x01;
                data11[2]=0x07;
                data11[3]=0x01;
                data11[4]=byte01[0];
                data11[5]=byte01[1];
                data11[6]=byte01[2];
                data11[7]=byte01[3];
                data11[8]=byte01[4];
                data11[9]=byte01[5];
                data11[10]=token[0];
                data11[11]=token[1];
                data11[12]=token[2];
                data11[13]=token[3];
                Log.d("TAG","token添加用户"+mBleController.bytesToHexString(token) + "\r\n");
                byte[] aesks8 = byteCunchu.getbyte("aesk");
                byte[] encrypt7 = jiamiandjiemi.Encrypt(data11, aesks8);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt7) + "\r\n");

                mBleController.writeBuffer(encrypt7, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });


                break;
            //设置租期范围
            case R.id.setzuqifanwei:
                //获取当前时间戳
                long timeStampSec1 = System.currentTimeMillis()/1000;
                String timestamp1 = String.format("%010d", timeStampSec1);
                Log.d("TAG",""+timestamp1);
                String string11 = Integer.toHexString((int) timeStampSec1);
                Log.d("TAG","..."+string11);
              //  1529635722  2018-06-22 10:48:42
            //    byte[] bytes2 = jiamiandjiemi.hexString2Bytes("1529635722");
                byte[] bytes1 = jiamiandjiemi.hexString2Bytes(string11);
                for (int i = 0; i < bytes1.length; i++) {
                    Log.d("TAG","."+bytes1[i]);
                }
              /*  for (int i = 0; i < bytes2.length; i++) {
                    Log.d("TAG","结束时间"+bytes2[i]);
                }*/
                byte[] bytes2=new byte[4];
                bytes2[0]=(byte)0x5b;
                bytes2[1]=(byte)0x53;
                bytes2[2]=(byte)0xf0;
                bytes2[3]=(byte)0x8a;
                byte[]data12=new byte[16];
                data12[0]=0x04;
                data12[1]=0x04;
                data12[2]=0x08;
                data12[3]=bytes1[0];
                data12[4]=bytes1[1];
                data12[5]=bytes1[2];
                data12[6]=bytes1[3];
                data12[7]=bytes2[0];
                data12[8]=bytes2[1];
                data12[9]=bytes2[2];
                data12[10]=bytes2[3];
                data12[11]=token[0];
                data12[12]=token[1];
                data12[13]=token[2];
                data12[14]=token[3];
                Log.d("TAG","token设置租期"+mBleController.bytesToHexString(token) + "\r\n");
                byte[] aesks9 = byteCunchu.getbyte("aesk");
                byte[] encrypt8 = jiamiandjiemi.Encrypt(data12, aesks9);
                Log.d("TAG","data12"+mBleController.bytesToHexString(data12) + "\r\n");

                mBleController.writeBuffer(encrypt8, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });
                break;

            case R.id.deletyonghuMessage:
                byte[]data19=new byte[16];
                data19[0]=0x04;
                data19[1]=0x03;
                data19[2]=0x01;
                data19[3]=0x00;
                data19[4]=token[0];
                data19[5]=token[1];
                data19[6]=token[2];
                data19[7]=token[3];
                byte[] aesks19 = byteCunchu.getbyte("aesk");
                byte[] encrypt18 = jiamiandjiemi.Encrypt(data19, aesks19);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt18) + "\r\n");

                mBleController.writeBuffer(encrypt18, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });
                break;
            case R.id.obtain_user_meaaage:
                byte[]data13=new byte[16];
                data13[0]=0x04;
                data13[1]=0x06;
                data13[2]=0x01;
                data13[3]=0x00;
                data13[4]=token[0];
                data13[5]=token[1];
                data13[6]=token[2];
                data13[7]=token[3];
                byte[] aesks10 = byteCunchu.getbyte("aesk");
                byte[] encrypt9 = jiamiandjiemi.Encrypt(data13, aesks10);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt9) + "\r\n");

                mBleController.writeBuffer(encrypt9, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;
            //获取租期范围
            case R.id.obtain_zuqifanwei:
                byte[]data141=new byte[16];
                data141[0]=0x04;
                data141[1]=0x07;
                data141[2]=0x01;
                data141[3]=0x00;
                data141[4]=token[0];
                data141[5]=token[1];
                data141[6]=token[2];
                data141[7]=token[3];
                byte[] aesks101 = byteCunchu.getbyte("aesk");
                byte[] encrypt101 = jiamiandjiemi.Encrypt(data141, aesks101);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt101) + "\r\n");

                mBleController.writeBuffer(encrypt101, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;
            case R.id.obtain_open_lock_recode:
                byte[] blemanagers3 = byteCunchu.getbyte("blemanager");
                byte[]data14=new byte[16];
                data14[0]=0x05;
                data14[1]=0x01;
                data14[2]=0x0a;
                data14[3]=0x00;
                data14[4]=blemanagers3[0];
                data14[5]=blemanagers3[1];
                data14[6]=blemanagers3[2];
                data14[7]=blemanagers3[3];
                data14[8]=blemanagers3[4];
                data14[9]=blemanagers3[5];
                data14[10]=blemanagers3[6];
                data14[11]=blemanagers3[7];
                data14[12]=token[0];
                data14[13]=token[1];
                data14[14]=token[2];
                data14[15]=token[3];
                byte[] aesks11 = byteCunchu.getbyte("aesk");
                byte[] encrypt10 = jiamiandjiemi.Encrypt(data14, aesks11);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10) + "\r\n");

                mBleController.writeBuffer(encrypt10, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });
                break;

            case R.id.obtain_open_lock_recode_userID:
                byte[]byte0122=new byte[6];
                byte0122[0]=04;
                byte0122[1]=05;
                byte0122[2]=(byte)0x08;
                byte0122[3]=05;
                byte0122[4]=06;
                byte0122[5]=07;

                byte[]data14a=new byte[16];
                data14a[0]=0x05;
                data14a[1]=0x01;
                data14a[2]=0x01;
                data14a[3]=0x00;
                data14a[4]=token[0];
                data14a[5]=token[1];
                data14a[6]=token[2];
                data14a[7]=token[3];
                byte[] aesks11a = byteCunchu.getbyte("aesk");
                byte[] encrypt10a = jiamiandjiemi.Encrypt(data14a, aesks11a);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10a) + "\r\n");

                mBleController.writeBuffer(encrypt10a, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });


                break;

            case R.id.obtain_open_lock_recode_time:
                byte[]data14a1=new byte[16];
                data14a1[0]=0x05;
                data14a1[1]=0x02;
                data14a1[2]=0x01;
                data14a1[3]=0x00;
                data14a1[4]=token[0];
                data14a1[5]=token[1];
                data14a1[6]=token[2];
                data14a1[7]=token[3];

                byte[] aesks11a1 = byteCunchu.getbyte("aesk");
                byte[] encrypt10a1 = jiamiandjiemi.Encrypt(data14a1, aesks11a1);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt10a1) + "\r\n");

                mBleController.writeBuffer(encrypt10a1, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;


            case R.id.open_lock:
                byte[] blemanagers2 = byteCunchu.getbyte("blemanager");
                byte[]data15=new byte[16];
                data15[0]=0x06;
                data15[1]=0x01;
                data15[2]=0x08;
                data15[3]=blemanagers2[0];
                data15[4]=blemanagers2[1];
                data15[5]=blemanagers2[2];
                data15[6]=blemanagers2[3];
                data15[7]=blemanagers2[4];
                data15[8]=blemanagers2[5];
                data15[9]=blemanagers2[6];
                data15[10]=blemanagers2[7];
                data15[11]=token[0];
                data15[12]=token[1];
                data15[13]=token[2];
                data15[14]=token[3];
                byte[] aesks12 = byteCunchu.getbyte("aesk");
                byte[] encrypt11 = jiamiandjiemi.Encrypt(data15, aesks12);
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
                break;
            //添加自定义密码
            case R.id.add_customPasswords:
                byte []byte16=new byte[16];;
                byte16[0]=04;
                byte16[1]=02;
                byte16[2]=01;
                byte16[3]=05;
                byte16[4]=token[0];
                byte16[5]=token[1];
                byte16[6]=token[2];
                byte16[7]=token[3];
                byte[] aesks13 = byteCunchu.getbyte("aesk");
                byte[] encrypt12 = jiamiandjiemi.Encrypt(byte16, aesks13);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt12) + "\r\n");

                mBleController.writeBuffer(encrypt12, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });

                break;

            case R.id.open_lock_user:
                byte[]byte012=new byte[6];
                byte012[0]=04;
                byte012[1]=05;
                byte012[2]=(byte)0x08;
                byte012[3]=05;
                byte012[4]=06;
                byte012[5]=07;
                byte[]data151=new byte[16];
                data151[0]=0x06;
                data151[1]=0x01;
                data151[2]=0x06;
                data151[3]=byte012[0];
                data151[4]=byte012[1];
                data151[5]=byte012[2];
                data151[6]=byte012[3];
                data151[7]=byte012[4];
                data151[8]=byte012[5];
                data151[9]=token[0];
                data151[10]=token[1];
                data151[11]=token[2];
                data151[12]=token[3];
                byte[] aesks012 = byteCunchu.getbyte("aesk");
                byte[] encrypt011 = jiamiandjiemi.Encrypt(data151, aesks012);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt011) + "\r\n");

                mBleController.writeBuffer(encrypt011, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });
                break;
            //删除开锁记录
            case R.id.delet_open_recode:
                byte[]byte01221=new byte[6];
                byte01221[0]=04;
                byte01221[1]=05;
                byte01221[2]=(byte)0x08;
                byte01221[3]=05;
                byte01221[4]=06;
                byte01221[5]=07;
                byte[]data1511=new byte[16];
                data1511[0]=0x05;
                data1511[1]=0x04;
                data1511[2]=0x01;
                data1511[3]=0x00;
              /*  data1511[3]=byte01221[0];
                data1511[4]=byte01221[1];
                data1511[5]=byte01221[2];
                data1511[6]=byte01221[3];
                data1511[7]=byte01221[4];
                data1511[8]=byte01221[5];*/
                data1511[4]=token[0];
                data1511[5]=token[1];
                data1511[6]=token[2];
                data1511[7]=token[3];
                byte[] aesks0121 = byteCunchu.getbyte("aesk");
                byte[] encrypt0111 = jiamiandjiemi.Encrypt(data1511, aesks0121);
                Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt0111) + "\r\n");

                mBleController.writeBuffer(encrypt0111, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","发送成功");

                    }
                    @Override
                    public void onFailed(int state) {

                    }
                });
                break;

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



}
