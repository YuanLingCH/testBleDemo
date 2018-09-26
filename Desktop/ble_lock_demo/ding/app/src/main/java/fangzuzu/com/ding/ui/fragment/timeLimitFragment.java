package fangzuzu.com.ding.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
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
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.event.passwordMessage;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.widget.DatePicier;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by yuanling on 2018/5/15.
 */

public class timeLimitFragment extends BaseFragment {
  LinearLayout  create_time ,lose_time;
    private TextView currentDate, currentTime;
    TextView electfrg_effect_time,electfrg_lose_time;
    Button create_pasrord;
    TextView   tv_pasw,tv;

    @Override
    protected int getLayoutId() {
        return R.layout.time_limit_fragment_layout;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event){
        endtime = event.getTime();
        currentDate.setText(endtime);

        Log.d("TAG","event1"+endtime);
    }

    String createtime;
    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event){
        createtime = event.getTime().toString().trim();
        currentTime.setText( createtime);
        Log.d("TAG","event2"+currentTime);


    }


    @Override
    protected void initViews() {
        tv= (TextView) root.findViewById(R.id.tv);
        // 密码在24小时内至少使用一次,否则失效
        String str="密码在<font color='#FF0000'>24小时内</font>至少使用一次,否则失效";
        tv.setTextSize(14);
        tv.setText(Html.fromHtml(str));
        tv_pasw= (TextView) root.findViewById(R.id.tv_pasw);
        create_pasrord= (Button) root.findViewById(R.id.create_pasrord);
        currentTime= (TextView) root.findViewById(R.id.electfrg_effect_time);
        currentDate= (TextView) root.findViewById(R.id.electfrg_lose_time);
        create_time= (LinearLayout) root.findViewById(R.id.create_time);
        lose_time=(LinearLayout) root.findViewById(R.id.lose_time);
        DatePicier.initDatePicker(currentDate, currentTime, getContext());
        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(currentTime.getText().toString());

                Log.d("TAG","时间开始");
            }
        });

        lose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker1().show(currentDate.getText().toString());
                Log.d("TAG","时间结束");

            }
        });




    }
    String s2;
    String s4;
    String s1;
    String s3;
    @Override
    protected void initEvents() {

        //接收数据
        final String lockName = MainApplication.getInstence().getLockName();
        final String allow = MainApplication.getInstence().getAllow();
        final String lockid = MainApplication.getInstence().getLockid();
        final String uid= SharedUtils.getString("uid");
        Log.d("TAG","接收数据"+lockName);
        Log.d("TAG","接收数据"+allow);
        Log.d("TAG","接收数据"+lockid );
            //生成密码
        create_pasrord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = currentTime.getText().toString().trim();
                String s = unixTime.dateToStampone(time);
                Log.d("TAG","开始时间戳"+s);
                String substring1 = s.substring(0, s.length() - 3);
                int startTime = Integer.parseInt(substring1);
                Log.d("TAG","开始时间"+ startTime);

//  currentDate


                String endtime = currentDate.getText().toString().trim();
                String send = unixTime.dateToStampone(endtime);
                Log.d("TAG","结束时间戳"+send);
                String substring1end = send.substring(0, send.length() - 3);
                int endTime = Integer.parseInt(substring1end);
                Log.d("TAG","结束时间"+ endTime);

                if (StringUtils.isEmpty(currentTime+"")){

                    Toast.makeText(getActivity(),"生效时间不能为空",Toast.LENGTH_LONG).show();
                }else if (StringUtils.isEmpty(endtime)){
                    Toast.makeText(getActivity(),"请选择失效时间",Toast.LENGTH_LONG).show();
                }else if (!StringUtils.isEmpty(currentTime+"")&&!StringUtils.isEmpty(endtime)){
                    if (startTime<endTime&&startTime!=endTime){



                  //  s1 = createtime.replaceAll(" ", "-");
                    s1 = currentTime.getText().toString().trim().replaceAll(" ", "-");

                Log.d("TAG","s1"+ s1);
                s2 = s1.replaceAll(":", "-");

                Log.d("TAG","s2"+ s2);
                String trim = s2.replaceAll("-", "").trim();
                Log.d("TAG","s2Time"+ trim );

                s3 =   endtime.replaceAll(" ", "-");
                s4 = s3.replaceAll(":", "-");
                Log.d("TAG","s4"+s4);



                Map<String,String> map= new HashMap<>();

                //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}

                map.put("lockId",lockid);
                map.put("unlockName",lockName);
                map.put("unlockFlag","");
                map.put("allow",allow);
                map.put("addPerson",uid);
                map.put("forWay","");
                map.put("startTime",s2);
                map.put("endTime",s4);
                map.put("addType","3");
                map.put("unlockType","0");
                Gson gson=new Gson();
                String value = gson.toJson(map);
                    Log.d("TAG","上传数据"+value);

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
                        if (!StringUtils.isEmpty(body)){


                        Log.d("TAG","上传数据"+body);
                        Gson gson1=new Gson();
                        msg s = gson1.fromJson(body, new TypeToken<msg>() {}.getType());
                        String data = s.getData();
                        String regex = "(.{5})";
                        String s1 = data.replaceAll(regex, "$1\t");
                        tv_pasw.setText(s1);

                        Log.d("TAG","上传数据1");
                        passwordMessage pamsg=new passwordMessage();
                        pamsg.setPassWord(s.getData());
                        pamsg.setTimeStart(s2);
                        pamsg.setTimeEnd(s4);
                        pamsg.setType("0");
                        pamsg.getPassWord();
                        Log.d("TAG", pamsg.getPassWord());
                       EventBus.getDefault().post(pamsg);
                        Log.d("TAG","上传数据2");
                        }else {
                            Toast.makeText(MainApplication.getInstence(),"服务器开小差了,请重试",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

                    }else {
                        Toast.makeText(getActivity(),"失效时间必须比当前时间和生效时间晚",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    @Override
    protected void initData() {

    }
}
