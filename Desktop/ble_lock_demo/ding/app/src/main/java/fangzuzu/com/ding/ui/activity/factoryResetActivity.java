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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansion.h_ble.BleController;
import com.hansion.h_ble.callback.OnReceiverCallback;
import com.hansion.h_ble.callback.OnWriteCallback;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.ble.jiamiandjiemi;
import fangzuzu.com.ding.utils.byteCunchu;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by lingyuan on 2018/7/3.
 */

public class factoryResetActivity extends BaseActivity {

    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "factoryResetActivity";
    private BleController mBleController;
    private StringBuffer mReciveString = new StringBuffer();
    Toolbar toolbar;
    byte[] aesks;
    String lockid;
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
        setContentView(R.layout.factory_reset_layout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        mBleController= BleController.getInstance();
        lockid= getIntent().getStringExtra("id");
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
        mBleController.registReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY, new OnReceiverCallback() {
            @Override
            public void onRecive(byte[] value) {
                byte[] decrypt = jiamiandjiemi.Decrypt(value, aesks);
                if (decrypt[0]==03&&decrypt[3]==00){
                    Toast.makeText(factoryResetActivity.this,"恢复出厂设置成功",Toast.LENGTH_LONG).show();
                    mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
                    mBleController.closeBleConn();
                    delectLock();
                }
            }
        });
    }

    /**
     * 删除锁
     */
    private void delectLock() {
        Retrofit re=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = re.create(apiManager.class);
        Call<String> call = manager.delectClock(lockid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Gson gson=new Gson();
               msg m = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = m.getCode();
                if (code==1001){
                    Log.d("TAG","删除成功");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

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
        //确定恢复出厂设置
    public void butClick(View view) {
        byte[] token = byteCunchu.getbyte("token3");
        byte[]data10=new byte[16];
        data10[0]=0x03;
        data10[1]=0x07;
        data10[2]=0x01;
        data10[3]=0x00;
        data10[4]=token[0];
        data10[5]=token[1];
        data10[6]=token[2];
        data10[7]=token[3];
        aesks = byteCunchu.getbyte("secretKeyBytes");
        byte[] encrypt6 = jiamiandjiemi.Encrypt(data10, aesks);
        Log.d("TAG","加密"+mBleController.bytesToHexString(encrypt6) + "\r\n");

        mBleController.writeBuffer(encrypt6, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG","发送成功");

            }
            @Override
            public void onFailed(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleController.unregistReciveListener(REQUESTKEY_SENDANDRECIVEACTIVITY);
    }
}
