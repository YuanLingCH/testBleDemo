package fangzuzu.com.ding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import fangzuzu.com.ding.R;

/**
 * Created by lingyuan on 2018/6/27.
 */

public class firstAddBle extends BaseActivity {
    ImageView iv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_add_ble_layout);
        iv= (ImageView) findViewById(R.id.iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(firstAddBle.this,lockListActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
