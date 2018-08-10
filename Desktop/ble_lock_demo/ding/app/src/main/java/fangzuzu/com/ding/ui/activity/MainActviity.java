package fangzuzu.com.ding.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.impl.MainService;
import fangzuzu.com.ding.ui.fragment.CenterFragment;
import fangzuzu.com.ding.ui.fragment.HomeFragment;
import fangzuzu.com.ding.ui.fragment.SmartDevice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2016/12/1.
 */
public class MainActviity extends BaseActivity {

    private FragmentTabHost tabHost;
    private FrameLayout frameLayout;
    private LayoutInflater inflater;

    private StringBuffer mReciveString = new StringBuffer();
    private byte[] token3;
    byte[]jiesouTock=new byte[16];
    byte[]token2=new byte[4];
    MediaPlayer mediaPlayer01;

    //底部导航栏数组
    private String [] tabItems={"首页","智能设备","个人中心"};
    //Tabhost 使用的数组类
    Class [] fragments={
            HomeFragment.class,
            SmartDevice.class,
            CenterFragment.class

    };



    //图片的id
    private  int[] imgIds={
            R.drawable.tab_home_sel,
            R.drawable.tab_message_sel,
            R.drawable.tab_center_sel

    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);


        getData();
        initalize();




    }










    private void initalize() {
        inflater=LayoutInflater.from(MainApplication.getInstence());
        tabHost= (FragmentTabHost) findViewById(android.R.id.tabhost);
        frameLayout= (FrameLayout) findViewById(R.id.framelayout);
        tabHost.setup(this,getSupportFragmentManager(),R.id.framelayout);
        for(int i=0;i<fragments.length;i++){
            TabHost.TabSpec tabItem=tabHost.newTabSpec(i+"");
            tabItem.setIndicator(getItemView(i));
            tabHost.addTab(tabItem,fragments[i],null);


        }


    }

    private void getData() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://stu.1000phone.net/")
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        MainService ms = retrofit.create(MainService.class);
        Call<String> call = ms.getUserInfo();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String value = response.body();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }




    private View getItemView(int index) {
        View view=inflater.inflate(R.layout.tabhost_item_layout,null);
        ImageView iv = (ImageView) view.findViewById(R.id.tab_img);
        iv.setImageResource(imgIds[index]);
        TextView tv= (TextView) view.findViewById(R.id.tab_tv);
        tv.setText(tabItems[index]);
        return view;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }


}
