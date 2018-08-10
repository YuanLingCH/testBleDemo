package fangzuzu.com.ding;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lingyuan on 2018/6/8.
 */

public class unixTime {


    /*
 * 将时间转换为时间戳
 */
    public  static String dateToStamp(String time)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return String.valueOf(ts);
    }


    /*
 * 将时间转换为时间戳
 */
    public  static String dateToStampone(String time)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
        date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
        e.printStackTrace();
        }
        long ts = date.getTime();
        return String.valueOf(ts);
        }

    /*
 * 将时间转换为时间戳
 */
    public  static String dateToStamptow(String time)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return String.valueOf(ts);
    }
//时间戳转字符串
public static String getStrTime(String timeStamp){
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        long  l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
        }








    /*
 * 将时间戳转换为时间
 */
public static String stampToDate(long timeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
        }
public static String NowString (){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

        return df.format(new Date());
        }
        }
