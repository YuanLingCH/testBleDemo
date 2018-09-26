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
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.KeyManageAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.keyManagerBean;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.impl.OnMqttListener;
import fangzuzu.com.ding.presenter.MqttPresenter;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by lingyuan on 2018/6/20.
 */

public class ElectKeyManagerActivity extends BaseActivity implements OnMqttListener {
    Toolbar toolbar;
    KeyManageAdapter adapter;
    RecyclerView key_manage_lv;
    List data3;
    String lockid;
    SwipeRefreshLayout srf;
    boolean isKitKat = false;
    LinearLayout ll_nodata;
    ImageView iv_no_data;
    TextView tv_no_data;
    String uid;
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
        setContentView(R.layout.elect_key_manager_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
      lockid = getIntent().getStringExtra("id");
        getData();
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
        uid = SharedUtils.getString("uid");
        iv_no_data=(ImageView) findViewById(R.id.iv_no_data);
        tv_no_data=(TextView) findViewById(R.id.tv_no_data);
        ll_nodata=(LinearLayout) findViewById(R.id.ll_nodata);
        srf= (SwipeRefreshLayout) findViewById(R.id.srf_elect);
        key_manage_lv= (RecyclerView) findViewById(R.id.key_manage_lv);
        LinearLayoutManager lin=new LinearLayoutManager(MainApplication.getInstence());
        lin.setOrientation(OrientationHelper.VERTICAL);
        key_manage_lv.setLayoutManager(lin);
        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TAG","刷新走了");
                getData();
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

    public void getData() {
        data3=new ArrayList();
        Log.d("TAG","刷新走2");
        String uid = SharedUtils.getString("uid");
        Map<String,String>map=new HashMap<>();
        map.put("pageSize","10");
        map.put("currentPage","1");
        map.put("lockId",lockid);
        map.put("userId",uid );
        Log.d("TAG","lockid"+lockid);
        Log.d("TAG","uid"+uid);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        data3.clear();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.keyManager(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (!StringUtils.isEmpty(body)){


                keyManagerBean bean = gson.fromJson(body, new TypeToken<keyManagerBean>() {}.getType());
                keyManagerBean.DataBeanX data = bean.getData();
                if (data==null){
                    //加载没有数据界面
                    key_manage_lv.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_no_data.setImageResource(R.mipmap.no_key);
                    tv_no_data.setText("暂无钥匙");
                }else if (data!=null){
                    key_manage_lv.setVisibility(View.VISIBLE);
                    ll_nodata.setVisibility(View.GONE);
                    List<keyManagerBean.DataBeanX.DataBean> data1 = data.getData();
                    Iterator<keyManagerBean.DataBeanX.DataBean> iterator = data1.iterator();
                    while (iterator.hasNext()){
                        keyManagerBean.DataBeanX.DataBean next = iterator.next();
                        data3.add(next);
                    }

                    Log.d("TAG", body );
                    adapter=new KeyManageAdapter(data3,ElectKeyManagerActivity.this);

    /*                adapter.setOnItemLongClickListener(new  KeyManageAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, final int position, final String id , final String userId) {

                            View viewDialog = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                            final TextView tv = (TextView) viewDialog.findViewById(R.id.dialog_editname);
                            TextView tv_cancle= (TextView) viewDialog.findViewById(R.id.add_cancle);
                            EditText et_yanzhenpasw= (EditText) viewDialog.findViewById(R.id.et_yanzhenpasw);
                            et_yanzhenpasw.setVisibility(View.GONE);

                            TextView tv_submit= (TextView)viewDialog.findViewById(R.id.add_submit);
                            final AlertDialog dialog = new AlertDialog.Builder(ElectKeyManagerActivity.this)
                                    .setView(viewDialog)
                                    .create();
                            dialog.show();
                            tv_cancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();

                                }
                            });
                            tv_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    delectData(id,position ,userId);

                                }
                            });

                        }
                    });*/

                    key_manage_lv.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                }
                    srf.setRefreshing(false);
            }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }

        });

    }

    public void delectData(String id, final int postion, final String userId){
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.delectKey(id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Gson gson=new Gson();
                msg m = gson.fromJson(body, new TypeToken<msg>() {}.getType());

                int code = m.getCode();
                if (code==1001){
                    Log.d("TAG","删除成功");
                    Toast.makeText(ElectKeyManagerActivity.this,"删除成功",Toast.LENGTH_LONG).show();

                    data3.remove(postion);
                    adapter.notifyDataSetChanged();
                    MqttPresenter presenter=new MqttPresenter();
                    presenter.sendMqtt("az"+userId,ElectKeyManagerActivity.this);
                    presenter.sendMqtt("ios"+userId,ElectKeyManagerActivity.this);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    @Override
    public void mqttSuccess() {

    }

    @Override
    public void mqttFaild() {

    }
}
