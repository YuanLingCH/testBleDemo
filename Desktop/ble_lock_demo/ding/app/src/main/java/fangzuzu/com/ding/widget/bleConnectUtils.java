package fangzuzu.com.ding.widget;

import android.util.Log;

import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.ConnectCallback;
import com.hansion.h_ble.callback.OnWriteCallback;

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






        connectble();


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
                Log.d("TAG","连接失败");
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
