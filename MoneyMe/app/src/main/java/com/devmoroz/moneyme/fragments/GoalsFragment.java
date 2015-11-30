package com.devmoroz.moneyme.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.GoalsAdapter;
import com.devmoroz.moneyme.eventBus.BusProvider;
import com.devmoroz.moneyme.eventBus.GoalsChangeEvent;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.utils.AnimationUtils;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;
import com.devmoroz.moneyme.widgets.EmptyRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GoalsFragment extends Fragment {

    private View view;
    private EmptyRecyclerView recyclerView;
    private GoalsAdapter adapter;
    private LinearLayout mTextEmpty;
    private CardView btnAddNewGoal;
    private static Date goalDate;

    EditText goalRequiredInput;
    Button goalDeadlineDate;
    EditText goalName;

    List<Goal> goals;

    public static GoalsFragment getInstance() {
        Bundle args = new Bundle();
        GoalsFragment fragment = new GoalsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        goals = MoneyApplication.getInstance().goals;
        view = inflater.inflate(R.layout.goals_fragment, container, false);
        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.goalsList);
        mTextEmpty = (LinearLayout) view.findViewById(R.id.goalsEmpty);
        recyclerView.setEmptyView(mTextEmpty);

        btnAddNewGoal = (CardView) view.findViewById(R.id.add_new_goal);
        btnAddNewGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewGoal();
            }
        });

        adapter = new GoalsAdapter(getActivity(), goals);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private void AddNewGoal(){
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.goal)
                .customView(R.layout.dialog_fragment_add_goal, true)
                .theme(Theme.LIGHT)
                .negativeText(R.string.cancel)
                .positiveText(R.string.save)
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .widgetColorRes(R.color.colorPrimary)
                .alwaysCallInputCallback()
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if(FormatUtils.isEmpty(goalName) || FormatUtils.isEmpty(goalDeadlineDate.getText().toString()) || FormatUtils.isEmpty(goalRequiredInput)){
                            return;
                        }
                        else{
                            materialDialog.dismiss();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .build();

        goalDate = new Date();
        goalRequiredInput = (EditText) dialog.getCustomView().findViewById(R.id.goalAddRequired);
        goalDeadlineDate = (Button) dialog.getCustomView().findViewById(R.id.goalAddDeadlineDate);
        goalDeadlineDate.setText(TimeUtils.formatShortDate(getContext(), goalDate));
        goalDeadlineDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDate(goalDeadlineDate);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        goalName = (EditText) dialog.getCustomView().findViewById(R.id.goalAddName);
        EditText goalAvailableInput = (EditText) dialog.getCustomView().findViewById(R.id.goalAddAvailable);
        goalRequiredInput.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        goalAvailableInput.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void OnGoalsChange(GoalsChangeEvent event) {
        CheckGoals();
    }

    private void CheckGoals() {
        goals = MoneyApplication.getInstance().goals;
        adapter.setGoalsData(goals);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Button date;

        public void setDate(Button date) {
            this.date = date;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH,month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            goalDate = cal.getTime();
            date.setText(TimeUtils.formatShortDate(getContext(),goalDate));
        }
    }
}
