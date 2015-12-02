package com.devmoroz.moneyme.utils;

import com.devmoroz.moneyme.models.CommonInOut;
import com.devmoroz.moneyme.models.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CommonInOutUtils {

    public void sortWalletEntriesByDate(ArrayList<CommonInOut> inout, boolean desc) {

        if (desc) {
            Collections.sort(inout, new Comparator<CommonInOut>() {
                @Override
                public int compare(CommonInOut lhs, CommonInOut rhs) {
                    Date lhsDate = lhs.getDateAdded();
                    Date rhsDate = rhs.getDateAdded();
                    if (lhsDate != null && rhsDate != null) {
                        return rhsDate.compareTo(lhsDate);
                    } else {
                        return 0;
                    }
                }
            });
        } else {
            Collections.sort(inout, new Comparator<CommonInOut>() {
                @Override
                public int compare(CommonInOut lhs, CommonInOut rhs) {
                    Date lhsDate = lhs.getDateAdded();
                    Date rhsDate = rhs.getDateAdded();
                    if (lhsDate != null && rhsDate != null) {
                        return lhsDate.compareTo(rhsDate);
                    } else {
                        return 0;
                    }
                }
            });
        }

    }

    public String getTotalExpense(ArrayList<CommonInOut> inout, Currency currency){
        if(inout!=null && !inout.isEmpty()){
            double total = 0f;
            for(CommonInOut io : inout){
                if(io.getType()==2){
                    total += io.getAmount();
                }
            }
            return FormatUtils.amountToString(currency,total);
        }
        return null;
    }

    public String getTotalIncomes(ArrayList<CommonInOut> inout, Currency currency){
        if(inout!=null && !inout.isEmpty()){
            double total = 0f;
            for(CommonInOut io : inout){
                if(io.getType()== 1){
                    total += io.getAmount();
                }
            }
            return FormatUtils.amountToString(currency,total);
        }
        return null;
    }

    public String[] getTotalInOut(ArrayList<CommonInOut> inout, Currency currency){
        if(inout!=null && !inout.isEmpty()){
            double totalIn = 0f;
            double totalOut = 0f;
            for(CommonInOut io : inout){
                if(io.getType()==1){
                    totalIn += io.getAmount();
                }
                else {
                    totalOut += io.getAmount();
                }
            }
            return new String[]{ FormatUtils.amountToString(currency,totalIn), FormatUtils.amountToString(currency,totalOut)};
        }
        return new String[]{"0.00","0.00"};
    }
}
