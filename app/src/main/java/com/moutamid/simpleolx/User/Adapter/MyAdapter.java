package com.moutamid.simpleolx.User.Adapter;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.moutamid.simpleolx.Admin.Fragments.AcceptedFragment;
import com.moutamid.simpleolx.Admin.Fragments.PendingFragment;


public class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PendingFragment pendingFragment = new PendingFragment();
                return pendingFragment;
            case 1:
                AcceptedFragment acceptedFragment = new AcceptedFragment();
                return acceptedFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}