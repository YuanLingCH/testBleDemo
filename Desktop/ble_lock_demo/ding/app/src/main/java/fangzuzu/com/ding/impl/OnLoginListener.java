package fangzuzu.com.ding.impl;


import fangzuzu.com.ding.bean.UserBean;

/**
 * Created by Administrator on 2016/12/1.
 */
public interface OnLoginListener {
    public void loginSuccess(UserBean user);
    public void loginFaild();
}
