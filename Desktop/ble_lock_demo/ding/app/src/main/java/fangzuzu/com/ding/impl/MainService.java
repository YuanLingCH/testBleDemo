package fangzuzu.com.ding.impl;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2016/12/2.
 */
public interface MainService {
    @GET("student.php/Index/index")
    Call<String> getUserInfo();
}
