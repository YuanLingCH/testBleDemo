package fangzuzu.com.ding;

import android.content.Context;
import android.content.SharedPreferences;

import fangzuzu.com.ding.bean.UserBean;


/**
 * Created by Administrator on 2016/12/1.
 */
public class SharedUtils {
    public static void saveUser(UserBean user) {
        SharedPreferences shared = MainApplication.getInstence().getSharedPreferences("shared", Context.MODE_PRIVATE);
        shared.edit().putString("user", user.name + ":" + user.password).commit();
    }

    public static UserBean getUser() {
        SharedPreferences shared = MainApplication.getInstence().getSharedPreferences("shared", Context.MODE_PRIVATE);
        String value = shared.getString("user", "");
        //name:password
        String[] arr = value.split(":");

        if (arr == null || arr.length != 2) {
            return null;
        }


        UserBean user = new UserBean();
        user.name = arr[0];
        user.password = arr[1];
        return user;
    }
    public static void putString(String key,String value) {
        SharedPreferences shared = MainApplication.getInstence().getSharedPreferences("shared", Context.MODE_PRIVATE);

        shared.edit().putString(key,value).commit();
    }
    public static String getString(String key) {
        SharedPreferences shared = MainApplication.getInstence().getSharedPreferences("shared", Context.MODE_PRIVATE);

      return  shared.getString(key,"");
    }
}
