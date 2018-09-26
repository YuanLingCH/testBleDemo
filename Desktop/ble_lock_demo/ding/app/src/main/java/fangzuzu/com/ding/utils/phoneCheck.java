package fangzuzu.com.ding.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电话号码检查
 * Created by lingyuan on 2018/7/16.
 */

public class phoneCheck {
    /**
     * 判断是否是电话号码
     * @param str
     * @return
     */
    public static boolean isChinaPhoneLegal(String str){
    String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";

        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
