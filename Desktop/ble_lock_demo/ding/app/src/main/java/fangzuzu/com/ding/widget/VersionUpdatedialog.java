package fangzuzu.com.ding.widget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fangzuzu.com.ding.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;

;


/**
 * Created by pc01 on 2017/5/24.
 * 功能作用：实时展示当前应用的更新进度
 */

public class VersionUpdatedialog extends BaseDialog{
    private TextView tvDownloadProgress;
    private TextView tvDownloadSpeed;
    private ProgressBar pbProgress;
    private Button btCancelUpdate;
//    private DownloadApk mDownloadApk;

    /**上一次的下载流大小*/
    private long lastcurrent;
    /**上一次的下载时间*/
    private long lastTime;

    private String updateUrl;
    private Activity activity;

    public VersionUpdatedialog(Activity context,String updateurl) {
        super(context);
        activity =  context;
        updateUrl =updateurl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_version_update);
        setWidthM_HeithW();
        this.setCanceledOnTouchOutside(false);
        initView();
        initListener();
        setInitContentShow();
//        downloadApk("");;
        Log.d("TAG","下载链接"+ updateUrl);
        downloadApk(updateUrl);//http://120.77.42.11:8080/txxc.apk
    }

    /**
     * 初始化 View
     */
    private void initView(){
        tvDownloadProgress 	= (TextView) findViewById(R.id.tvDownloadProgress);
        tvDownloadSpeed 	= (TextView) findViewById(R.id.tvDownloadSpeed);
        pbProgress 			= (ProgressBar) findViewById(R.id.pbProgressBar);
        btCancelUpdate 		= (Button) findViewById(R.id.btCancelUpdate);
//        mDownloadApk		= new DownloadApk();
    }
    /**
     * 显示更新版本的内容
     */
    private void setInitContentShow(){
        pbProgress.setProgress(0);
        tvDownloadSpeed.setText("0kB/s");
        tvDownloadProgress.setText("0%");
    }

    /**
     * 初始化事件
     */
    private void initListener(){
        btCancelUpdate.setOnClickListener(clickListener);
    }

    /**
     * 控件点击事件处理
     */
    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //取消更新
                case R.id.btCancelUpdate:
                   /* if (activity instanceof SplashActivity){    //当前启动的activity 是否是SplashActivity     如果是  ,  直接进入主页
                        ((SplashActivity) activity).startActivtyHome();
                    }*/
                    cancel();
                    break;

                default:
                    break;
            }
        }
    };
    /*
     * 版本正在升级
     */
    private void downloadApk(String apkPath) {
        Log.d("TAG","下载链接"+ updateUrl);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {      //判断是否内存卡挂载

//            String installPath = getContext().getExternalCacheDir()  + "txxc.apk";
////            String installPath = Environment.getExternalStorageDirectory()
////                    .getAbsolutePath() + File.separator + "txxc.apk";
//            LogUtils.d("installPath--->"+installPath);
//            final File file = new File(installPath);
//            if(file.exists()){
//                file.delete();
//            }
            String aFileName = new File(apkPath).getName();
            download(apkPath,aFileName);

        }else{
            Log.d("TAG","没有SD卡访问权限，无法下载apk文件");
            //没有SD卡访问权限
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.please_make_sure_open_the_sd_card_access), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param url 下载连接
     * @param saveDir 储存下载文件的SDCard目录
     */
    public void download(final String url, final String saveDir) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG","下载失败！y异常原因"+e.getMessage());
                Log.d("TAG","下载失败！y异常原因"+e.getLocalizedMessage());

               /* if (activity instanceof SplashActivity){    //当前启动的activity 是否是SplashActivity     如果是  ,  直接进入主页
                    ((SplashActivity) activity).startActivtyHome();
                }*/
                cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                BufferedSource source=null;
                byte[] buf = new byte[10*1024];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                BufferedOutputStream out=null;
                try {
//                    is =  response.body().byteStream();
                    source = response.body().source();

                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    out=new BufferedOutputStream(fos);
                 /*   FileOutputStream fileOutputStream = activity.openFileOutput(saveDir, ((Context) activity).MODE_PRIVATE);
                    File cacheDir = activity.getCacheDir();*/

                    long sum = 0;
                    while ((len = source.read(buf)) != -1) {
                        out.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中

                        final int index = len;
                        final int iddex1 = progress;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (index >0){

                                    setSpeedShow((long)index,iddex1);
                                }
                            }
                        });
                    }
                    out.flush();
                    Log.d("TAG","下载中完成！");
                    // 下载完成
                    Looper.prepare();
                    installAPK(file);//安装
                    Looper.loop();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG","下载失败！"+e.getMessage());
                    Log.d("TAG","下载失败！"+e.getLocalizedMessage());
                } finally {
                    try {
                        if (source != null)
                            source.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (out != null)
                            out.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }



    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        String installPath=Environment.getExternalStorageDirectory()+"/"+"tianxiaxiaochi";
        File downloadFile = new File(installPath, saveDir);
        Log.d("TAG","下载路径"+downloadFile.getAbsolutePath());
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }

        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    /**
     * 设置当前下载速度显示
     * @param current
     * @param  index 下载进度
     */
    private void setSpeedShow(long current,int index){
        tvDownloadSpeed.setText((current / 10)+"k/s");
        tvDownloadProgress.setText(index+ " %");
        pbProgress.setProgress(index);   //设置下载进度
    }
    /**
     * 安装下载下来的最新APK
     */
    private void installAPK(File file) {
        Log.d("TAG","升级路径"+Uri.fromFile(file));
        //如果当前版本小于6.0,则用普通方式安装
        if(Build.VERSION.SDK_INT < 23){
            // 通过隐式意图去开启activity
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             activity.	startActivity(intent);
        }else{
            openFile(file);
        }
    }
    /**
     * 当前系统版本高于6.0时，用此方法安装apk
     * @param file
     */
    private void openFile(File file) {
        Log.d("TAG","当前系统版本高于6.0时，用此方法安装apk");
    Intent intent = new Intent();
        intent.addFlags(268435456);         //启动方式
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));
        try {
            getContext().startActivity(intent);
        } catch (Exception e) {
            Log.d("TAG",e.toString());
            //当前没有找到打开apk的运用
            Toast.makeText(getContext(), getContext().getString(R.string.program_to_open_such_files_not_found), Toast.LENGTH_SHORT).show();
        }

        if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(activity, "fangzuzu.com.ding.fileprovider", file);//在AndroidManifest中的android:authorities值
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            activity. startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity. startActivity(install);
        }


    }

    /**
     * 取得打开当前apk的运用方式
     * @param var0
     * @return
     */
    private String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        Log.d("TAG","文件夹名称"+var3);
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

}
