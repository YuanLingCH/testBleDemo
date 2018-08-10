package fangzuzu.com.ding.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by lingyuan on 2018/6/3.
 */

public  class Dialog {
    ProgressDialog progressDialog;
    Context mcontent;

    public  Dialog(Context mcontent) {
        this.mcontent = mcontent;
    }

    public  void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(mcontent, title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
