package fangzuzu.com.ding.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Liu Jianping
 *
 * @date : 16/8/10.
 */
public abstract class BaseFragment extends Fragment {

    protected View root;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root==null){
            root = inflater.inflate(getLayoutId(), null);
            initViews();

            initEvents();
        }

        ViewGroup parent = (ViewGroup) root.getParent();
        if (parent != null) {
            parent.removeView(root);
        }


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    /**
     * 获取布局的id
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化视图
     */
    protected abstract void initViews();

    /**
     * 初始化视频的监听事件
     */
    protected abstract void initEvents();

    /**
     * 初始化数据
     */
    protected abstract void initData();


}
