package fangzuzu.com.ding.model;


import fangzuzu.com.ding.SharedUtils;
import fangzuzu.com.ding.bean.UserBean;

/**
 * Created by Administrator on 2016/12/1.
 */
public class UserModel implements IUserModel {
    @Override
    public String getName() {
        UserBean user = getUser();
        if (user == null) {
            return "";
        }
        return user.name;
    }

    @Override
    public String getPassword() {
        UserBean user = getUser();
        if (user == null) {
            return "";
        }
        return user.password;
    }

    @Override
    public UserBean getUser() {
        return SharedUtils.getUser();
    }

    @Override
    public void saveUser(UserBean user) {
        SharedUtils.saveUser(user);
    }
}
