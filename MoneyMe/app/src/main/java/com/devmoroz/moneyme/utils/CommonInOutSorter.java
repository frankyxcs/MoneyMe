package com.devmoroz.moneyme.utils;

import com.devmoroz.moneyme.models.CommonInOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CommonInOutSorter {

    public void sortWalletEntriesByDate(ArrayList<CommonInOut> movies){

        Collections.sort(movies, new Comparator<CommonInOut>() {
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
