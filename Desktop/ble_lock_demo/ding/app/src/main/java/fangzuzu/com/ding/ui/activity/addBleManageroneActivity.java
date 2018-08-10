package fangzuzu.com.ding.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hansion.h_ble.BleController;

import fangzuzu.com.ding.R;


/**
 * Created by lingyuan on 2018/6/23.
 */

public class addBleManageroneActivity extends BaseActivity {
    Toolbar toolbar;
    private BleController mBleController;
    public static final String REQUESTKEY_SENDANDRECIVEACTIVITY = "addBleManageActivity";
    private StringBuffer mReciveString = new StringBuffer();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ble_manager_one_layoout);

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO 在新的界面要获取实例，无需init
        mBleController = BleController.getInstance();
        initview();
        initlize();
        String ble1="020305080903";
        int b = Integer.parseInt(ble1.replaceAll("^0[x|X]", ""), 16);
        Log.d("TAG","十进制"+b);
    }

    private void initlize() {
    }

    private void initview() {

    }

    public void butClick(View view) {

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
