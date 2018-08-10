package fangzuzu.com.ding.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 
 * 引导页的FragmentPagerAdapter
 * 
 *
 *
 * @date : 16/8/8.
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter
{

    private List<Fragment> fragmentList;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> list)
    {
        super(fm);
        if (list == null)
        {
            throw new NullPointerException("List can not be null !");
        }
        fragmentList = list;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }


}
