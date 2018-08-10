package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.bean.passwordManagerBean;
import fangzuzu.com.ding.unixTime;
import fangzuzu.com.ding.utils.StringUtils;

/**
 * Created by lingyuan on 2018/7/5.
 */

public class passwordManagerListAdapter extends RecyclerView.Adapter{



    private List<passwordManagerBean.DataBeanX.DataBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    private OnItemLongClickListener mOnItemLongClickListener;
    private int normalType = 0;     // 第一种ViewType，正常的item
    private int footType = 1;       // 第二种ViewType，底部的提示View

    private boolean hasMore = true;   // 变量，是否有更多数据
    private boolean fadeTips = false; // 变量，是否隐藏了底部的提示
    public passwordManagerListAdapter(List mDatas, Context mContext, boolean hasMore) {
        this.mDatas = mDatas;
        this.mContext=mContext;
        inflater=LayoutInflater.from(mContext);
        this.hasMore = hasMore;

    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position,String id,String unlcokflag,String unlockType);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 根据返回的ViewType，绑定不同的布局文件，这里只有两种
        if (viewType == normalType) {
            return new IcViewHolder(inflater.inflate(R.layout.pasw_manager_layout,parent,false));
        } else {
            return new FootHolder(inflater.inflate(R.layout.add_more_layout,parent,false));
        }

    }

    // // 底部footView的ViewHolder，用以缓存findView操作
    class FootHolder extends RecyclerView.ViewHolder {
            TextView add_more_tv;
        public FootHolder(View itemView) {
            super(itemView);
            add_more_tv= (TextView) itemView.findViewById(R.id.add_more_tv);

        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof IcViewHolder) {
            ((IcViewHolder) holder).bind(mDatas, position);
        }else {
            // 之所以要设置可见，是因为我在没有更多数据时会隐藏了这个footView
            ((FootHolder) holder).add_more_tv.setVisibility(View.VISIBLE);
            // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
            if (hasMore == true){
                // 不隐藏footView提示
                fadeTips = false;
                if (mDatas.size()>0){
                // 如果查询数据发现增加之后，就显示正在加载更多
                    ((FootHolder) holder).add_more_tv.setText("正在加载更多...");
                    ((FootHolder) holder).add_more_tv.setGravity(Gravity.CENTER);

                }
            }else {
                if (mDatas.size() > 0) {
                    // 如果查询数据发现并没有增加时，就显示没有更多数据了
                    ((FootHolder) holder).add_more_tv.setText("没有更多数据了");
                    ((FootHolder) holder).add_more_tv.setGravity(Gravity.CENTER);
                    // 隐藏提示条
                    ((FootHolder) holder).add_more_tv.setVisibility(View.GONE);
                    // 将fadeTips设置true
                    fadeTips = true;
                    // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                    hasMore = true;
                }
                }
        }

         }
    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }
    @Override
    public int getItemCount() {
        return mDatas.size()+1;//recylerview的item的总数目是所有数据数量加一
    }
    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return mDatas.size();
    }
    public class IcViewHolder extends RecyclerView.ViewHolder{

        TextView lock_name,lock_time,lock_state;
        LinearLayout re_adapter;
        ImageView iv;
        public IcViewHolder(View itemView) {
            super(itemView);
            lock_name= (TextView) itemView.findViewById(R.id.lock_name);
            lock_time= (TextView) itemView.findViewById(R.id.lock_time);
            lock_state =(TextView) itemView.findViewById(R.id.lock_state);
            re_adapter= (LinearLayout) itemView.findViewById(R.id.re_adapter);
            iv= (ImageView) itemView.findViewById(R.id.iv);
        }
        public void bind(List<passwordManagerBean.DataBeanX.DataBean> demo, int position) {

            passwordManagerBean.DataBeanX.DataBean dataBean = mDatas.get(position);
            final String UnlockFlag = dataBean.getUnlockFlag();
           lock_name.setText(UnlockFlag);
           final String id = dataBean.getId();

            String Starttime =  dataBean.getStartTime();
            String endtime =  dataBean.getEndTime();
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
                Log.d("TAG","结束时间撮"+ end);



                long timeStampSec = System.currentTimeMillis()/1000;
                String timestamp = String.format("%010d", timeStampSec);
                Log.d("TAG",""+timestamp);
                int current = Integer.parseInt(timestamp);
                Log.d("TAG","当前时间错"+current);
                if (startTime-current>0){
                  lock_state.setText("未生效");
                    iv.setImageResource(R.mipmap.mima);
                   lock_state.setTextColor(Color.parseColor("#778899"));

                }else if (startTime-current<0&&end-current>0){
                   lock_state.setText("已生效");
                    iv.setImageResource(R.mipmap.mima);
                  lock_state.setTextColor(Color.parseColor("#2E8B57"));



                }else if (endtime.equals(Starttime)){
                   lock_state.setText("已生效");
                    iv.setImageResource(R.mipmap.mima);
                    lock_state.setTextColor(Color.parseColor("#2E8B57"));

                }else if (end-current<0){

                    lock_state.setText("已过期");
                   lock_state.setTextColor(Color.parseColor("#FF0000"));

                    iv.setImageResource(R.mipmap.icon_mima_unable);
                    re_adapter.setBackgroundColor(Color.parseColor("#E5E5E5"));

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
            String endTime = dataBean.getEndTime();
            String startTime =  dataBean.getStartTime();
            final int unlockType = dataBean.getUnlockType();
            String str=null;
            if (unlockType==0){
                str="限时";
            }else if(unlockType==2){
                str="自定义";
            }else if(unlockType==3){
                str="循环";
            }else if(unlockType==4){
                str="清空";
            }else if(unlockType==1){
                str="永久";
            }

            if (!StringUtils.isEmpty(startTime)&&!StringUtils.isEmpty(endTime)){


                if (endTime.equals(startTime)){
                    String substringStart = startTime.substring(0, startTime.length() - 5);
                  lock_time.setText(substringStart+  str);
                }else {
                    String substringStart = startTime.substring(0, startTime.length() - 5);
                    String substringendTime  = endTime .substring(0, endTime .length() - 5);
                   lock_time.setText(substringStart+"至"+substringendTime+str);
                }

            }
            if(mOnItemLongClickListener != null){
               itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getLayoutPosition();
                        mOnItemLongClickListener.onItemLongClick(itemView,position, id,UnlockFlag,unlockType+"");
                        //返回true 表示消耗了事件 事件不会继续传递
                        return true;
                    }
                });
            }

        }
    }
    // 暴露接口，改变fadeTips的方法
    public boolean isFadeTips() {
        return fadeTips;
    }

    // 暴露接口，下拉刷新时，通过暴露方法将数据源置为空
    public void resetDatas() {
        mDatas = new ArrayList<>();
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List  newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            mDatas.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

}
