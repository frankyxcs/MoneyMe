package com.devmoroz.moneyme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devmoroz.moneyme.fragments.GoalsFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    private String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);

        tabs = new String[]{
          "Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5"
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
                return GoalsFragment.getInstance();
            case 1:
                return GoalsFragment.getInstance();
            case 2:
                return GoalsFragment.getInstance();
            case 3:
                return GoalsFragment.getInstance();
            case 4:
                return GoalsFragment.getInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
