package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
//自定义抽象万能BaseAdapter
    //可传入任意数据源

public abstract class AbsBaseAdapter<T> extends BaseAdapter {
//数据源
    List<T> data;
    //LayoutInflater
    LayoutInflater inflater;
    //多个布局资源
    int[] layoutId;
    //构造方法，传入必要的参数
    public AbsBaseAdapter(Context context, List<T> data, int...layoutId){
        this.layoutId=layoutId;
        this.data=data;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return layoutId.length;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    //抽象绑定数据的方法
    public abstract void bindData(int position,ViewHolder holder);

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
          ViewHolder holder=null;
        //得到当前数据布局类型
        int layoutIndex=getItemViewType(i);
        if(view==null){
            view=inflater.inflate(layoutId[layoutIndex],viewGroup,false);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        //绑定数据
        bindData(i,holder);
        return view;
    }
    public static class ViewHolder{
        //保存的控件：是需要设置值的控件、
        private View view;
        public ViewHolder(View v){
            this.view=v;
        }
        public View findViewById(int viewId){
            //根据viewid,找到对应的控件
            return view.findViewById(viewId);
        }
    }
}
