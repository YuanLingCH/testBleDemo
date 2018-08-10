package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fangzuzu.com.ding.MainApplication;
import fangzuzu.com.ding.R;
import fangzuzu.com.ding.bean.permisonBean;

/**
 * Created by lingyuan on 2018/6/5.
 */

public class PermissionLockhomeAdapter extends RecyclerView.Adapter<PermissionLockhomeAdapter.perViewHolder>implements View.OnClickListener {
    private List<permisonBean.DataBean> data;
    Context mcontent;
    LayoutInflater inflater;
    public PermissionLockhomeAdapter(List<permisonBean.DataBean>  data, Context mcontent) {
        this.data = data;
        this.mcontent=mcontent;
        inflater=LayoutInflater.from(mcontent);
    }



    public interface OnItemClickListener{
        void onItemClick(String id);
    }

    private OnItemClickListener mItemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
    @Override
    public PermissionLockhomeAdapter.perViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.permiss_item_laayout,parent,false);
        perViewHolder holder=new perViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }
    String id;
    @Override
    public void onBindViewHolder(PermissionLockhomeAdapter.perViewHolder holder, int position) {

        permisonBean.DataBean dataBean = data.get(position);
        String description = dataBean.getDescription();
         id = dataBean.getId();
        Log.d("TAG","adapter"+description);
        Log.d("TAG","adapter"+id );

        holder.itemView.setTag(description);
        if (description.equals("发送钥匙")){
            holder.tvpermison.setText(description);
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.yaosi);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }else if (description.equals("发送密码")){
            holder.tvpermison.setText(description);
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.fasongmima);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }else if (description.equals("钥匙管理")){
            holder.tvpermison.setText(description);
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.yaosiguanli);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }else if (description.equals("密码管理")){
            holder.tvpermison.setText(description);
            holder.tvpermison.setPadding(0,10,0,0);
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.mima);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }else if (description.equals("IC卡")){
            holder.tvpermison.setText("卡片管理");
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.ic);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }else if (description.equals("指纹")){
            holder.tvpermison.setText(description);
            holder.tvpermison.setPadding(50,0,0,0);
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.ziwen);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }else if (description.equals("操作记录")){
            holder.tvpermison.setText(description);
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.jilu);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }else if (description.equals("设置")){
            holder.tvpermison.setText(description);
            holder.tvpermison.setPadding(50,0,0,0);
            Drawable top = MainApplication.getInstence().getResources().getDrawable(R.mipmap.set);// 获取res下的图片drawable
            top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());// 一定要设置setBounds();
// 调用setCompoundDrawables(Drawable left, Drawable top,Drawable right, Drawable bottom)方法。(有四个参数，不需要设置的参数传null)
            holder.tvpermison.setCompoundDrawables(null, top, null, null);

        }


    }
    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick((String) v.getTag());
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    public class perViewHolder extends RecyclerView.ViewHolder{
TextView tvpermison;
        public perViewHolder(View itemView) {
            super(itemView);
            tvpermison = (TextView ) itemView.findViewById(R.id.permis);
        }
    }

}
