package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import fangzuzu.com.ding.R;

/**
 * Created by lingyuan on 2018/6/4.
 */

public class addfingerTwoStepActivity extends BaseActivity {
    Toolbar toolbar;
    ImageView iv_0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_finger_two_step_aactivity);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initlize();
    }

    private void initlize() {
        iv_0= (ImageView) findViewById(R.id.iv_0);
        Animation translateAnimation = new TranslateAnimation(0,-210,0,0);//平移动画  从0,0,平移到100,100
        translateAnimation.setDuration(1500);//动画持续的时间为1.5s
        translateAnimation.setRepeatCount(Animation.INFINITE);
        translateAnimation.setRepeatMode(Animation.REVERSE);
                iv_0.setAnimation(translateAnimation);//给imageView添加的动画效果
       // translateAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
       // translateAnimation.setFillAfter(true);//不回到起始位置
        //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
        translateAnimation.startNow();//动画开始执行 放在最后即可
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

    /**
     * 点击添加指纹  连接蓝牙
     * @param view
     */
    public void butClick(View view) {
        Intent intent=new Intent(this,addfingerThreeStepActivity.class);
        startActivity(intent);

    }
}
