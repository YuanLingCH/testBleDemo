package fangzuzu.com.ding.broadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import fangzuzu.com.ding.utils.delectFile;

/**
 * Created by lingyuan on 2018/8/31.
 */

public class InitApkBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//  RecursionDeleteFile(new File(Environment.getExternalStorageDirectory() + "/download/youni/"));

        String installPath= "/"+"tianxiaxiaochi";
        File downloadFile = new File(Environment.getExternalStorageDirectory() + "tianxiaxiaochi");
        Log.d("TAG","广播走了"+installPath);
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            delectFile.RecursionDeleteFile(downloadFile);
            Toast.makeText(context , "监听到系统广播添加" , Toast.LENGTH_LONG).show();
            Log.d("TAG","监听到系统广播添加");
        }

        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            delectFile.RecursionDeleteFile(downloadFile);
            Toast.makeText(context , "监听到系统广播移除" , Toast.LENGTH_LONG).show();
            Log.d("TAG","监听到系统广播移除");
        }

        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            delectFile.RecursionDeleteFile(downloadFile);
         //   Toast.makeText(context , "监听到系统广播替换" , Toast.LENGTH_LONG).show();
            Log.d("TAG","监听到系统广播替换");
        }
    }

}
