package com.devmoroz.moneyme.fragments.Intro;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.devmoroz.moneyme.R;

public class FinishFragment2 extends Fragment implements View.OnClickListener{

    private View view;
    private Button driveButton;
    private Button localButton;
    private Callback mCallback;

    public interface Callback{
        void onDriveClicked();
        void onLocalClicked();
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public static FinishFragment2 newInstance(Callback callback) {
        Bundle args = new Bundle();
        FinishFragment2 finishSlide = new FinishFragment2();
        finishSlide.setCallback(callback);
        finishSlide.setArguments(args);

        return finishSlide;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.intro_finish1, container, false);
        driveButton = (Button) view.findViewById(R.id.intro_driveButton);
        localButton = (Button) view.findViewById(R.id.intro_localButton);
        LinearLayout m = (LinearLayout) view.findViewById(R.id.main);

        m.setBackgroundColor(Color.parseColor("#673AB7"));

        driveButton.setOnClickListener(this);
        localButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.intro_driveButton:
                if(mCallback!=null){
                    mCallback.onDriveClicked();
                }
                break;
            case R.id.intro_localButton:
                if(mCallback!=null){
                    mCallback.onLocalClicked();
                }
                break;
        }
    }
}
