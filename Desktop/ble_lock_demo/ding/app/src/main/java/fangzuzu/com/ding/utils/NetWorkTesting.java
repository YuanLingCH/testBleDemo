package fangzuzu.com.ding.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络测试
 * Created by lingyuan on 2018/7/27.
 */

public class NetWorkTesting {
    private Context context = null;

    public NetWorkTesting(Context context) {
        this.context = context;
    }

    public boolean isNetWorkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected())
                return true;
            else
                return false;
        } else {
            return false;
        }
    }
}
