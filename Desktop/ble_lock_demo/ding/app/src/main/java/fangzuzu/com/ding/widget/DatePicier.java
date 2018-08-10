package fangzuzu.com.ding.widget;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fangzuzu.com.ding.event.createtimeMessage;
import fangzuzu.com.ding.event.losetimeMessage;

/**
 * Created by yuanling on 2018/4/25.
 */

public class DatePicier {

    private static CustomDatePicker customDatePicker1, customDatePicker2;
    public DatePicier() {
    }

    public static CustomDatePicker getCustomDatePicker1() {
        return customDatePicker1;
    }

    public static CustomDatePicker getCustomDatePicker2() {
        return customDatePicker2;
    }

    public static void initDatePicker(final TextView currentDate , final TextView  currentTime, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
      // currentDate.setText(now.split(" ")[0]);
        currentDate.setText(now);
        currentTime.setText(now);


     /*   customDatePicker1 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentDate.setText(time.split(" ")[0]);
            }
        }, "2017-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动*//**//*
*/




        customDatePicker2 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentTime.setText(time);
                createtimeMessage message=new createtimeMessage();
                message.setTime(time);
                EventBus.getDefault().post(message);


            }
        }, now,"2025-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动



        customDatePicker1 = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间

                currentDate.setText(time);
                Log.d("TAG","时间"+time);


                losetimeMessage message=new losetimeMessage();
                message.setTime(time);
                EventBus.getDefault().post(message);


            }
        }, now,"2025-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 显示时和分
        customDatePicker1.setIsLoop(true); // 允许循环滚动
    }

}
