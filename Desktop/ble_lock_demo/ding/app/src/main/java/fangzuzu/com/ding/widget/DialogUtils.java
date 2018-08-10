package fangzuzu.com.ding.widget;

import android.content.Context;
import android.view.View;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by lingyuan on 2018/6/2.
 */

public class DialogUtils {

    public static abstract class OnButtonClickListener{
        public abstract void onConfirmButtonClick();
        public void onCancelButtonClick(){

        }
    }

    /**
     * 只显示一个按钮调用这个方法
     * @param context
     * @param message
     * @param positiveText
     * @param onButtonClickListener
     */
    public static void showSingleButtonDialog(Context context, String message,String positiveText,OnButtonClickListener onButtonClickListener) {
        showCustomMessageDialog(context,message,null,positiveText,onButtonClickListener);
    }


    public static void showNormalDialog(Context context, String message, final OnButtonClickListener onButtonClickListener){
        showCustomMessageDialog(context,message,"取消","确定",onButtonClickListener);
    }

    public static void showCustomMessageDialog(Context context, String message,String negativeText,String positiveText, final OnButtonClickListener onButtonClickListener){
        showCustomMessageDialog(context,"提示",message,negativeText,positiveText,onButtonClickListener);
    }
    public static void showCustomMessageDialog(Context context, String title, String message, String negativeText, String positiveText, final OnButtonClickListener onButtonClickListener){
        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        onButtonClickListener.onConfirmButtonClick();
                    }
                })
                .setNegativeButton(negativeText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        onButtonClickListener.onCancelButtonClick();
                    }
                });

        mMaterialDialog.show();
    }
}
