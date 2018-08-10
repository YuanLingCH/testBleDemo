package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fangzuzu.com.ding.R;

/**
 * Created by lingyuan on 2018/5/21.
 */

public class addSmartAdapter extends RecyclerView.Adapter<addSmartAdapter.addViewHolder> {
    private List data;
    Context mcontent;
    LayoutInflater inflater;

    public addSmartAdapter(List  data, Context mcontent) {
        this.data = data;
        this.mcontent=mcontent;
        inflater=LayoutInflater.from(mcontent);
    }
    @Override
    public addViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.add_smart_item_layout,parent,false);
        addViewHolder holder=new addViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(addViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class addViewHolder extends RecyclerView.ViewHolder{

        public addViewHolder(View itemView) {
            super(itemView);
        }
    }

}
