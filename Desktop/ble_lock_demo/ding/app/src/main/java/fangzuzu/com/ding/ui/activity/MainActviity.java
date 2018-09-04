package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.callback.SubcribeCallBackHandler;
import fangzuzu.com.ding.event.MessageEvent;
import fangzuzu.com.ding.ui.fragment.CenterFragment;
import fangzuzu.com.ding.ui.fragment.HomeFragment;
import fangzuzu.com.ding.ui.fragment.SmartDevice;
import fangzuzu.com.ding.utils.NetWorkTesting;
import fangzuzu.com.ding.utils.StringUtils;

/**
 * Created by Administrator on 2016/12/1.
 */
public class MainActviity extends BaseActivity {
    public  String clientID;
    private static MqttAndroidClient client;
    public String serverIP="www.fzhuzhu.cn";
    public String port="1883";
    private FragmentTabHost tabHost;
    private FrameLayout frameLayout;
    private LayoutInflater inflater;
    String uid;


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

        initalize();
      EventBus.getDefault().register(this);
        uid= SharedUtils.getString("uid");
        clientID="az"+uid;

    }


    public String topic="fzzchat/PTP";
    private void subscibe() {
        NetWorkTesting netWorkTesting=new NetWorkTesting(this);
        if (netWorkTesting.isNetWorkAvailable()){
            if(client!=null){
                /**订阅一个主题，服务的质量默认为0*/
                try {
                    client.subscribe(topic,0,null,new SubcribeCallBackHandler(MainActviity.this));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActviity.this,"当前网络不可用，请检查您的网络！",Toast.LENGTH_LONG).show();
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
       String frome = event.getFrome();
       Log.d("TAG","收到消息main函数走了。。。。。。。。。。。。。。。。frome。。。。。。。。。。"+frome);
       if(!StringUtils.isEmpty(code)){
           if (code.equals("10086")){
               if (!uid.equals(frome)){
                   Intent intent=new Intent(MainApplication.getInstence(),lockListActivity.class);
                   startActivity(intent);
                   finish();
               }

           }
       }






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
        if(client!=null)
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }


    }


}
