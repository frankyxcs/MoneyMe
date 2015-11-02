package com.devmoroz.moneyme.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devmoroz.moneyme.fragments.ChartFragment;
import com.devmoroz.moneyme.fragments.GoalsFragment;
import com.devmoroz.moneyme.fragments.HistoryFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    public static final int TAB_HISTORY = 0;
    public static final int TAB_CHART = 1;
    public static final int TAB_GOALS = 2;
    public static final int TAB_COUNT = 3;

    private String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm, String[] tabsNames) {
        super(fm);
        tabs = tabsNames;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_HISTORY :
                return HistoryFragment.getInstance();
            case TAB_CHART:
                return ChartFragment.getInstance();
            case TAB_GOALS:
                return GoalsFragment.getInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

}
