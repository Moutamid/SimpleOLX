package com.moutamid.simpleolx.Admin.Adapter;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.moutamid.simpleolx.User.Fragments.AcceptedFragment;
import com.moutamid.simpleolx.User.Fragments.PendingFragment;
import com.moutamid.simpleolx.User.Fragments.RejectedFragment;


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
                com.moutamid.simpleolx.User.Fragments.PendingFragment pendingFragment = new PendingFragment();
                return pendingFragment;
            case 1:
                com.moutamid.simpleolx.User.Fragments.AcceptedFragment acceptedFragment = new AcceptedFragment();
                return acceptedFragment;
            case 2:
                RejectedFragment rejectedFragmeny = new RejectedFragment();
                return rejectedFragmeny;
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