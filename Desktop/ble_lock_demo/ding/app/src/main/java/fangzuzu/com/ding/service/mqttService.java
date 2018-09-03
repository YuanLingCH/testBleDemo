package fangzuzu.com.ding.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.callback.ConnectCallBackHandler;
import fangzuzu.com.ding.callback.MqttCallbackHandler;
import fangzuzu.com.ding.callback.SubcribeCallBackHandler;
import fangzuzu.com.ding.event.MessageEvent;
import fangzuzu.com.ding.utils.NetWorkTesting;

/**
 * Created by lingyuan on 2018/8/23.
 */

public class mqttService extends Service {
    public  String clientID;
    private static MqttAndroidClient client;
  //  public String serverIP="www.fzhuzhu.cn";
    // http://192.168.0.121:8799/news/PointToPoint
  public String serverIP="www.fzhuzhu.cn";
    public String port="1883";
    String uid;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        uid= SharedUtils.getString("uid");
        clientID="az"+uid;
        startConnect(clientID,serverIP,port);
 /*       Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               subscibe();
            }
        },2000);*/
        Log.d("TAG","服务走了。onCreate()。。。。。。。。。。。。。。。。。。。。。。。。。"+uid);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG","服务走了。onStartCommand。。。。。。。。。。。。。。。。。。。。。。。。"+uid);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public String topic="fzzchat/PTP";
    private void subscibe() {
        NetWorkTesting netWorkTesting=new NetWorkTesting(this);
        if (netWorkTesting.isNetWorkAvailable()){
            if(client!=null){
                /**订阅一个主题，服务的质量默认为0*/
                try {
                    client.subscribe(topic,0,null,new SubcribeCallBackHandler(MainApplication.getInstence()));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainApplication.getInstence(),"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    /**
     * 运行在主线程
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String code = event.getCode();

        Log.d("TAG","收到消息服务函数走了。。。。。。。。。。。。。。。。。。。。。。。。。。"+ code);
    }

    private void startConnect(String clientID, String serverIP, String port) {
        //服务器地址   192.168.1.3
        NetWorkTesting net=new NetWorkTesting(this);
        if (net.isNetWorkAvailable()){

            String  uri ="tcp://";
            uri=uri+serverIP+":"+port;
            Log.d("MainActivity",uri+"  "+clientID+serverIP+port);
            /**
             * 连接的选项
             */
            MqttConnectOptions conOpt = new MqttConnectOptions();
            /**设计连接超时时间*/
            conOpt.setConnectionTimeout(3000);
            /**设计心跳间隔时间300秒*/
            conOpt.setKeepAliveInterval(300);
            conOpt.setCleanSession(true);

            /**
             * 创建连接对象
             */
            client = new MqttAndroidClient(this,uri, clientID);

            /**
             * 连接后设计一个回调
             */
            client.setCallback(new MqttCallbackHandler(this, clientID));
            /**
             * 开始连接服务器，参数：ConnectionOptions,  IMqttActionListener
             */
            try {
                client.connect(conOpt, null, new ConnectCallBackHandler(this));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取MqttAndroidClient实例
     * @return
     */
    public static MqttAndroidClient getMqttAndroidClientInstace(){
        if(client!=null)
            return  client;
        return null;
    }
}
