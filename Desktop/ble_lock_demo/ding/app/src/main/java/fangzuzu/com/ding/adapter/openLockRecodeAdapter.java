package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.bean.openLockRecoderBean;


/**
 * Created by lingyuan on 2018/6/20.
 */

public class openLockRecodeAdapter extends RecyclerView.Adapter<openLockRecodeAdapter.keyViewHolder> {

    private List<openLockRecoderBean.DataBeanX.DataBean> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public openLockRecodeAdapter(List<openLockRecoderBean.DataBeanX.DataBean> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        inflater=LayoutInflater.from(mContext);
    }


    @Override
    public openLockRecodeAdapter.keyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.open_lock_item,parent,false);
        keyViewHolder holder=new keyViewHolder(view);
        return holder;
    }

  //  开锁类型0：密码开锁，1：蓝牙开锁，2：指纹开锁，3：IC卡开锁，4：身份证开锁


    @Override
    public void onBindViewHolder(openLockRecodeAdapter.keyViewHolder holder, int position) {
        openLockRecoderBean.DataBeanX.DataBean dataBean = mDatas.get(position);
        String unlockTime = dataBean.getUnlockTime();
        int unlockType = dataBean.getUnlockType();
        String substring1 = unlockTime.substring(0, unlockTime.length() - 2);
        holder.lock_open_time.setText(substring1);
        holder.open_user.setText(dataBean.getUserId());
        if (unlockType==1){
            holder.open_type.setText("APP开锁");
            holder.iv_open_lock.setImageResource(R.mipmap.portrait);
        }else if (unlockType==0){
            holder.open_type.setText("密码开锁");
            holder.iv_open_lock.setImageResource(R.mipmap.mima);
        }else if (unlockType==2){
            holder.open_type.setText("指纹开锁");
            holder.iv_open_lock.setImageResource(R.mipmap.ziwen);
        }else if (unlockType==3){
            holder.open_type.setText("卡片开锁");
            holder.iv_open_lock.setImageResource(R.mipmap.ic_enable);
        }/*else if (unlockType==4){
            holder.open_type.setText("身份证开锁");
            holder.iv_open_lock.setImageResource(R.mipmap.shengfz_enable);
        }*/

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
    class keyViewHolder extends RecyclerView.ViewHolder{
TextView lock_open_time,open_type,open_user;
        ImageView iv_open_lock;

        public keyViewHolder(View itemView) {
            super(itemView);
            lock_open_time= (TextView) itemView.findViewById(R.id.lock_open_time);
            open_type= (TextView) itemView.findViewById(R.id.open_type);
            iv_open_lock= (ImageView) itemView.findViewById(R.id.iv_open_lock);
            open_user=(TextView) itemView.findViewById(R.id.open_user);
        }
    }
}
