package fangzuzu.com.ding.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.BaseFragmentPagerAdapter;
import fangzuzu.com.ding.event.passwordMessage;
import fangzuzu.com.ding.ui.fragment.circulationFragment;
import fangzuzu.com.ding.ui.fragment.customFragment;
import fangzuzu.com.ding.ui.fragment.foreverFragment;
import fangzuzu.com.ding.ui.fragment.timeLimitFragment;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;

/**
 * Created by yuanling on 2018/5/15.
 */

public class sendPassWordActivity extends BaseActivity {
    private TabLayout tabLayout;

    private ViewPager viewPager;
    Toolbar toolbar;
    TextView send_password;
    boolean isKitKat = false;
    String uid;
    String adminUserId;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.send_passsword_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        uid= SharedUtils.getString("uid");
        adminUserId = SharedUtils.getString("adminUserId");
        initview();
        initEvents();
        initlize();
        EventBus.getDefault().register(this);

        Log.d("TAG","点击我了"+uid);
        Log.d("TAG","点击我了"+adminUserId);

    }

    protected void setStatusBar() {
        if (isKitKat){

            int statusH = screenAdapterUtils.getStatusHeight(this);
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }






    String password;
    String paswType;
    String timeStart;
    String timeEnd;
    String type;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(passwordMessage event){
        password = event.getPassWord();
       paswType = event.getPaswType();
         timeStart = event.getTimeStart();
        timeEnd = event.getTimeEnd();
         type = event.getType();
        Log.d("TAG"," password"+password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initlize() {
        //分享密码
        send_password= (TextView) findViewById(R.id.send_password);
        send_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","点击我了");
                    if (!StringUtils.isEmpty(password)){
                        showShare();
                    }else {
                        Toast.makeText(sendPassWordActivity.this,"请生成密码",Toast.LENGTH_LONG).show();
                    }

            }
        });

    }

    private void showShare() {
        String first=null;
        String lockName = MainApplication.getInstence().getLockName();
        if (type.equals("0")){
            paswType="限时";
            first="生效时间:"+timeStart+"\n"+"失效时间:"+timeEnd+"\n"+"类型:"+paswType+"\n"+"房间:"+lockName+"\n"+"密码须要在24小时内使用一次，否则将失效";
        }else if (type.equals("1")){
            paswType="永久";
            first="生效时间:"+timeStart+"\n"+"类型:"+paswType+"\n"+"房间:"+lockName+"\n"+"密码须要在24小时内使用一次，否则将失效";
        }else if (type.equals(3)){
            //   循环
            first="生效时间:"+timeStart+"\n"+"失效时间:"+timeEnd+"\n"+"类型:"+paswType+"\n"+"房间:"+lockName;
        }if (type.equals("2")){
            paswType="限时";
            first="生效时间:"+timeStart+"\n"+"失效时间:"+timeEnd+"\n"+"类型:"+paswType+"\n"+"房间:"+lockName+"\n"+"密码须要在24小时内使用一次，否则将失效";
        }
        OnekeyShare oks = new OnekeyShare();

       // oks.setCallback(new PlatformActionListenerImpl());
        //关闭sso授权
      oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
    // oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
    //   oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("你好,你的密码是:"+password+"\n"+first+"\n"+"输入密码后，按锁键盘右下角开锁键或#键开锁");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
      //  oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
     //  oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
     //  oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
   // oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
   //   oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
    // oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    private void initview() {
        send_password= (TextView) findViewById(R.id.send_password);
        tabLayout = (TabLayout)findViewById(R.id.tab);
        // 添加标签
        if (uid.equals(adminUserId)){
            tabLayout.addTab(tabLayout.newTab().setText("永久"));
            tabLayout.addTab(tabLayout.newTab().setText("限时"));
            tabLayout.addTab(tabLayout.newTab().setText("自定义"));
            tabLayout.addTab(tabLayout.newTab().setText("循环"));
        }else {
            tabLayout.addTab(tabLayout.newTab().setText("限时"));
            tabLayout.addTab(tabLayout.newTab().setText("自定义"));
        }

      //  tabLayout.addTab(tabLayout.newTab().setText("清空"));


        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        viewPager = (ViewPager)findViewById(R.id.vp);

        List<Fragment> list = new ArrayList<>();
        if (uid.equals(adminUserId)){
            list.add(new foreverFragment());
            list.add(new timeLimitFragment());
            list.add(new customFragment());
            list.add(new circulationFragment());
        }else {
            list.add(new timeLimitFragment());
            list.add(new customFragment());
        }


      //  list.add(new clearFragment());


        // Fragment嵌套Fragment传入FragmentManager用getChildFragmentManager()
        BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(
                getSupportFragmentManager(), list);

        viewPager.setAdapter(pagerAdapter);

    }


        protected void initEvents() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //让TabLayout和ViewPager联动
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //让viewPager和TabLayout联动
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




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
