package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.utils.StringUtils;


/**
 * Created by lingyuan on 2018/7/12.
 */

public class SplashActivity extends BaseActivity {



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
            String name = user.name;
            if (!StringUtils.isEmpty(name)){
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
        }else {
            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent=new Intent(MainApplication.getInstence(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);

        }
    }
}
