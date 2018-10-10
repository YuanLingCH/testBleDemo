package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.bean.userLockBean;
import fangzuzu.com.ding.service.mqttService;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by lingyuan on 2018/7/12.
 */

public class SplashActivity extends BaseActivity {

    List data3;
    String uid;
    List dataPart=new ArrayList();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.splash_activity_layout);

   initlize();

    }

    private void initlize() {
        UserBean user = SharedUtils.getUser();


        if (user!=null){
            getUserLockList();
            String name = user.name;
            if (!StringUtils.isEmpty(name)){
                Log.d("TAG","直接走了啊");
                Intent intent=new Intent(SplashActivity.this, mqttService.class);
                startService(intent);



            }
        }else {
       /*     Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent=new Intent(MainApplication.getInstence(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);*/

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /**
                     *要执行的操作
                     */
                    Intent intent=new Intent(MainApplication.getInstence(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);//3秒后执行Runnable中的run方法




        }
    }


    /**
     * 请求网络数据
     */
    String lockName;
    String secretKey;
    String adminPsw;
    String adminUserId;
    String electricity;
    String allow;
    String id1;
    String lockNumber;
    String endTime;
    String startTime;
    String updataFlag;
    public void getUserLockList() {
        data3=new ArrayList();
        String partid = SharedUtils.getString("partid");
        uid= SharedUtils.getString("uid");
        Log.d("TAG","partid"+partid);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        final NetWorkTesting net=new NetWorkTesting(this);
        if (net.isNetWorkAvailable()){
            Call<String> call = manager.getLockUserList("aa123456", uid);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String body = response.body();
                    if (StringUtils.isEmpty(body)){
                        Log.d("TAG","网络错误11"+body);
                      finish();

                    }else {
                        Log.d("TAG","测试一test把手锁"+body );
                        Gson gson=new Gson();
                        userLockBean bean = gson.fromJson(body, new TypeToken<userLockBean>() {}.getType());
                        int code = bean.getCode();
                        data3.clear();// 防止加载重复数据
                        if (code==1001){
                            userLockBean.DataBean data = bean.getData();
                            List<?> parentLock = data.getParentLock();
                            Iterator<?> iterator = parentLock.iterator();
                            while (iterator.hasNext()){
                                Object next = iterator.next();
                                dataPart.add(next);
                            }
                            List<userLockBean.DataBean.UserLockBean> userLock = data.getUserLock();
                            Iterator<userLockBean.DataBean.UserLockBean> iterator1 = userLock.iterator();
                            while (iterator1.hasNext()){
                                userLockBean.DataBean.UserLockBean next = iterator1.next();
                               lockName = next.getLockName();
                              secretKey = next.getSecretKey();
                                adminPsw = next.getAdminPsw();
                                 adminUserId = next.getAdminUserId();
                                 electricity = next.getElectricity();
                                allow = next.getAllow();
                                String keyId = next.getKeyId();
                                SharedUtils.putString("keyId",keyId);
                                id1 = next.getId();
                             updataFlag = next.getUpdataFlag()+"";
                                endTime = next.getEndTime();
                           startTime = next.getStartTime();
                                lockNumber = next.getLockNumber();
                                Log.d("TAG","锁命"+lockName);
                                data3.add(next);
                            }
                            data3.addAll(dataPart);
                            Log.d("TAG","集合大小"+data3.size());
                            Log.d("TAG",body);

                            if (data3.size()==1){
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),MainActviity.class);
                                        intent.putExtra("id",id1);
                                        intent.putExtra("secretKey",secretKey);
                                        intent.putExtra("allow",allow);
                                        intent.putExtra("electricity",electricity);
                                        intent.putExtra("lockNumber",lockNumber);
                                        intent.putExtra("adminPsw",adminPsw);
                                        intent.putExtra("lockName",lockName);
                                        intent.putExtra("jihe","1");
                                        intent.putExtra("adminUserId",adminUserId);
                                        intent.putExtra("startTime",startTime);
                                        intent.putExtra("endTime",endTime); //updataFlag
                                        intent.putExtra("updataFlag",updataFlag);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);


                            }else {
                                Timer timer=new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                },1000);
                            }


                        }else if(code==1002){
                            Log.d("TAG","网络错误1002");

                        }

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }else {

            Toast.makeText(SplashActivity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();



        }
    }



}
