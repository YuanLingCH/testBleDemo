package fangzuzu.com.ding.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.widget.DatePicier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/9/19.
 */

public class modificationKeyLockMessageActivity extends BaseActivity{
    TextView tv_start_time,tv_end_time;
    LinearLayout ll_start_time,ll_end_time;
    Button but_time;
    Toolbar toolbar;
    boolean isKitKat = false;
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
        setContentView(R.layout.modification_key_activity_layout);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EventBus.getDefault().register(this);

        getIntentData();
        initViews();
        DatePicier.initDatePicker(tv_start_time, tv_end_time, modificationKeyLockMessageActivity.this);
        setStatusBar();
        initEvent();

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
    /**
     * 点击事件
     */
    private void initEvent() {
        tv_start_time.setText(getstartTime);
        tv_end_time.setText(getendTime);

        // 点击开始时间
        ll_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePicier.getCustomDatePicker1().show(tv_start_time.getText().toString());
            }
        });
        // 点击结束时间
        ll_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(tv_end_time.getText().toString());
            }
        });
        /**
         * 点击改时间
         */
        but_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  kaishi=null;
                if (!StringUtils.isEmpty(createtime)){  //  endtime  kaishi 时间

                if (StringUtils.isEmpty(endtime)){
                    kaishi=getstartTime;
                }else {
                    kaishi=endtime;
                }
                    String s = unixTime.dateToStampone( kaishi);
                    Log.d("TAG","开始时间戳"+s);
                    String substring1 = s.substring(0, s.length() - 3);
                    int startTime = Integer.parseInt(substring1);
                    Log.d("TAG","开始时间"+ startTime);

//  currentDate
                    String send = unixTime.dateToStampone(createtime);
                    Log.d("TAG","结束时间戳"+send);
                    String substring1end = send.substring(0, send.length() - 3);
                    int endTime = Integer.parseInt(substring1end);
                    Log.d("TAG","结束时间"+ endTime);
                    if (startTime<endTime){


                Map<String,String> map=new HashMap<>();
                map.put("id",id);
                map.put("keyName",keyName);
                map.put("startTime",kaishi);//开始时间
                map.put("endTime", createtime);   //结束时间
                final Gson gson=new Gson();
                final String json = gson.toJson(map);
                Log.d("TAG","上传数据"+json);
                Retrofit re=new Retrofit.Builder()
                        .baseUrl(apiManager.baseUrl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(MainApplication.getInstence().getClient())
                        .build();

                apiManager manager = re.create(apiManager.class);
                Call<String> call = manager.modificationKeyLcokMeaasge(json);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String body = response.body();
                        if (!StringUtils.isEmpty(body)){


                            Log.d("TAG","修改数据"+body);
                            msg o = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                            int code = o.getCode();
                            if (code==1001){

                                Toast.makeText(MainApplication.getInstence(),"修改有效期成功",Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(MainApplication.getInstence(),"修改有效期失败",Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
                    }else {
                        Toast.makeText(MainApplication.getInstence(),"失效时间必须大于生效时间",Toast.LENGTH_LONG).show();
                    }
            }else {
                    Toast.makeText(MainApplication.getInstence(),"请选择时间",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void initViews() {
        tv_start_time=(TextView) findViewById(R.id.tv_start_time);
        tv_end_time=(TextView)findViewById(R.id.tv_end_time);
        ll_start_time=(LinearLayout) findViewById(R.id.ll_start_time);
        ll_end_time=(LinearLayout) findViewById(R.id.ll_end_time);

        but_time=(Button) findViewById(R.id.but_time);

    }
    String getstartTime;
    String getendTime;
    String id;
    String keyName;
    public void getIntentData() {
        getstartTime = getIntent().getStringExtra("startTime");
        getendTime = getIntent().getStringExtra("endTime");
        keyName = getIntent().getStringExtra("keyName");
        id = getIntent().getStringExtra("id");
        Log.d("TAG"," getstartTime"+ getstartTime);
    }
    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime();
      //  tv_end_time.setText(endtime);

        Log.d("TAG","event1"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime().toString().trim();
      //  tv_start_time.setText( createtime);
        Log.d("TAG","event2"+createtime);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
