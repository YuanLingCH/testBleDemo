package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.graphics.Color;
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
import fangzuzu.com.ding.bean.keyManagerBean;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;


/**
 * Created by yuanling on 2018/5/3.
 */

public class KeyManageAdapter extends RecyclerView.Adapter<KeyManageAdapter.keyViewHolder>  {


    private List<keyManagerBean.DataBeanX.DataBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    private KeyManageAdapter.OnItemLongClickListener mOnItemLongClickListener;
    public KeyManageAdapter(List<keyManagerBean.DataBeanX.DataBean> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        inflater=LayoutInflater.from(mContext);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position,String id,String userId);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @Override
    public keyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.key_manage_item,parent,false);
        keyViewHolder holder=new keyViewHolder(view);
    ;
        return holder;
    }

    @Override
    public void onBindViewHolder(final keyViewHolder holder, int position) {

        keyManagerBean.DataBeanX.DataBean dataBean = mDatas.get(position);
      String startTime = dataBean.getStartTime()+"";
        String endTime = dataBean.getEndTime()+"";
        String keyName = dataBean.getKeyName();
        String substringstartTime= startTime.substring(0, startTime.length() - 2);
        String substringendTime = endTime.substring(0, endTime.length() - 2);
        holder.name.setText(keyName);
        holder.startTime.setText(substringstartTime+"至"+substringendTime);
        long timeStampSec = System.currentTimeMillis()/1000;
        String timestamp = String.format("%010d", timeStampSec);


        if (!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(endTime)){
            String substring = startTime.substring(0,startTime.length()-2);
            Log.d("TAG","时间"+ substring);
            String s = unixTime.dateToStamp(substring);
            String substring1 = s.substring(0, s.length() - 3);
            int start = Integer.parseInt(substring1);
            Log.d("TAG","时间"+ startTime);


            String substringt = endTime.substring(0,endTime.length()-2);
            Log.d("TAG","时间"+ substring);
            String st = unixTime.dateToStamp(substringt);
            String substring1t = st.substring(0, s.length() - 3);
            int end = Integer.parseInt(substring1t);
            Log.d("TAG","结束时间撮"+ end);




            Log.d("TAG",""+timestamp);
            int current = Integer.parseInt(timestamp);
            Log.d("TAG","当前时间错"+current);
            if (start-current>0){
                holder.state.setText("未生效");
                holder.state.setTextColor(Color.parseColor("#778899"));
                ;
            }else if (start-current<0&&end-current>0){
                holder.state.setText("已生效");
                holder.state.setTextColor(Color.parseColor("#2E8B57"));



            }else if (end-current<0&&!startTime.equals(endTime)){

                holder.state.setText("已过期");
                holder.state.setTextColor(Color.parseColor("#FF0000"));
                holder.re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));
                holder.iv.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.icon_key_manage_unable));
            } else if (endTime.equals(startTime)){

                holder.state.setText( "永久");
            }else if (endTime.equals(startTime)){
                String substringStart = startTime.substring(0, startTime.length() - 5);
                String kaishi = startTime.substring(0, startTime.length() - 9);
                Log.d("TAG","kaishi"+kaishi);
                holder.state.setText(substringStart+"永久");

            }else if (endTime.equals("nu")||startTime.equals("nu")){
                holder.state.setText( "永久");
            }

        }

        final String id = dataBean.getId();
        final String userId = dataBean.getUserId();
        Log.d("TAG",""+timestamp);

        if(mOnItemLongClickListener != null){
           holder. itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position =holder. getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position, id,userId);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    class keyViewHolder extends RecyclerView.ViewHolder{
        TextView name,startTime,state;
        LinearLayout re_adapter;
        ImageView iv;
        public keyViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.name);
            startTime= (TextView) itemView.findViewById(R.id.date_start);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            iv= (ImageView) itemView.findViewById(R.id.iv);
            state= (TextView) itemView.findViewById(R.id.state);
        }
    }

}
