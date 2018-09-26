package fangzuzu.com.ding.widget;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnWriteCallback;
import com.hansion.h_ble.callback.ScanCallback;
import com.hansion.h_ble.event.bleStateMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.ble.jiamiandjiemi;

/**
 * 蓝牙连接公共类
 * Created by lingyuan on 2018/8/6.
 * 密钥
 * 锁标识
 * 时间校验
 */

public class bleConnectUtils {
    private BleController myBleController;
    String lockmac;
    byte[] secret;
    byte[]allowbyt;
    Set bledata=new HashSet();
    // 初始化定时器
    Timer timer = new Timer();

    public  void  bleConnect(BleController mBleController,  byte[] secretKeyBytes,byte[]allowbyt1, String lockNumber){
        myBleController=mBleController;
        lockmac=lockNumber;
        secret=secretKeyBytes;
        Log.d("TAG","密钥类"+myBleController.bytesToHexString(secret) + "\r\n");
        Log.d("TAG","锁标识类"+myBleController.bytesToHexString(allowbyt) + "\r\n");
        if (!mBleController.isEnable()){
            mBleController.openBle();
        }else {
            if (!bledata.contains(lockNumber)){
                mBleController.scanBleone(0, new ScanCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","蓝牙扫描结束");

                                if (bledata.size()==0){
                                    bleStateMessage message=new bleStateMessage();
                                    EventBus.getDefault().post(message);
                               //   Toast.makeText(MainApplication.getInstence(), "没有扫描到锁，请重新扫描", Toast.LENGTH_SHORT).show();
                                }

                            connectble();


                    }

                    @Override
                    public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        String address = device.getAddress();
                        if (address.equals(lockmac)){
                            if (!bledata.contains(lockmac)){
                                bledata.add(address);
                                myBleController.stopScan();
                                Log.d("TAG","蓝牙扫描"+address);
                            }

                        }

                    }
                });
            }else {
                Log.d("TAG","不蓝牙扫描");
                connectble();
            }


        }


    }

    private void connectble() {
        myBleController.connect(0, lockmac, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                Log.d("TAG","连接成功");
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
                byte[] encrypt = jiamiandjiemi.Encrypt(data4, secret);
                Log.d("TAG","加密"+myBleController.bytesToHexString(encrypt) + "\r\n");

                myBleController.writeBuffer(encrypt, new OnWriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("TAG","身份校验成功");
                        // sendFirstCode();  //发送锁标识符
                  //   sendLockAllow();
                    }
                    @Override
                    public void onFailed(int state) {
                        Log.d("TAG","身份校验失败"+state);
                    }
                });
            }
        },500);

    }

    // 停止定时器
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            // 一定设置为null，否则定时器不会被回收
            timer = null;
        }
    }
}
