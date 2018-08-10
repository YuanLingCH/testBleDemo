package fangzuzu.com.ding.widget;

/**
 * Created by pc01 on 2017/5/24.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.UIutils;


/**
 * 功能作用：本app版本改变后提示用户更新到最新版本
 * 时间：2017、02、21
 *
 */
public class VersionUpdateTipdialog extends BaseDialog {

    private TextView tvVersion;
    private TextView tvFileSize;
    private TextView tvUpdateContent;
    private Button btLater;
    private Button btUpdateNow;

    private String mVersionNeme,mFileSize,mUpdateContent,mUrl;

    private Activity acticity;


    /**
     * 版本更新窗口
     * @param context 上下文
     * @param versionName 版本号
     * @param mFileSize 下载url
     * @param mUpdateContent 更新内容
     * @param url 下载URL
     */
    public VersionUpdateTipdialog(Activity context, String versionName , String mFileSize , String mUpdateContent,String url) {
        super(context);
        acticity =  context;
        this.mVersionNeme = versionName;
        this.mFileSize = mFileSize;
        this.mUpdateContent = mUpdateContent;
        this.mUrl = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_version_update_tip);
        setWidthM_HeithW();
        this.setCanceledOnTouchOutside(false);
        initView();
        setInitContentShow();
        initListener();
    }

    /**
     * 初始化 View
     */
    private void initView(){
        tvVersion		 = (TextView) findViewById(R.id.tvVersion);
        tvFileSize		 = (TextView) findViewById(R.id.tvFileSize);
        tvUpdateContent	 = (TextView) findViewById(R.id.tvUpdateContent);
        btLater			 = (Button) findViewById(R.id.btLater);
        btUpdateNow		 = (Button) findViewById(R.id.btUpdateNow);
    }
    /**
     * 显示更新版本的内容
     */
    private void setInitContentShow(){
        String versionName = mVersionNeme ==null?"": mVersionNeme;
        tvVersion		 .setText(UIutils.getString(R.string.software_version)+ versionName);
        if (mFileSize!=null&&mFileSize.length()>10){
            mFileSize = mFileSize.substring(0,10);
        }
        tvFileSize		 .setText(UIutils.getString(R.string.file_size)+mFileSize);
        tvUpdateContent	 .setText(mUpdateContent);
    }
    /**
     * 初始化事件
     */
    private void initListener(){
        btLater			 .setOnClickListener(clickListener);
        btUpdateNow		 .setOnClickListener(clickListener);
    }

    /**
     * 控件点击事件处理
     */
    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //以后再说
                case R.id.btLater:
                   /* if (acticity instanceof SplashActivity){    //当前启动的activity 是否是SplashActivity     如果是  ,  直接进入主页
                       ((SplashActivity) acticity).startActivtyHome();
                    }*/
                    cancel();
                    break;
                //立即更新
                case R.id.btUpdateNow:
                    new VersionUpdatedialog(acticity,mUrl).show();
                    cancel();
                    break;
                default:
                    break;
            }
        }
    };

}
