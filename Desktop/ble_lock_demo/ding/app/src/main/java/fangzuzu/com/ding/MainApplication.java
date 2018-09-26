package fangzuzu.com.ding;

import android.app.Activity;
import android.app.Application;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2016/12/1.
 */
public class MainApplication extends Application {
    String uid;
    String partid;
    String allow;
    String lockid;
    String lockName;
    String pasword;
    String elect;
    String mac;
    String appVersion;
    String startTime;
    String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPasword() {
        return pasword;
    }

    public void setPasword(String pasword) {
        this.pasword = pasword;
    }

    public String getElect() {
        return elect;
    }

    public void setElect(String elect) {
        this.elect = elect;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    public String getLockid() {
        return lockid;
    }

    public void setLockid(String lockid) {
        this.lockid = lockid;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getPartid() {
        return partid;
    }

    public void setPartid(String partid) {
        this.partid = partid;
    }

    private List<Activity> oList;//用于存放所有启动的Activity的集合
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    static MainApplication app;

    OkHttpClient client;

    //请求的Cookies
    List<Cookie> cookiesStore = new ArrayList<>();






    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        oList = new ArrayList<Activity>();

        CrashReport.initCrashReport(MainApplication.getInstence(), "09743bc044", false);



    }

    OkHttpClient okHttpClient;
    ClearableCookieJar cookieJar;
    public OkHttpClient getClient() {
/*        if (client == null) {
            //保存Cookies
            //添加请求头
            client = new OkHttpClient.Builder()
                   .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            Log.d("ytmfdw", cookies.toArray().toString());
                            //拿到登录成功后的Cookies，
                            if (cookiesStore.size() == 0) {
                                if (cookies != null) {
                                    cookiesStore.addAll(cookies);
                                }
                            }
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            //返回登录成功后的Cookies
                            return cookiesStore;
                        }
                    }).build();
        }
        return client;*/

        if (cookieJar==null){
            cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MainApplication.getInstence()));
        }

        if (okHttpClient==null){
            okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();
        }

        return okHttpClient;
    }


    public static MainApplication getInstence() {
        return app;
    }


    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }



}
