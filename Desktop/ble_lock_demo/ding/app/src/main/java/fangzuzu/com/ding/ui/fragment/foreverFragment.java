package fangzuzu.com.ding.ui.fragment;


import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import fangzuzu.com.ding.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 永久密码
 * Created by yuanling on 2018/5/15.
 */

public class foreverFragment extends BaseFragment {
    Button but_send_pasw;
    TextView tv_pasw;
    TextView tv;
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
        final String  adminUserId = SharedUtils.getString("adminUserId");
        Log.d("TAG","接收数据"+lockName);
        Log.d("TAG","接收数据"+allow);
        Log.d("TAG","接收数据"+lockid );
        tv= (TextView) root.findViewById(R.id.tv);
        // 密码在24小时内至少使用一次,否则失效
        String str="密码在<font color='#FF0000'>24小时内</font>至少使用一次,否则失效";
        tv.setTextSize(14);
        tv.setText(Html.fromHtml(str));

        but_send_pasw= (Button) root.findViewById(R.id.but_send_pasw);
        but_send_pasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.equals(adminUserId)){



                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");// HH:mm:ss
                //获取当前时间
                Date date = new Date(System.currentTimeMillis());
                // time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));

                Log.d("TAG","当前时间"+simpleDateFormat.format(date));




                Map<String,String> map= new HashMap<>();

                //   {"lockId":"5b97192a-631d-11e8-b04f-00163e0c1269","userId":"aaaaa000200","keyName":"给小明的","startTime":"2018-12-21-12-14","endTime":"2018-12-21-12-15","parentId":"aaaaa0003"}
                final String time=simpleDateFormat.format(date);
                map.put("lockId",lockid);
                map.put("unlockName",lockName);
                map.put("unlockFlag","");
                map.put("allow",allow);
                map.put("addPerson",uid);
                map.put("forWay","");
                map.put("startTime",time);
                map.put("endTime",time);
                map.put("addType","3");
                map.put("unlockType","1");
                Gson gson=new Gson();
                String value = gson.toJson(map);
                Log.d("TAG","拼接"+ value);
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
                        if (!StringUtils.isEmpty(body )){
                        Log.d("TAG","上传数据"+body);
                        Gson gson1=new Gson();
                        msg s = gson1.fromJson(body, new TypeToken<msg>() {}.getType());
                        String data = s.getData();
                        String regex = "(.{5})";
                        String s1 = data.replaceAll(regex, "$1\t");
                        tv_pasw.setText(s1);
                        passwordMessage pamsg=new passwordMessage();
                        pamsg.setPassWord(s.getData());
                        pamsg.getPassWord();
                        pamsg.setTimeStart(time);
                        pamsg.setTimeEnd(time);
                        pamsg.setType("1");
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
                    Toast.makeText(MainApplication.getInstence(),"你不是管理员，没永久密码权限",Toast.LENGTH_LONG).show();
                }



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
