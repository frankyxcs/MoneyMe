package com.devmoroz.moneyme.utils;

import com.devmoroz.moneyme.models.CommonInOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CommonInOutSorter {

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
}
