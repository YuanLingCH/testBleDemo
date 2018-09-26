package fangzuzu.com.ding.adapter;

import android.content.Context;
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
import fangzuzu.com.ding.bean.permisonBean;

/**
 * Created by lingyuan on 2018/6/5.
 */

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.perViewHolder> {

    private List<permisonBean.DataBean> data;
    Context mcontent;

    LayoutInflater inflater;
    public PermissionAdapter(List<permisonBean.DataBean>  data, Context mcontent) {
        this.data = data;
        this.mcontent=mcontent;
        inflater=LayoutInflater.from(mcontent);
    }




    public interface OnItemClickListener{
        void onItemClick(String id,int postion,List<permisonBean.DataBean> data);
    }

    private OnItemClickListener mItemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
    @Override
    public PermissionAdapter.perViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.author_item_layout,parent,false);
        perViewHolder holder=new perViewHolder(view);
      //  view.setOnClickListener(this);
        return holder;
    }
    String id;
   ;
    @Override
    public void onBindViewHolder(final PermissionAdapter.perViewHolder holder, final int position) {

        permisonBean.DataBean dataBean = data.get(position);
        final String description = dataBean.getDescription();
         id = dataBean.getId();

        Log.d("TAG","adapter"+description);
        Log.d("TAG","adapter"+id );
        holder.itemView.setTag(id);

        if (description.equals("发送钥匙")){  // 钥匙管理
            Log.d("TAG","点击了我");
            holder.tvpermison.setText("钥匙授权");
            holder.iv_auth.setImageResource(R.drawable.auth_yaoshi_sel);


        }else if (description.equals("发送密码")){
            holder.tvpermison.setText("密码授权");
            holder.iv_auth.setImageResource(R.drawable.auth_senmima_sel);

        }/*else if (description.equals("钥匙管理")){
            holder.tvpermison.setText(description);
            holder.iv_auth.setImageResource(R.drawable.auth_yaosiguanli_sel);

        }else if (description.equals("密码管理")){
            holder.tvpermison.setText(description);
            holder.iv_auth.setImageResource(R.drawable.auth_mimaguanli_sel);

        }*/else if (description.equals("IC卡")){
            holder.tvpermison.setText(description);
            holder.iv_auth.setImageResource(R.drawable.auth_kapianguanli_sel);

        }else if (description.equals("指纹")){
            holder.tvpermison.setText(description);
            holder.iv_auth.setImageResource(R.drawable.auth_ziwen_sel);

        }else if (description.equals("操作记录")){
            holder.tvpermison.setText(description);
            holder.iv_auth.setImageResource(R.mipmap.jilu);

        }else if (description.equals("设置")){
            holder.tvpermison.setText(description);
            holder.iv_auth.setImageResource(R.mipmap.set);

        }

        if(data.get(position).isFlag()){
            holder.iv_auth.setSelected(true);
        }else {
            holder.iv_auth.setSelected(false);
        }


        if (mItemClickListener!=null){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mItemClickListener.onItemClick((String) v.getTag(),position,data);

                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class perViewHolder extends RecyclerView.ViewHolder{
        TextView tvpermison;
        ImageView iv_auth;
        LinearLayout ll_auth;
        public perViewHolder(View itemView) {
            super(itemView);
            tvpermison = (TextView ) itemView.findViewById(R.id.permis);
            iv_auth= (ImageView) itemView.findViewById(R.id.iv_auth);
            ll_auth= (LinearLayout) itemView.findViewById(R.id.ll_auth);
        }
    }

}
