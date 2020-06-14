package com.swufe.sakura;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyPageAdapter extends FragmentPagerAdapter  {
    List<Fragment> fragmentList;
   private String[] title = new String[]{"国内疫情","国外疫情","疫情地图"};
    public MyPageAdapter(FragmentManager manager){
        super(manager);
        this.fragmentList= fragmentList;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {//到底显示哪一个
        if(position==0){
            return new FirstFragment();
        }else if(position==1){
            return new SencondFragment();

        }else{
            return new ThirdFragment();
        }

    }
    @Override
    public CharSequence getPageTitle(int position) {
       return title[position];

    }

    @Override
    public int getCount() {
        return 3;//返回多少个Fra
    }
}
