package com.devmoroz.moneyme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devmoroz.moneyme.R;

public class AccountsFragment extends Fragment {

    private View view;

    public static AccountsFragment getInstance() {
        Bundle args = new Bundle();
        AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.accounts_fragment, container, false);
        return view;
    }
}
