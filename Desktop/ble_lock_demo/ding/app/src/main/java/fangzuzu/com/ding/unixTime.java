package fangzuzu.com.ding;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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









public static String NowString (long timeStamp){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    // 时间戳转换成时间
        return  df.format(new Date(timeStamp));
        }
    /*
 * 将时间戳转换为时间
 */

    public static String stampToTime(long stamp) {
        String time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Date date = new Date(stamp*1000);
        time = simpleDateFormat.format(date);
        return time;
    }


    /**
     * 获取指定网站的日期时间
     *
     * @param webUrl
     * @return
     */
    public static long getWebsiteDatetime(String webUrl){
        try {
            URL url = new URL(webUrl);// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            return ld;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        return 0;
    }

        }
