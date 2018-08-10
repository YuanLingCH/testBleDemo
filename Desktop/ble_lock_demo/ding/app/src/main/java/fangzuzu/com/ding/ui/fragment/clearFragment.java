package fangzuzu.com.ding.ui.fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.event.passwordMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by yuanling on 2018/5/15.
 */

public class clearFragment extends BaseFragment{
    Button but_send_pasw;
    TextView tv_pasw;
    public static int flag = 0;
    @Override
    protected int getLayoutId() {
        return R.layout.forever_fragment_layout;
    }

    @Override
    protected void initViews() {

        //接收数据
        final String lockName = MainApplication.getInstence().getLockName();
        final String allow = MainApplication.getInstence().getAllow();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid = SharedUtils.getString("uid");
        Log.d("TAG","接收数据"+lockName);
        Log.d("TAG","接收数据"+allow);
        Log.d("TAG","接收数据"+lockid );

        but_send_pasw= (Button) root.findViewById(R.id.but_send_pasw);
        but_send_pasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");// HH:mm:ss
                    //获取当前时间
                    Date date = new Date(System.currentTimeMillis());
                    // time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));

                    Log.d("TAG","当前时间"+simpleDateFormat.format(date));




                    Map<String,String> map= new HashMap<>();

                    //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}

                    map.put("lockId",lockid);
                    map.put("unlockName",lockName);
                    map.put("unlockFlag","");
                    map.put("allow",allow);
                    map.put("addPerson",uid);
                    map.put("forWay","");
                    map.put("startTime",simpleDateFormat.format(date));
                    map.put("endTime",simpleDateFormat.format(date));
                    map.put("addType","3");
                    map.put("unlockType","4");
                    Gson gson=new Gson();
                    String value = gson.toJson(map);

                    Retrofit retrofit=new Retrofit.Builder()
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .client(MainApplication.getInstence().getClient())
                            .baseUrl(apiManager.baseUrl)
                            .build();
                    apiManager manager = retrofit.create(apiManager.class);
                    Call<String> call = manager.sendPassward(value);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String body = response.body();
                            Log.d("TAG","上传数据"+body);
                            Gson gson1=new Gson();
                            msg s = gson1.fromJson(body, new TypeToken<msg>() {}.getType());
                            tv_pasw.setText(s.getData());
                            passwordMessage pamsg=new passwordMessage();
                            pamsg.setPassWord(s.getData());
                            pamsg.getPassWord();
                            Log.d("TAG", pamsg.getPassWord());
                            EventBus.getDefault().post(pamsg);
                            Log.d("TAG","上传数据2");

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });

                    flag=1;




            }
        });


    }

    @Override
    protected void initEvents() {
        tv_pasw= (TextView) root.findViewById(R.id.tv_pasw);


    }

    @Override
    protected void initData() {

    }
}
