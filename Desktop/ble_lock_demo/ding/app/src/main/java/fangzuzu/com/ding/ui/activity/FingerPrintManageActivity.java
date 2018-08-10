package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.adapter.AbsBaseAdapter;
import fangzuzu.com.ding.apiManager;
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
    TextView add_finger,cler_finger;
    ListView Lv;
    AbsBaseAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_finger_printer);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        getData();
    }

    private void initView() {
        Lv= (ListView) findViewById(R.id.finger_lv);
        add_finger= (TextView) findViewById(R.id.add_finger);
        //添加指纹
        add_finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(),addfingerOneStepActivity.class);
                startActivity(intent);


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

    /**
     * 查询指纹
     */
    public void getData() {

        //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}
        Map<String ,String> map=new HashMap<>();
        map.put("pageSize","10");
        map.put("currentPage","1");
        map.put("addPerson","aaaaa000200");
        map.put("addType","3");
        map.put("lockId","5b97192a-631d-11e8-b04f-00163e0c1269");
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
                Log.d("TAG",body);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
