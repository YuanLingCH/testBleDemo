package fangzuzu.com.ding.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.impl.OnMqttListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/8/17.
 */

public class MqttPresenter {

    public MqttPresenter() {
    }
    public void sendMqtt(String to, final OnMqttListener listener){
        String uid = SharedUtils.getString("uid");
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager api = retrofit.create(apiManager.class);
        Map<String ,String> data=new HashMap<>();
        data.put("from",uid);
        data.put("to",to);
        data.put("text","发内容");
        data.put("time","2018_06");
        final Map msg=new HashMap<>();
        msg.put("code","10086");
        msg.put("data",data);

        Map map=new HashMap();
        map.put("topic","fzzchat.PTP");
        map.put("topicid",to);
        map.put("msg",msg);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG","MMQTT"+s);
        Call<String> call = api.sendMqtt(s);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG","MMQTT"+body);

                Log.d("TAG", "授权" + body);
                msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = s.getCode();
                if (code==1001){
                    listener.mqttSuccess();

                }else if (code==1002){
              listener.mqttFaild();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
