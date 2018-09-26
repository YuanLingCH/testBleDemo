package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.bean.userLockBean;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;

/**
 * Created by lingyuan on 2018/6/27.
 */

public class lockListSmartAdapter extends RecyclerView.Adapter<lockListSmartAdapter.lockViewHolder> {

    private List<userLockBean.DataBean.UserLockBean > mDatas;
    private Context mContext;
    private LayoutInflater inflater;


    public lockListSmartAdapter(List<userLockBean.DataBean.UserLockBean> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext=mContext;
        inflater=LayoutInflater.from(mContext);
    }


    @Override
    public lockListSmartAdapter.lockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.lock_item_smart_layout,parent,false);
        final lockViewHolder holder=new lockViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(lockListSmartAdapter.lockViewHolder holder, int position) {

        userLockBean.DataBean.UserLockBean userLockBean = mDatas.get(position);
        String id = userLockBean.getId();
        holder.lock_name.setText(userLockBean.getLockName());




        String Starttime = (String) userLockBean.getStartTime();
        String endtime = (String) userLockBean.getEndTime();
        Log.d("TAG","开始时间"+Starttime);
        Log.d("TAG","结束时间"+endtime);
        if (!StringUtils.isEmpty(Starttime)&&!StringUtils.isEmpty(endtime)){
                 String substring = Starttime.substring(0,Starttime.length()-2);
               Log.d("TAG","时间"+ substring);
            String s = unixTime.dateToStamp(substring);
            String substring1 = s.substring(0, s.length() - 3);
            int startTime = Integer.parseInt(substring1);
            Log.d("TAG","时间"+ startTime);


            String substringt = endtime.substring(0,endtime.length()-2);
            Log.d("TAG","时间"+ substring);
            String st = unixTime.dateToStamp(substringt);
            String substring1t = st.substring(0, s.length() - 3);
            int end = Integer.parseInt(substring1t);
            Log.d("TAG","时间"+ end);

            final int[] current = {0};
            new Thread(){
                @Override
                public void run() {
                    super.run();

                    String websiteDatetime = unixTime.getWebsiteDatetime("http://www.baidu.com")+"";
                    String substring = websiteDatetime.substring(0, websiteDatetime.length() - 3);
                    current[0] = Integer.parseInt(substring);
                    Log.d("TAG","北京时间撮"+substring);
                }
            }.start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

         /*   long timeStampSec = System.currentTimeMillis()/1000;
            String timestamp = String.format("%010d", timeStampSec);
            Log.d("TAG",""+timestamp);
            int current = Integer.parseInt(timestamp);*/
            Log.d("TAG",""+ current[0]);
            if (startTime- current[0] >0){
                holder.lock_state.setText("未生效");
                holder.lock_state.setTextColor(Color.WHITE);
                holder.lock_state.setBackgroundResource(R.color.weishengxiao);
                holder.re_adapter.setEnabled(false);
                holder.re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));
                holder.iv.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.lock_uneable));
            }else if (startTime- current[0] <0&&end- current[0] >0&&!Starttime.equals(endtime)){
                holder.lock_state.setText("已生效");
                holder.lock_state.setTextColor(Color.WHITE);
                holder.lock_state.setBackgroundResource(R.color.yishengxiao);
                holder.re_adapter.setEnabled(true);

            }else if (end- current[0] <0&&!Starttime.equals(endtime)){
              //  holder.iv.setImageResource(R.drawable.door_logo);
                holder.lock_state.setText("已过期");
                holder.lock_state.setTextColor(Color.WHITE);
                holder.re_adapter.setEnabled(false);
                holder.lock_state.setBackgroundResource(R.color.yiguoqi);
                holder.re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));
                holder.iv.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.lock_uneable));
              //  holder.iv.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.door_logo));


            }else if(Starttime.equals(endtime)){
                holder.lock_state.setText("已生效");
                holder.lock_state.setBackgroundResource(R.color.yishengxiao);
                holder.lock_state.setTextColor(Color.WHITE);
                holder.re_adapter.setEnabled(true);


            }else if(StringUtils.isEmpty(Starttime)){
                holder.lock_state.setText("已生效");
                holder.lock_state.setBackgroundResource(R.color.yishengxiao);
                holder.lock_state.setTextColor(Color.WHITE);
                holder.re_adapter.setEnabled(true);


            }

        }
        if (!StringUtils.isEmpty(endtime)){
            String substring = endtime.substring(0,endtime.length()-5);
            Log.d("TAG","结束时间"+ substring);
        }

        long timeStampSec = System.currentTimeMillis()/1000;
        String timestamp = String.format("%010d", timeStampSec);
        Log.d("TAG",""+timestamp);
        int i = Integer.parseInt(timestamp);
        Log.d("TAG",""+i);
        //  String s = unixTime.dateToStamp(Starttime);


        //   Log.d("TAG","开始时间"+s  );
        int electricity = Integer.parseInt(userLockBean.getElectricity());
        if (electricity>80){
            Drawable drawableLeft =mContext.getResources().getDrawable(
                    R.mipmap.power);
            holder.lock_elect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            holder.lock_elect.setCompoundDrawablePadding(5);
        }else if (electricity>20){
            Drawable drawableLeft =mContext.getResources().getDrawable(
                    R.mipmap.power_middle);
            holder.lock_elect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            holder.lock_elect.setCompoundDrawablePadding(5);
        }else if (electricity<20){
            Drawable drawableLeft =mContext.getResources().getDrawable(
                    R.mipmap.power_low);
            holder.lock_elect.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            holder.lock_elect.setCompoundDrawablePadding(5);
        }
        holder.lock_elect.setText(userLockBean.getElectricity()+"%");

        String endTime = (String) userLockBean.getEndTime();
        String startTime = (String) userLockBean.getStartTime();
        if (endTime==null||startTime==null){
            holder.lock_time.setText("永久");
            holder.lock_state.setText("已生效");
            holder.lock_state.setBackgroundResource(R.color.yishengxiao);
        }else if (endTime.equals(startTime)){
            String substringStart = startTime.substring(0, startTime.length() - 5);
            String kaishi = startTime.substring(0, startTime.length() - 9);
            Log.d("TAG","kaishi"+kaishi);
            holder.lock_time.setText(substringStart+"永久");

        }else {
            String substringStart = startTime.substring(0, startTime.length() - 5);
            String substringendTime  = endTime .substring(0, endTime .length() - 5);
            holder.lock_time.setText(substringStart+"至"+substringendTime);
        }


    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    class lockViewHolder extends RecyclerView.ViewHolder{
            TextView lock_name,lock_elect,lock_time,lock_state;
        LinearLayout re_adapter;
        ImageView iv;
        public lockViewHolder(View itemView) {
            super(itemView);
            lock_name= (TextView) itemView.findViewById(R.id.lock_name);
            lock_elect= (TextView) itemView.findViewById(R.id.lock_elcet);
            lock_time= (TextView) itemView.findViewById(R.id.lock_time);
            lock_state= (TextView) itemView.findViewById(R.id.lock_state_smart);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            iv= (ImageView) itemView.findViewById(R.id.iv);
        }
    }

}
