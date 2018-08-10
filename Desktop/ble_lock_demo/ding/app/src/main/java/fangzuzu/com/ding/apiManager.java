package fangzuzu.com.ding;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by lingyuan on 2018/5/31.
 */

public interface apiManager {
    String baseUrl="http://fzhuzhu.cn:8766/login/";
    //验证码接口
    //http://fzhuzhu.cn:8766/login/VerificationPhone?username=18665261827
    //  http://fzhuzhu.cn:8766/news/ShortMessages?mobile=18665261827
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/permissionsUsers/news/ShortMessages")
    Call<String>getOuthCode(@Field("mobile") String s);

    // 注册接口
    //http://fzhuzhu.cn:8766/login/addUser?
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/permissionsUsers/login/addUser?")
    Call<String>registerUser(@Field("user") String s);
    //验证电话接口 http://192.168.0.119:443
   // http://fzhuzhu.cn:8766/login/VerificationPhone?username=18617145277
    //  https://www.fzzsaas.com/permissionsUsers/login/VerificationPhone?
    // http://192.168.0.119:443/permissionsUsers/login/VerificationPhone?
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/permissionsUsers/login/VerificationPhone?")
    Call<String>checkPhone(@Field("username") String s);

    //发送电子钥匙
    //http://192.168.0.118:7956/subset/insert?userSubsetModel=%7B%7D
    // https://www.fzzsaas.com/lockingSystem/subset/insert?
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/lockingSystem/subset/insert?")
    Call<String>sendkey(@Field("userSubsetModel") String s);

    // 发送密码 永久  IC卡
    // http://192.168.0.118:7956/unlock/insert?unlockModel=%7B%7D
    //  http://lock.fzhuzhu.cn:7956/unlock/insert?
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/lockingSystem/unlock/insert?")
    Call<String>sendPassward(@Field("unlockModel") String s);
    //修改密码
    // http://fzhuzhu.cn:8766/login/updateUserPassword?username=18617145277&password=123457
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/permissionsUsers/login/updateUserPassword")
    Call<String>upDataPassword(@Field("username") String username,@Field("password")String password);
//查询开锁方式   http://192.168.0.118:7956/unlock/selectPage


    @GET("https://www.fzzsaas.com/lockingSystem/unlock/selectPage?")
    Call<String>queryUnlockStype(@Query("unlockModel") String s);

    //权限设置  http://192.168.0.119:443/permissionsUsers/Permissions/getPermissions
    //     https://www.fzzsaas.com/permissionsUsers/Permissions/getPermissions

  @GET("https://www.fzzsaas.com/permissionsUsers/Permissions/getPermissions")
    Call<String>queryPermison(@Query("id") String id,@Query("lockId") String lockId);

   /* @POST("https://www.fzzsaas.com/permissionsUsers/Permissions/sharePermissions")
    Call<String>queryPermison();*/

    //查询锁用户列表  http://192.168.0.118:7956/sysLock/selectAll
    @GET("https://www.fzzsaas.com/lockingSystem/sysLock/selectAll")
    Call<String>getLockUserList(@Query("parentId") String parentId,@Query("userId") String userId);
    //锁添加蓝牙管理员后初始化   http://192.168.0.118:7956/sysLock/initLockModel
    //  https://www.fzzsaas.com/lockingSystem/sysLock/initLockModel?
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/lockingSystem/sysLock/initLockModel?")
    Call<String>initLockMolde(@Field("sysLockModel") String sysLockModel);
    //上传开锁记录  http://192.168.0.118:7956/operatin/insert
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/lockingSystem/operatin/insert?")
    Call<String>upDataOpenlockRecoder(@Field("operatinModel") String operatinModel);
    //获取开锁记录  http://192.168.0.118:7956
   // @GET("https://www.fzzsaas.com/lockingSystem/operatin/selectPage")
    @GET("https://www.fzzsaas.com/lockingSystem/operatin/selectPage")
    Call<String>getopenLockRecoder(@Query("operatinModel") String operatinModel);
//授权接口 http://192.168.0.119:443   https://www.fzzsaas.com/permissionsUsers/Permissions/sharePermissions?
    // http://192.168.0.119:443/permissionsUsers/Permissions/sharePermissions?
@FormUrlEncoded
@POST("https://www.fzzsaas.com/permissionsUsers/Permissions/sharePermissions?")
Call<String>authpeople(@Field("authorizationModel") String sysLockModel);

    //删除锁 https://127.0.0.1:9053/lockingSystem/sysLock/delete
    @GET("https://www.fzzsaas.com/lockingSystem/sysLock/delete")
    Call<String>delectClock(@Query("id") String id);
    //钥匙管理   https://127.0.0.1:9053/lockingSystem/subset/selectPage
    @GET("https://www.fzzsaas.com/lockingSystem/subset/selectPage")
    Call<String>keyManager(@Query("userSubsetModel") String userSubsetModel);

// 密码管理  ic卡 https://127.0.0.1:9053/lockingSystem/unlock/selectPage
// http://192.168.0.118:7956/unlock/selectPage
@GET("https://www.fzzsaas.com/lockingSystem/unlock/selectPage")
Call<String>paswManager(@Query("unlockModel") String unlockModel);
    // 查询ic卡  http://192.168.0.118:7956/unlock/selectPage
//删除钥匙

// 修改锁名 http://192.168.0.118:7956/sysLock/update
@FormUrlEncoded
@POST("https://www.fzzsaas.com/lockingSystem/sysLock/update?")
Call<String>upDatalockName(@Field("sysLockModel") String sysLockModel);

    //删除密码管理 IC身份证
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/lockingSystem/unlock/delete")
    Call<String>delectunlock(@Field("type") String type,@Field("ids") String ids,@Field("lockId") String lockId);
    //删除钥匙  http://192.168.0.118:7956/subset/delete
    @GET("https://www.fzzsaas.com/lockingSystem/subset/delete")
    Call<String>delectKey(@Query("ids") String ids);
    //Mqtt  https://127.0.0.1:9053/permissionsUsers/news/PointToPoint
    // http://192.168.0.119:443
    //https://www.fzzsaas.com/permissionsUsers/news/PointToPoint
    @FormUrlEncoded
    @POST("https://www.fzzsaas.com/permissionsUsers/news/PointToPoint")
    Call<String>sendMqtt(@Field("topicMqttv3") String topicMqttv3);
    //权限绑定创建蓝牙管理员
    @GET("https://www.fzzsaas.com/permissionsUsers//Permissions/BindingPermissions")
    Call<String>BindingPermissions(@Query("id") String id,@Query("number") String number);
    //删除锁
    @GET("https://www.fzzsaas.com/lockingSystem/sysLock/delete")
    Call<String>delctLock(@Query("id") String id);
}
