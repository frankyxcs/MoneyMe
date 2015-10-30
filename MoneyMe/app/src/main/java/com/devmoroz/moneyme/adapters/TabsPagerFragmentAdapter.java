package com.devmoroz.moneyme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devmoroz.moneyme.fragments.ChartFragment;
import com.devmoroz.moneyme.fragments.GoalsFragment;
import com.devmoroz.moneyme.fragments.HistoryFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);

        tabs = new String[]{
          "История", "График", "Цели"
        };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HistoryFragment.getInstance();
            case 1:
                return ChartFragment.getInstance();
            case 2:
                return GoalsFragment.getInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}