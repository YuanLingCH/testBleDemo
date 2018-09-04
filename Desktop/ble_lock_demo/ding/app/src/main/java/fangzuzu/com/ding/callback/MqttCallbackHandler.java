package fangzuzu.com.ding.callback;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.bean.mqttBean;
import fangzuzu.com.ding.event.MessageEvent;


/**
 * Description :接收服务器推送过来的消息
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/10/25 0025
 */

public class MqttCallbackHandler implements MqttCallback {

    private Context context;
    private String clientId;

    public MqttCallbackHandler(Context context,String clientId) {
        this.context=context;
        this.clientId=clientId;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.d("MqttCallbackHandler","MqttCallbackHandler/connectionLost");
    }

    /**
     *
     * @param s  主题
     * @param mqttMessage  内容信息
     * @throws Exception
     */
    @Override
    public void messageArrived(final String s, final MqttMessage mqttMessage) throws Exception {
        Log.d("MqttCallbackHandler","MqttCallbackHandler/messageArrived="+s);
        Log.d("MqttCallbackHandler","message打印="+new String(mqttMessage.getPayload()));
        String s1 = new String(mqttMessage.getPayload());
        Gson gson=new Gson();
        mqttBean bean = gson.fromJson(s1, new TypeToken<mqttBean>() {}.getType());
        final String code = bean.getCode();
        mqttBean.DataBean data = bean.getData();
        final String from = data.getFrom();

        Log.d("TAG","消息"+code+"from"+from);
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new MessageEvent(s,mqttMessage,code,from ));
            }
        },1000);




    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d("MqttCallbackHandler","MqttCallbackHandler/deliveryComplete");
    }

}
