package fangzuzu.com.ding.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.apiManager;
import fangzuzu.com.ding.bean.UserBean;
import fangzuzu.com.ding.bean.loginBean;
import fangzuzu.com.ding.impl.LoginService;
import fangzuzu.com.ding.impl.OnLoginListener;
import fangzuzu.com.ding.model.IUserModel;
import fangzuzu.com.ding.model.UserModel;
import fangzuzu.com.ding.view.ILoginView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.mob.tools.utils.DeviceHelper.getApplication;

/**
 * Created by Administrator on 2016/12/1.
 */
public class LoginPresenter {
    // 声明数据模型接口实例
    IUserModel model;
    //声明UI接口实例
    ILoginView view;

    public LoginPresenter(ILoginView view) {
        //实例化Model
        model = new UserModel();
        //实例化view
        this.view = view;
    }

    public void saveUser(UserBean user) {
        model.saveUser(user);
    }

    /**
     * 设置控件显示文本
     */
    public void setUser() {
        //显示名称
        view.setName(model.getName());
        //显示密码
        view.setPassword(model.getPassword());
    }

    public void login(final UserBean bean, final OnLoginListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(MainApplication.getInstence().getClient())
                .baseUrl(apiManager.baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        LoginService service = retrofit.create(LoginService.class);

        Call<String> call = service.login(bean.name, bean.password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String value = response.body();
                Log.d("TAG", "登录结果：" + value);
                Gson gson=new Gson();
                loginBean s = gson.fromJson(value, new TypeToken<loginBean>() {}.getType());
                if(s!=null){

                loginBean.DataBean data = s.getData();

                String code = s.getCode()+"";

                //如果value中，包含 “不匹配”，表示登录失败
                    if (code.equals("1002")) {
                    //登录失败
                    listener.loginFaild();
                } else if(code.equals("1001")){
                    //登录成功

                        String uid = data.getUid();
                        String partid = (String) data.getPartid();
                        //保存uid 全局变量
                        final MainApplication app = (MainApplication )getApplication();
                        SharedUtils.putString("uid",uid);
                        SharedUtils.getString("uid");
                        Log.d("TAG", "存uid"+SharedUtils.getString("uid"));
                        SharedUtils.putString("partid",partid);
                        SharedUtils.getString("partid");
                        Log.d("TAG", "存partid"+ SharedUtils.getString("partid"));
                        listener.loginSuccess(bean);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG", "登录失败");
                listener.loginFaild();
            }
        });
        }



}
