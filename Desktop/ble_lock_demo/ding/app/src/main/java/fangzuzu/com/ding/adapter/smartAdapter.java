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

public class smartAdapter extends RecyclerView.Adapter<smartAdapter.smartViewHolder> {
private List data;
    Context mcontent;
    LayoutInflater inflater;

    public smartAdapter(List  data, Context mcontent) {
        this.data = data;
        this.mcontent=mcontent;
        inflater=LayoutInflater.from(mcontent);
    }

    @Override
    public smartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.smart_item_layout,parent,false);
        smartViewHolder holder=new smartViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(smartViewHolder holder, int position) {
        Object o = data.get(position);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class smartViewHolder extends RecyclerView.ViewHolder{
        public smartViewHolder(View itemView) {
            super(itemView);
        }
    }


}
