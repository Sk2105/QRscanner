package com.sgtech.qr_scanner.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class SlideAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;

    public SlideAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {

        super(fm);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
