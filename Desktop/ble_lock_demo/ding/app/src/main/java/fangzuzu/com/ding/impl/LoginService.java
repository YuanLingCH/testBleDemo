package fangzuzu.com.ding.impl;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2016/12/2.
 */
public interface LoginService {
    //  http://fzhuzhu.cn:8766/login/else/login?uname=18665261827&upwd=123456
    //  http://fzhuzhu.cn:8766/login/else/login?
    //  http://192.168.0.116:8799/login/else/login?       http://192.168.0.119:8799/login/else/login?
    // https://www.fzzsaas.com/permissionsUsers/login/else/login?

@FormUrlEncoded
@POST("https://www.fzzsaas.com/permissionsUsers/login/else/login?")
    Call<String> login(@Field("uname") String name, @Field("upwd") String password);
        }
