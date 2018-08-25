package fangzuzu.com.ding.event;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Description : 存放消息的类
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/10/26 0026
 */

public class MessageEvent {
    private String topic;
    private MqttMessage mqttMessage;
    private String string="";
    private  String code;
    public MessageEvent() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MessageEvent(String topic, MqttMessage mqttMessage, String code) {
        this.topic = topic;
        this.mqttMessage = mqttMessage;
        this.code=code;
    }

    public MessageEvent(String str) {
        this.string=str;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
