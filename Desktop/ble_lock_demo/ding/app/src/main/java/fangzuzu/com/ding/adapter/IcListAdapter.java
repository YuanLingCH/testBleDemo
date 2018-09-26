package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import fangzuzu.com.ding.bean.Icbean;
import fangzuzu.com.ding.ui.activity.icLockItemMessageActvity;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;

/**
 * Created by lingyuan on 2018/7/5.
 */

public class IcListAdapter extends RecyclerView.Adapter<IcListAdapter.IcViewHolder> {

    private List<Icbean.DataBeanX.DataBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position,int unlockType);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }


    public IcListAdapter(List<Icbean.DataBeanX.DataBean> mDatas,Context mContext) {
        this.mDatas = mDatas;
        this.mDatas = mDatas;
        this.mContext=mContext;
        inflater=LayoutInflater.from(mContext);
    }

    @Override
    public IcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.ic_list_layout,parent,false);
        final IcViewHolder holder=new IcViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(IcViewHolder holder, final int position) {
        Icbean.DataBeanX.DataBean  bean= mDatas.get(position);
        final String unlockName = bean.getUnlockName();
        final String addPerson = bean.getAddPerson();
        final int addType = bean.getAddType();
       final int unlockType = bean.getUnlockType();

        final    String unlockFlag = bean.getUnlockFlag();
        Log.d("TAG","开锁类型"+unlockType);
        final String id = bean.getId();
        if (addType==0){
            holder.lock_elcet.setText("IC卡");
        }else if (addType==1){
            holder.lock_elcet.setText("身份证");
        }else if (addType==2){
            holder.lock_elcet.setText("指纹");
        }
        String Starttime =  bean.getStartTime();
        String endtime = bean.getEndTime();
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



            long timeStampSec = System.currentTimeMillis()/1000;
            String timestamp = String.format("%010d", timeStampSec);
            Log.d("TAG",""+timestamp);
            int current = Integer.parseInt(timestamp);
            Log.d("TAG",""+current);
            if (startTime-current>0){
                holder.lock_state.setText("未生效");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state.setBackgroundColor(Color.parseColor("#EC8325"));
             //   holder.lock_state.setTextColor(Color.parseColor("#6B6B6B"));
                holder.re_adapter.setEnabled(false);
                holder.re_adapter.setBackgroundColor(220);
                if (addType==0){
                    holder.iv.setImageResource(R.mipmap.ic_unable);

                }else if (addType==1){
                    holder.iv.setImageResource(R.mipmap.shengfz_unable);

                }
                else if (addType==2){
                    holder.iv.setImageResource(R.mipmap.zeiwen_unable);

                }

            }else if (startTime-current<0&&end-current>0){

                holder.lock_state.setText("限时");
                holder.lock_state_one.setTextColor(Color.GREEN);
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
             //   holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));
             //   holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);
                if (addType==0){
                    holder.iv.setImageResource(R.mipmap.ic_enable);
                }else if (addType==1){

                    holder.iv.setImageResource(R.mipmap.shengfz_enable);
                } else if (addType==2){
                    holder.iv.setImageResource(R.mipmap.ziwen);

                }


            }else if (end-current<0&&!Starttime.equals(endtime)){
                //  holder.iv.setImageResource(R.drawable.door_logo);
                holder.lock_state.setText("限时");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已过期");
                holder.lock_state_one.setTextColor(Color.RED);
             //   holder.lock_state.setTextColor(Color.parseColor("#EE0000"));
              //  holder.re_adapter.setEnabled(false);
             //   holder.lock_state.setBackgroundColor(Color.TRANSPARENT);
              //  holder.lock_state.setBackgroundColor(Color.parseColor("#31A14B"));
                if (addType==0){
                    holder.iv.setImageResource(R.mipmap.ic_unable);

                }else if (addType==1){
                    holder.iv.setImageResource(R.mipmap.shengfz_unable);

                }else if (addType==2){
                    holder.iv.setImageResource(R.mipmap.zeiwen_unable);

                }

                //  holder.iv.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.door_logo));


            }else if(Starttime.equals(endtime)){
              //  holder.lock_state.setText("已生效");
                holder.lock_state.setText("永久");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
                holder.lock_state_one.setTextColor(Color.GREEN);
              //  holder.lock_state.setTextColor(Color.TRANSPARENT);

              //  holder.lock_state.setBackgroundColor(Color.parseColor("#F14448"));
               // holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);
                if (addType==0){
                    holder.iv.setImageResource(R.mipmap.ic_enable);
                }else if (addType==1){
                    holder.iv.setImageResource(R.mipmap.shengfz_enable);
                }else if (addType==2){
                    holder.iv.setImageResource(R.mipmap.ziwen);

                }



            }else if(StringUtils.isEmpty(Starttime)){
                holder.lock_state.setText("永久");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
                holder.lock_state_one.setTextColor(Color.GREEN);
              //  holder.lock_state.setTextColor(Color.TRANSPARENT);
              //  holder.lock_state.setBackgroundColor(Color.parseColor("#F14448"));
              //  holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);
                if (addType==0){
                    holder.iv.setImageResource(R.mipmap.ic_enable);
                }else if (addType==1){
                    holder.iv.setImageResource(R.mipmap.shengfz_enable);
                }else if (addType==2){
                    holder.iv.setImageResource(R.mipmap.ziwen);

                }

            }else if (Starttime.equals(endtime)){
                holder.lock_state.setText("永久");
                holder.lock_state.setTextColor(Color.BLACK);
                holder.lock_state_one.setText("已生效");
                holder.lock_state_one.setTextColor(Color.GREEN);
              //  holder.lock_state.setTextColor(Color.TRANSPARENT);
              //  holder.lock_state.setBackgroundColor(Color.parseColor("#F14448"));
             //   holder.lock_state.setTextColor(Color.parseColor("#00ff00"));
                holder.re_adapter.setEnabled(true);
                if (addType==0){
                    holder.iv.setImageResource(R.mipmap.ic_enable);
                }else if (addType==1){
                    holder.iv.setImageResource(R.mipmap.shengfz_enable);
                }
            }else if (addType==2){
                holder.iv.setImageResource(R.mipmap.ziwen);

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


        String endTime = bean.getEndTime();
        String startTime = bean.getStartTime();
        if (endTime==null||startTime==null){
            holder.lock_state.setText("永久");
            holder.lock_state_one.setTextColor(Color.GREEN);
            holder.lock_state.setTextColor(Color.BLACK);
            holder.lock_state_one.setText("已生效");
            if (addType==0){
                holder.iv.setImageResource(R.mipmap.ic_enable);
            }else if (addType==1){
                holder.iv.setImageResource(R.mipmap.shengfz_enable);
            }
        }else {
            String substringStart = startTime.substring(0, startTime.length() - 5);
            String substringendTime  = endTime .substring(0, endTime .length() - 5);
            holder.lock_time.setText(substringStart+"至"+substringendTime);
            if (addType==0){
                holder.iv.setImageResource(R.mipmap.ic_enable);
            }else if (addType==1){
                holder.iv.setImageResource(R.mipmap.shengfz_enable);
            }
        }

        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    mOnItemLongClickListener.onItemLongClick(v,position,addType);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }

        holder.re_adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unlock=unlockType+"";
               Intent  intent=new Intent(mContext, icLockItemMessageActvity.class);
                    intent.putExtra("unlockName",unlockName);
                intent.putExtra("addPerson",addPerson);
                intent.putExtra("addType",unlock);
                intent.putExtra("id",id );
                intent.putExtra("unlockFlag",unlockFlag);
                intent.putExtra("addTypeFlag",addType+"");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class IcViewHolder extends RecyclerView.ViewHolder{
       TextView  lock_time,lock_state,lock_elcet,lock_state_one;
        LinearLayout re_adapter;
        ImageView iv;
        public IcViewHolder(View itemView) {
            super(itemView);
            lock_state= (TextView) itemView.findViewById(R.id.lock_state);
            lock_time= (TextView) itemView.findViewById(R.id.lock_time);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            lock_elcet= (TextView) itemView.findViewById(R.id.lock_elcet);
            iv= (ImageView) itemView.findViewById(R.id.iv);
            lock_state_one=itemView.findViewById(R.id.lock_state_one);
        }
    }
}
