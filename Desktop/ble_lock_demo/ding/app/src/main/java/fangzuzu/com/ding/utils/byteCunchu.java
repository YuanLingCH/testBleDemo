package fangzuzu.com.ding.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Base64;

import fangzuzu.com.ding.MainApplication;

/**
 * Created by lingyuan on 2018/6/14.
 */

public class byteCunchu {


    public static  void put(byte [] b,String str){
        SharedPreferences sharedPreferences = MainApplication.getInstence().getSharedPreferences("demo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String imageString = new String(Base64.encode(b,Base64.DEFAULT));
        editor.putString(str, imageString);
        editor.commit();
    }

public  static  byte[]getbyte(String str){
    SharedPreferences sharedPreferences = MainApplication.getInstence().getSharedPreferences("demo",Activity.MODE_PRIVATE);
    String string = sharedPreferences.getString(str, "");
    byte[] b = Base64.decode(string.getBytes(), Base64.DEFAULT);
    return b;
}

    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }


}
