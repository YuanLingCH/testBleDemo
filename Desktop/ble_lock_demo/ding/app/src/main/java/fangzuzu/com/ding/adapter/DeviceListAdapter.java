package fangzuzu.com.ding.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fangzuzu.com.ding.R;
import fangzuzu.com.ding.bean.bleBean;

/**
 * Description：
 * Author: Hansion
 * Time: 2017/2/13 11:23
 */
public class DeviceListAdapter extends BaseAdapter {

 //   private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
 private List bluetoothDevices;
    private Context mContext;


    public DeviceListAdapter(Context context,List bluetoothDevices) {
        mContext = context;
        this.bluetoothDevices = bluetoothDevices;

    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        BaseViewHolder holder = BaseViewHolder.
                getViewHolder(mContext, convertView, viewGroup, R.layout.item_device_list, position);
        TextView name = holder.getView(R.id.mDeviceName);
        TextView address = holder.getView(R.id.mDeviceMacAddress);
        ImageView iv=holder.getView(R.id.iv_devices);
        bleBean bleBean = (bleBean) bluetoothDevices.get(position);
        String mstrbiaozhi = bleBean.getBiaozhi();
        ImageView iv_dev=holder.getView(R.id.iv_device);
        TextView tv_state=holder.getView(R.id.tv_state);
        if (mstrbiaozhi.equals("00")){
            iv.setVisibility(View.GONE);
            tv_state.setText("(待激活)");
          iv_dev.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.lock_uneable));
        }else if (mstrbiaozhi.equals("01")){
            iv.setVisibility(View.VISIBLE);
            tv_state.setText("(待激活)");
            iv_dev.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.lock_uneable));
        }else if (mstrbiaozhi.equals("02")){
            iv.setVisibility(View.GONE);
            tv_state.setText("(管理员已添加)");
            iv_dev.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.list_lock));
        }

        name.setText(bleBean.getName());
        address.setText(bleBean.getMac());
        return holder.getConvertView();
    }
}
