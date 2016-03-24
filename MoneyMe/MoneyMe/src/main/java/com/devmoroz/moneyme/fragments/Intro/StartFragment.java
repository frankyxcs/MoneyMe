package com.devmoroz.moneyme.fragments.Intro;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devmoroz.moneyme.R;

public class StartFragment extends Fragment implements View.OnClickListener{

    private View view;
    private Button restoreButton;
    private Button newUserButton;
    private Callback mCallback;

    public interface Callback{
        void onRestoreClicked();
        void onNewUserClicked();
    }

    public static StartFragment newInstance(Callback callback) {
        Bundle args = new Bundle();
        StartFragment startSlide = new StartFragment();
        startSlide.setCallback(callback);
        startSlide.setArguments(args);

        return startSlide;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.intro_start, container, false);
        restoreButton = (Button) view.findViewById(R.id.intro_restoreButton);
        newUserButton = (Button) view.findViewById(R.id.intro_newButton);
        restoreButton.setOnClickListener(this);
        newUserButton.setOnClickListener(this);

        return view;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.intro_restoreButton:
                if(mCallback!=null){
                    mCallback.onRestoreClicked();
                }
                break;
            case R.id.intro_newButton:
                if(mCallback!=null){
                    mCallback.onNewUserClicked();
                }
                break;
        }
    }
}
