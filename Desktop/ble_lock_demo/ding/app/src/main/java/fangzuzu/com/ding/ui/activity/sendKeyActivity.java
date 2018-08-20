package fangzuzu.com.ding.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.PermissionAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.msg;
import fangzuzu.com.ding.bean.permisonBean;
import fangzuzu.com.ding.callback.PublishCallBackHandler;
import fangzuzu.com.ding.event.MessageEvent;
import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import fangzuzu.com.ding.widget.DatePicier;
import fangzuzu.com.ding.widget.EditTextDrawableClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by yuanling on 2018/5/14.
 */

public class sendKeyActivity extends BaseActivity {
    Toolbar toolbar;
    LinearLayout create_time;
    String username, usernumber;
    CheckBox timeLimte;
    ProgressDialog progressDialog;
    CheckBox forver;
    private static final int REQUEST_CODE = 1;
    LinearLayout loseTime;
    EditText electfrg_key_name;

    private TextView currentDate, currentTime;
    Button but_send;
    EditTextDrawableClick electfrg_inputaccount;
    RecyclerView rc;
    PermissionAdapter adapter;
    MqttAndroidClient client;
    String  parentid;
    TextView select_author;
    String str=new String();
  List<String> authordata=new ArrayList();
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
        setContentView(R.layout.electronfragment_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setStatusBar();
        parentid = SharedUtils.getString("uid");
        initgetdata();
        onclick();

        DatePicier.initDatePicker(currentDate, currentTime, sendKeyActivity.this);
        EventBus.getDefault().register(this);
        initView();
        /**获取client对象*/
        client = lockListActivity.getMqttAndroidClientInstace();
        publish();


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





    String lockid;
    String lockName;
    /**
     * 上一个界面传过来的数据
     */
    private void initgetdata() {
        lockid = getIntent().getStringExtra("id");
        parentid=SharedUtils.getString("uid");
        Log.d("TAG", "传来的"+lockid);
        Log.d("TAG", "传来的parent"+ parentid);
       lockName = MainApplication.getInstence().getLockName();
        Log.d("TAG", "传来的锁名"+ lockName);

    }

    private void publish() {
        String pubMessage = "abczzzzzz";
        byte[] message = pubMessage.getBytes();
        if (client != null) {
            /**发布一个主题:如果主题名一样不会新建一个主题，会复用*/
            try {
                client.publish("topicTest", message, 0, false, null, new PublishCallBackHandler(sendKeyActivity.this));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 运行在主线程
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        String string = event.getString();
        /**接收到服务器推送的信息，显示在右边*/
        if ("".equals(string)) {
            String topic = event.getTopic();
            MqttMessage mqttMessage = event.getMqttMessage();
            String s = new String(mqttMessage.getPayload());
            topic = topic + " : " + s;
            Log.d("TAG", topic);
            /**接收到订阅成功的通知,订阅成功，显示在左边*/
        }
    }


    List<permisonBean.DataBean> data3;

    private void initView() {
        data3=new ArrayList<>();

        //拿到权限
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())

                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Log.d("TAG","lock"+ lockid);
        Log.d("TAG","parentid"+ parentid);
       Call<String> call = manager.queryPermison(parentid, lockid);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG", "permison" + body);
                Gson gson = new Gson();
                permisonBean bean = gson.fromJson(body, new TypeToken<permisonBean>() {}.getType());
                List<permisonBean.DataBean> data = bean.getData();
               if (!data.isEmpty()){
                    Iterator<permisonBean.DataBean> iterator = data.iterator();
                    while (iterator.hasNext()){
                        permisonBean.DataBean next = iterator.next();
                        data3.add(next);
                    }
                }



            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    String endtime;

    //失效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(losetimeMessage event) {
        endtime = event.getTime();
        Log.d("TAG", "event" + endtime);
    }

    String createtime;

    //生效时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBuscreate(createtimeMessage event) {
        createtime = event.getTime();
        Log.d("TAG", "event" + currentTime);


    }
    String s2=null;
    String s4=null;
    public void onclick() {
        currentTime = (TextView) findViewById(R.id.electfrg_effect_time);
        create_time = (LinearLayout) findViewById(R.id.create_time);
        currentDate = (TextView) findViewById(R.id.electfrg_lose_time);
        timeLimte = (CheckBox) findViewById(R.id.time_limte);
        forver = (CheckBox) findViewById(R.id.forver);
        loseTime = (LinearLayout) findViewById(R.id.lose_time);
        but_send = (Button) findViewById(R.id.but_send);
        electfrg_key_name = (EditText) findViewById(R.id.electfrg_key_name);
       // electfrg_key_name.setText(lockName);
        StringUtils.fixInputMethodManagerLeak(sendKeyActivity.this);
        electfrg_inputaccount = (EditTextDrawableClick) findViewById(R.id.electfrg_inputaccount);
        electfrg_inputaccount.setDrawableRightListener(new EditTextDrawableClick.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                //获取联系人界面
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED) {//没有权限需要动态获取
                    //动态请求权限
                    ActivityCompat.requestPermissions(sendKeyActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);

                }
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
            }
        });
        //检查电话号码是不是注册
        electfrg_inputaccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){

                    if (!StringUtils.isEmpty(usernumber)){
                        String s = usernumber.replaceAll("-", "");
                        Log.d("TAG","电话切割"+s.length());
                        if (s.length()==11){
                            getPermision(s);
                        }

                        }
                  else if (!StringUtils.isEmpty(electfrg_inputaccount.getText().toString().trim())){
                        String phone = electfrg_inputaccount.getText().toString().trim();
                        Log.d("TAG","电话切割"+phone.length());
                        if (phone.length()==11){
                            getPermision(phone);
                        }

                    }
                }
                }


        });

        create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicier.getCustomDatePicker2().show(currentTime.getText().toString());

            }
        });

        loseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePicier.getCustomDatePicker1().show(currentTime.getText().toString());

            }
        });
        //永久类型
        forver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loseTime.setVisibility(View.GONE);
                   create_time.setVisibility(View.GONE);

                    // create_time.setVisibility(View.INVISIBLE);
                 timeLimte.setChecked(false);
                } else {
                    loseTime.setVisibility(View.VISIBLE);
                    create_time.setVisibility(View.VISIBLE);
                    timeLimte.setChecked(true);
                }
            }
        });
        //限时类型
        timeLimte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    forver.setChecked(false);
                } else {
                    forver.setChecked(true);
                }
            }
        });


        //发送
        but_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = electfrg_inputaccount.getText().toString().trim();
                String name = electfrg_key_name.getText().toString().trim();
                if (StringUtils.isEmpty(phone)||StringUtils.isEmpty(name)){
                    Log.d("TAG","锁命"+name);
                    Toast.makeText(sendKeyActivity.this,"请填写信息",Toast.LENGTH_LONG).show();
                }else {
                    if (!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(phone)){

                    Log.d("TAG","锁命"+name);




                if (forver.isChecked()){
                    Log.d("TAG","永久走了");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");// HH:mm:ss
                    //获取当前时间
                    Date date = new Date(System.currentTimeMillis());
                    // time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date));

                    Log.d("TAG","当前时间"+simpleDateFormat.format(date));
                    s2=simpleDateFormat.format(date);
                    Log.d("TAG","当前s2时间"+simpleDateFormat.format(date));
                    s4=s2;
                    Log.d("TAG","当前s4时间"+simpleDateFormat.format(date));

                } else  {
                //!StringUtils.isEmpty(currentTime+"")&&!StringUtils.isEmpty(endtime)
                    Log.d("TAG","限时走了");
                    String time = currentTime.getText().toString().trim();
                    String ss = unixTime.dateToStampone(time);
                    Log.d("TAG","开始时间戳"+ss);
                    String substring1 = ss.substring(0, ss.length() - 3);
                    int startTime = Integer.parseInt(substring1);
                    Log.d("TAG","开始时间"+ startTime);

//  currentDate

                    String endtime = currentDate.getText().toString().trim();
                    String send = unixTime.dateToStampone(endtime);
                    Log.d("TAG","结束时间戳"+send);
                    String substring1end = send.substring(0, send.length() - 3);
                    int endTime = Integer.parseInt(substring1end);
                    Log.d("TAG","结束时间"+ endTime);
                    if (startTime<endTime&&startTime!=endTime){



                    String s1 = currentTime.getText().toString().trim().replaceAll(" ", "-");
                    String s;
                    if (usernumber != null) {
                        s = usernumber.replaceAll("-", "");
                    } else {
                        s = electfrg_inputaccount.getText().toString().trim();
                    }
                    Log.d("TAG", "usernumbler" + s);
                    s2 = s1.replaceAll(":", "-");
                    Log.d("TAG", "s2" + s2);
                    if (!StringUtils.isEmpty(endtime)) {
                        String s3 = endtime.replaceAll(" ", "-");
                        s4 = s3.replaceAll(":", "-");
                    }

                    String s2time = unixTime. dateToStamptow(s2);
                    String s4time = unixTime. dateToStamptow(s4);
                    Log.d("TAG", "s2time" + s2time);
                    Log.d("TAG", "s4time" + s4time);
                    }else {
                        Toast.makeText(sendKeyActivity.this,"失效时间不能小于生效时间，并且2个时间不能相同",Toast.LENGTH_LONG).show();
                    }

             }
                        if (authordata.size()>0){
                    Log.d("TAG","userID"+ parentid);
                    if (!StringUtils.isEmpty(userId)){


                    Map map = new HashMap<>();
                    map.put("lockId", lockid);
                    map.put("userId", userId);
                    map.put("keyName", electfrg_key_name.getText().toString().trim());
                    map.put("startTime", s2);
                    map.put("endTime", s4);
                    map.put("parentId",  parentid);
                        map.put("firstPermissionIds",authStr);



                        showProgressDialog("","加载数据...");
                    final Gson gson = new Gson();
                    final String body = gson.toJson(map);
                    Log.d("TAG", "上传json" + body);
                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .baseUrl(apiManager.baseUrl)
                            .client(MainApplication.getInstence().getClient())
                            .build();
                    apiManager manager = retrofit.create(apiManager.class);
                    Call<String> call = manager.sendkey(body);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String value = response.body();
                            Log.d("TAG", "发送电子钥匙" + value);
                            msg s = gson.fromJson(value ,new TypeToken<msg>() {}.getType());
                            int code1 = s.getCode();
                            String code = s.getCode()+"";
                            Log.d("TAG", "上传数据2" + code1);
                            if (code.equals("1012")){
                                Toast.makeText(sendKeyActivity.this,"锁已经存在",Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                            }else if (code.equals("1010")){
                                Toast.makeText(sendKeyActivity.this,"锁已被删除",Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                            }else if (code.equals("1001")){
                                Log.d("TAG", "本地接口调用锁" + authordata.size());
                                sendMqtt("ios"+userId);
                                sendMqtt("az"+userId);

                                Log.d("TAG", "授权大小集合" + authordata.size());
                           /*     if (authordata.size()>0){
                                    Log.d("TAG", "授权大小集合" + authordata.size()+"走了");
                                    authPeople();
                                }else {
                                    Toast.makeText(sendKeyActivity.this,"请选择授权模块",Toast.LENGTH_LONG).show();
                                    hideProgressDialog();
                                }*/

                            }else if (code.equals("1002")){
                                Toast.makeText(sendKeyActivity.this,"发送失败",Toast.LENGTH_LONG).show();
                                hideProgressDialog();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                    }else {
                        Toast.makeText(sendKeyActivity.this,"钥匙只能发给已注册的账号",Toast.LENGTH_LONG).show();
                    }

                    }else {
                        Toast.makeText(sendKeyActivity.this,"请选择授权模块",Toast.LENGTH_LONG).show();
                    }

                    }else {
                        Toast.makeText(sendKeyActivity.this,"给锁命名",Toast.LENGTH_LONG).show();
                    }
                }

                }

        });

            //权限选择
        select_author= (TextView) findViewById(R.id.select_author);
        select_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //弹出对话框
                final View view = getLayoutInflater().inflate(R.layout.select_author_layout, null);
                rc= (RecyclerView) view.findViewById(R.id.rc_select);
                final TextView tv_cancle = (TextView) view.findViewById(R.id.auth_cancle);

                final TextView tv_submit = (TextView) view.findViewById(R.id.auth_sbumit);

                adapter=new PermissionAdapter(sendKeyActivity.this.data3,sendKeyActivity.this);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(sendKeyActivity.this, 4);
                gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
                rc.setLayoutManager(gridLayoutManager);
                rc.setAdapter(adapter);

              //  authordata.clear(); 不用清掉
                adapter.setItemClickListener(new PermissionAdapter.OnItemClickListener() {
                  @Override
                  public void onItemClick(String id,  int postion,  List<permisonBean.DataBean> data) {
                      Log.d("TAG", "上传数据" + id);

                      if (!authordata.contains(id)){
                                    authordata.add(id);
                                   data.get(postion).setFlag(true);
                                }else if (authordata.contains(id)){
                                   authordata.remove(id);
                                   data.get(postion).setFlag(false);
                               }
                               adapter.notifyDataSetChanged();
                      Iterator iterator = authordata.iterator();
                      while (iterator.hasNext()){
                          Object next = iterator.next();
                          Log.d("TAG", "遍历" + next);
                      }


                  }
              });
                final AlertDialog dialog = new AlertDialog.Builder(sendKeyActivity.this)
                        .setView(view)
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

                        if (authordata.size()==0){

                            Toast.makeText(sendKeyActivity.this,"至少选择一个模块",Toast.LENGTH_LONG).show();
                        }else if (authordata.size()>0){
                            authStr = authordata.toArray(new String[authordata.size()]);
                            for (int i = 0; i < authStr.length; i++) {
                                Log.d("TAG", "上传数据" + authStr[i]);
                            }

                            dialog.dismiss();
                        }

                    }
                });
            }
        });

    }

    /**
     * 发送电子钥匙第一步
     */
    private void updata(String s2,String s4) {


    }

    String[] authStr;

    private void authPeople() {
        String[]str={"f37618bc-686d-11e8-b04f-00163e0c1269","83a3378a-7b89-11e8-9505-00163e06d99e"};
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Map map=new HashMap<>();
        map.put("id","");
        map.put("parentId",parentid);
        map.put("lockNum",lockid);
        map.put("uid",userId);
        map.put("firstPermissionId",authStr);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG","授权json"+s);
       Call<String> call = manager.authpeople(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG", "授权" + body);
               msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = s.getCode();
                if (code==1001){
                  //  Toast.makeText(sendKeyActivity.this,"发送成功",Toast.LENGTH_LONG).show();
                    //发送mqtt
                    Timer timer =new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            sendMqtt("ios"+userId);
                            sendMqtt("az"+userId);
                        }
                    },5000);

                    View view = getLayoutInflater().inflate(R.layout.custom_diaglog_layut, null);
                    final TextView tv = (TextView) view.findViewById(R.id.dialog_editname);
                    TextView tv_cancle= (TextView) view.findViewById(R.id.add_cancle);
                    EditText et_yanzhenpasw= (EditText) view.findViewById(R.id.et_yanzhenpasw);
                    et_yanzhenpasw.setVisibility(View.INVISIBLE);
                    TextView tv1= (TextView) view.findViewById(R.id.tv);
                    tv1.setVisibility(View.INVISIBLE);
                    tv.setText("发送钥匙成功");
                    tv.setGravity(Gravity.CENTER);
                    TextView tv_submit= (TextView) view.findViewById(R.id.add_submit);
                    final AlertDialog dialog = new AlertDialog.Builder(sendKeyActivity.this)
                            .setView(view)
                            .create();
                    dialog.show();
                    tv_cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            hideProgressDialog();
                        }
                    });
                    tv_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            hideProgressDialog();
                        }
                    });


                }else if (code==1002){
                   // Toast.makeText(sendKeyActivity.this,"发送失败",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 发送mqtt
     */

    private void sendMqtt(String to) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager api = retrofit.create(apiManager.class);
        Map<String ,String>data=new HashMap<>();
        data.put("from",parentid);
        data.put("to",to);
        data.put("text","发内容");
        data.put("time","2018_06");
        Map msg=new HashMap<>();
        msg.put("code","10086");
        msg.put("data",data);

        Map map=new HashMap();
        map.put("topic","fzzchat.PTP");
        map.put("topicid",to);
        map.put("msg",msg);
        final Gson gson=new Gson();
        String s = gson.toJson(map);
        Log.d("TAG","MMQTT"+s);
        Call<String> call = api.sendMqtt(s);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG","MMQTT"+body);

                Log.d("TAG", "授权" + body);
                msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = s.getCode();
                if (code==1001){
                    Toast.makeText(sendKeyActivity.this,"发送钥匙成功",Toast.LENGTH_LONG).show();
                    hideProgressDialog();

                }else if (code==1002){
                    Toast.makeText(sendKeyActivity.this,"发送钥匙失败",Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver reContentResolverol = getContentResolver();
            Uri contactData = data.getData();
            @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            while (phone.moveToNext()) {
                usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                // electfrg_inputaccount.setText(usernumber+" ("+username+")");
                electfrg_inputaccount.setText(usernumber);
                Log.d("TAG", ",,," + usernumber);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//判断是否给于权限

            } else {
                Toast.makeText(this, "请开启权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
    String userId;
    public void getPermision(String str ) {

        //校验电话号码
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .build();
        apiManager api = retrofit.create(apiManager.class);


        Call<String> call = api.checkPhone(str);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();

                Gson gson=new Gson();
                msg s = gson.fromJson(body, new TypeToken<msg>() {}.getType());
                int code = s.getCode();
                if (code==1002){
                    userId = s.getData();
                }else if (code==1001){
                    Toast.makeText(sendKeyActivity.this,"电话号码没注册,请注册",Toast.LENGTH_LONG).show();
                }
                Log.d("TAG","验证号码"+body);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }
    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
