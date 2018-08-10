package fangzuzu.com.ding.model;


import fangzuzu.com.ding.bean.UserBean;

/**
 * Created by Administrator on 2016/12/1.
 */
public interface IUserModel {
    public String getName();

    public String getPassword();

    public UserBean getUser();

    public void saveUser(UserBean user);
}
