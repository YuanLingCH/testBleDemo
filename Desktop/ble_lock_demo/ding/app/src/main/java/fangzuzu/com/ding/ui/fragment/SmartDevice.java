package fangzuzu.com.ding.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.adapter.lockListSmartAdapter;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.userLockBean;
import fangzuzu.com.ding.ui.activity.addSmartServiceActivityOne;
import fangzuzu.com.ding.utils.screenAdapterUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by yuanling on 2018/5/12.
 */

public class SmartDevice extends BaseFragment {
    RecyclerView smart_rv;
    lockListSmartAdapter adapter;
    List data3;
    ImageView iv;
    Toolbar toolbar;
    boolean isKitKat = false;
    @Override
    protected int getLayoutId() {
        return R.layout.smart_fragment_layout;

    }

    @Override
    protected void initViews() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isKitKat = true;
        }
        toolbar = (Toolbar)root. findViewById(R.id.toolbar);

        toolbar.setTitle("");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getUserLockList();
        data3=new ArrayList();

        iv= (ImageView) root.findViewById(R.id.iv);
        smart_rv= (RecyclerView) root.findViewById(R.id.smart_rv);
        LinearLayoutManager manager=new LinearLayoutManager(MainApplication.getInstence());
        manager.setOrientation(OrientationHelper.VERTICAL);
        smart_rv.setLayoutManager(manager);
        adapter=new lockListSmartAdapter(data3,MainApplication.getInstence());

        //点击添加设备
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainApplication.getInstence(), addSmartServiceActivityOne.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainApplication.getInstence().startActivity(intent);

            }
        });
        setStatusBar();

    }
    protected void setStatusBar() {
        if (isKitKat){



            int statusH = screenAdapterUtils.getStatusHeight(MainApplication.getInstence());
            //获取ToolBar的布局属性，设置ToolBar的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.height = params.height + statusH;
            toolbar.setLayoutParams(params);
            //设置ToolBar的PaddingTop属性
            toolbar.setPadding(0, statusH, 0, 0);
            getActivity().getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Log.d("TAG","普通");
        }
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 请求网络数据
     */
    public void getUserLockList() {
        String uid = SharedUtils.getString("uid");
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(apiManager.baseUrl)
                .client(MainApplication.getInstence().getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        apiManager manager = retrofit.create(apiManager.class);
        Call<String> call = manager.getLockUserList("aa123456", uid);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                Log.d("TAG",body );
                Gson gson=new Gson();
                userLockBean bean = gson.fromJson(body, new TypeToken<userLockBean>() {}.getType());
                int code = bean.getCode();

                if (code==1001){
                    userLockBean.DataBean data = bean.getData();
              /*  List<userLockBean.DataBean.ParentLockBean> parentLock = data.getParentLock();
                Iterator<userLockBean.DataBean.ParentLockBean> iterator = parentLock.iterator();
                while (iterator.hasNext()){
                    userLockBean.DataBean.ParentLockBean next = iterator.next();
                //  data3.add(next);

                }*/
                    data3.clear();// 防止加载重复数据
                    List<userLockBean.DataBean.UserLockBean> userLock = data.getUserLock();
                    Iterator<userLockBean.DataBean.UserLockBean> iterator1 = userLock.iterator();
                    while (iterator1.hasNext()){
                        userLockBean.DataBean.UserLockBean next = iterator1.next();

                        data3.add(next);
                    }

                    smart_rv.setAdapter(adapter);

                    adapter.notifyDataSetChanged();


                    Log.d("TAG",body);
                }else if(code==1002){
                    Log.d("TAG","网络错误");

                }





            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }
}
