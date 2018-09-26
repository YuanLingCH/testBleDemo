package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 添加指纹
 * Created by lingyuan on 2018/6/4.
 */

public class FingerPrintManageActivity extends BaseActivity {
    Toolbar toolbar;
    List fingerData;
    RecyclerView Lv;
    ImageView iv_no_data;
    TextView tv_no_data;
    IcListAdapter adapter;
    LinearLayout ll_no_data;
    boolean isKitKat = false;
    SwipeRefreshLayout srf;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_finger_printer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initView();
        getData();
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

    private void initView() {
        ll_no_data=(LinearLayout) findViewById(R.id.ll_nodata);
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        Lv= (RecyclerView) findViewById(R.id.finger_lv);
        srf=(SwipeRefreshLayout) findViewById(R.id.srf);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        Lv.setLayoutManager(layoutManager);
        //添加指纹

        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //  adapter.resetDatas();
                getData();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.finger,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.seting_finger:
                Intent intent=new Intent(MainApplication.getInstence(),addfingerOneStepActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 查询指纹
     */
    public void getData() {
        fingerData=new ArrayList();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid = SharedUtils.getString("uid");
        //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}
        Map<String ,String> map=new HashMap<>();
        map.put("pageSize","100");
        map.put("currentPage","1");
        map.put("addPerson",uid);
        map.put("addType","2");
        map.put("lockId",lockid);
        Gson gson=new Gson();
        String s = gson.toJson(map);
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.queryUnlockStype(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){
                    Log.d("TAG",body);
                    Gson gson=new Gson();
                    Icbean bean= gson.fromJson(body, new TypeToken<Icbean>() {}.getType());
                    int code = bean.getCode();
                    if (code==1001){

                    Icbean.DataBeanX data = bean.getData();
                    List<Icbean.DataBeanX.DataBean> data1 = data.getData();
                    if (data1.size()==0){
                       Lv.setVisibility(View.GONE);
                        ll_no_data.setVisibility(View.VISIBLE);
                     iv_no_data.setImageResource(R.mipmap.finger);
                     tv_no_data.setText("暂无指纹");
                    }else  if (data1.size()>0){
                       // ll_no_data.setVisibility(View.GONE);
                        Lv.setVisibility(View.VISIBLE);
                        Iterator<Icbean.DataBeanX.DataBean> iterator = data1.iterator();
                        while (iterator.hasNext()){
                            Icbean.DataBeanX.DataBean next = iterator.next();
                           fingerData.add(next);
                        }
                        adapter=new IcListAdapter(fingerData,FingerPrintManageActivity.this);
                        Lv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    srf.setRefreshing(false);
                }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
