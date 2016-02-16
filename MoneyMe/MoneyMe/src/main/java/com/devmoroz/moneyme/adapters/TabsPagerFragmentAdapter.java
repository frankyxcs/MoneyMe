package com.devmoroz.moneyme.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devmoroz.moneyme.fragments.AccountsFragment;
import com.devmoroz.moneyme.fragments.ChartFragment;
import com.devmoroz.moneyme.fragments.GoalsFragment;
import com.devmoroz.moneyme.fragments.HistoryFragment;
import com.devmoroz.moneyme.utils.Constants;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {



    private String[] tabs;
    private FloatingActionsMenu fab;

    public TabsPagerFragmentAdapter(FragmentManager fm, String[] tabsNames,FloatingActionsMenu fab) {
        super(fm);
        tabs = tabsNames;
        this.fab = fab;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Constants.TAB_HISTORY :
                return HistoryFragment.getInstance(fab);
            case Constants.TAB_CHART:
                return ChartFragment.getInstance();
            case Constants.TAB_GOALS:
                return GoalsFragment.getInstance();
            case Constants.TAB_ACCOUNTS:
                return AccountsFragment.getInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return Constants.TAB_COUNT;
    }

}
