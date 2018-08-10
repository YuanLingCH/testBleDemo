package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.IcListAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.Icbean;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/6/5.
 */

public class addICCardActivity extends BaseActivity{
    Toolbar toolbar;

    RecyclerView rc;
    List data3;
    IcListAdapter adapter;
    boolean isKitKat = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        setContentView(R.layout.add_ic_card_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
setStatusBar();
        getData();
        initViews();
        initlize();


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






    private void initlize() {

        rc= (RecyclerView) findViewById(R.id.lv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rc.setLayoutManager(layoutManager);

    }

    private void initViews() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
      //  return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=new Intent(addICCardActivity.this,addICCardOneStepActivity.class);

        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.seting_ic:
           //添加ic卡

                intent.putExtra("addType","0");
                intent.putExtra("toolbar","添加IC卡");
                intent.putExtra("byte","3");
                startActivity(intent);
                break;
            case R.id.seting_identity_card:
                //添加身份证

                intent.putExtra("addType","1");
                intent.putExtra("toolbar","添加身份证");
                intent.putExtra("byte","4");
                startActivity(intent);
                break;
        }
        return true;
      //  return super.onOptionsItemSelected(item);
    }

    public void getData() {
        data3=new ArrayList();
        String uid = SharedUtils.getString("uid");
        String lockid = MainApplication.getInstence().getLockid();
        Map<String,String> map=new HashMap<>();
        map.put("pageSize","10");
        map.put("currentPage","1");
        map.put("lockId",lockid);
        map.put("addType","0");
        map.put("addPerson",uid );
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.paswManager(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG",body);
                Icbean bean= gson.fromJson(body, new TypeToken<Icbean>() {}.getType());
                Icbean.DataBeanX data = bean.getData();
                List<Icbean.DataBeanX.DataBean> data1 = data.getData();
                Iterator<Icbean.DataBeanX.DataBean> iterator = data1.iterator();
                while (iterator.hasNext()){
                    Icbean.DataBeanX.DataBean next = iterator.next();
                    data3.add(next);
                }
                adapter=new IcListAdapter(data3,addICCardActivity.this);
                rc.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


}
