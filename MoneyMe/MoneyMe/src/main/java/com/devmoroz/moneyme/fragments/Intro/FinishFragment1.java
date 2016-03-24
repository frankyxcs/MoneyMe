package com.devmoroz.moneyme.fragments.Intro;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.adapters.AccountSpinnerWithIconsAdapter;
import com.devmoroz.moneyme.helpers.CurrencyHelper;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.widgets.DecimalDigitsInputFilter;

import java.sql.SQLException;

public class FinishFragment1 extends Fragment {

    private View view;
    private EditText accountNameText;
    private EditText startBalanceText;
    private Spinner accountTypeSpinner;
    private Button currencyButton;
    private View done;

    public static FinishFragment1 newInstance(View doneButton) {
        Bundle args = new Bundle();
        FinishFragment1 finishSlide = new FinishFragment1();
        finishSlide.setArguments(args);
        finishSlide.done = doneButton;

        return finishSlide;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.intro_finish2, container, false);
        accountNameText = (EditText) view.findViewById(R.id.intro_accountAddName);
        startBalanceText = (EditText) view.findViewById(R.id.intro_accountAddBalance);
        accountTypeSpinner = (Spinner) view.findViewById(R.id.intro_accountAddType);
        currencyButton = (Button) view.findViewById(R.id.intro_accountCurrency);
        LinearLayout m = (LinearLayout) view.findViewById(R.id.main);
        done.setEnabled(false);


        m.setBackgroundColor(Color.parseColor("#3F51B5"));

        initListenersAndWidgets();

        return view;
    }

    private void initListenersAndWidgets() {
        accountNameText.addTextChangedListener(createTextWatcher());

        String[] types = getContext().getResources().getStringArray(R.array.account_types);
        AccountSpinnerWithIconsAdapter adapter = new AccountSpinnerWithIconsAdapter(getContext(),R.layout.account_type_row,types,true);
        accountTypeSpinner.setAdapter(adapter);
        startBalanceText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        currencyButton.setText("USD (United States dollar)");
        currencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencyHelper ch = new CurrencyHelper(getContext(), MoneyApplication.getInstance().GetDBHelper(), new CurrencyHelper.Callback() {
                    @Override
                    public void onSelectClick(String currencyText) {
                        currencyButton.setText(currencyText);
                    }
                });
                ch.show();
            }
        });
    }

    private TextWatcher createTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                done.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    public boolean setUpAccount(){
        try{
            String name = accountNameText.getText().toString();
            int type = accountTypeSpinner.getSelectedItemPosition();
            double amount = 0f;
            if(!FormatUtils.isEmpty(startBalanceText)){
                amount = Double.parseDouble(startBalanceText.getText().toString());
            }
            Account acc = new Account(name,amount,type,true);
            DBHelper helper = MoneyApplication.GetDBHelper();
            helper.getAccountDAO().create(acc);
            return true;
        }catch (SQLException ex){
            return false;
        }
    }

}
