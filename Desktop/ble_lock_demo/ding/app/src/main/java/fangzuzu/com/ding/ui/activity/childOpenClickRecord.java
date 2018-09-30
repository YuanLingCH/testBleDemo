package fangzuzu.com.ding.ui.activity;

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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.adapter.openLockRecodeAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.openLockRecoderBean;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/9/19.
 */

public class childOpenClickRecord extends BaseActivity {
    boolean isKitKat = false;
    Toolbar toolbar;
    RecyclerView rc;
    SwipeRefreshLayout srf;
    ImageView iv_no_data;
    TextView tv_no_data;
    LinearLayout ll_no_data;
    List data3;
    openLockRecodeAdapter adapter;
    String uid;
    String keyName;
    String lockId;
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
        setContentView(R.layout.child_open_lock_record_activity_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        initViews();
        getData();
    }

    private void initViews() {
         uid = getIntent().getStringExtra("uid");
         keyName = getIntent().getStringExtra("keyName");
        lockId = getIntent().getStringExtra("lockId");
        rc=(RecyclerView) findViewById(R.id.lv);
        srf=(SwipeRefreshLayout) findViewById(R.id.srf);
        ll_no_data=(LinearLayout) findViewById(R.id.ll_nodata);
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rc.setLayoutManager(layoutManager);
        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //  adapter.resetDatas();
                getData();   //得到数据
            }
        });
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

    public void getData() {
       Log.d("TAG","锁id"+lockId);
        Log.d("TAG","锁名"+keyName);
        Log.d("TAG","userid"+uid);
        data3=new ArrayList();
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = re.create(apiManager.class);
        Map<String,String> map=new HashMap<>();
        map.put("pageSize","100");
        map.put("currentPage","1");
        map.put("key","");
        map.put("lockId",lockId);
        map.put("userId",uid );
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG", "上传json"+s);
        data3.clear();
        Call<String> call = manager.getopenLockRecoder(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)) {
                    Log.d("TAG", body);
                    openLockRecoderBean bean = gson.fromJson(body, new TypeToken<openLockRecoderBean>() {
                    }.getType());

                    int code = bean.getCode();
                    if (code == 1001) {
                        openLockRecoderBean.DataBeanX data = bean.getData();
                        List<openLockRecoderBean.DataBeanX.DataBean> data1 = data.getData();
                        if (data1.size() == 0) {
                            rc.setVisibility(View.GONE);
                            ll_no_data.setVisibility(View.VISIBLE);
                            iv_no_data.setImageResource(R.mipmap.no_open_door);
                            tv_no_data.setText("暂无开锁记录");

                        } else if (data1.size() > 0) {
                            ll_no_data.setVisibility(View.GONE);
                            rc.setVisibility(View.VISIBLE);

                            Iterator<openLockRecoderBean.DataBeanX.DataBean> iterator = data1.iterator();
                            while (iterator.hasNext()) {
                                openLockRecoderBean.DataBeanX.DataBean next = iterator.next();
                                data3.add(next);
                            }
                            adapter = new openLockRecodeAdapter(data3, childOpenClickRecord.this);
                            rc.setAdapter(adapter);
                            srf.setRefreshing(false);

                        } else {
                            Toast.makeText(MainApplication.getInstence(), "没有数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
