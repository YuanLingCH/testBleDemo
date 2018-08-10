package fangzuzu.com.ding.ui.activity;

import android.util.Log;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by lingyuan on 2018/7/28.
 */

class PlatformActionListenerImpl implements PlatformActionListener {
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Log.d("TAG","回调完成");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.d("TAG","回调错误");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Log.d("TAG","回调取消");
    }
}
