package com.devmoroz.moneyme.utils.datetime;


import android.content.Context;

import java.util.Calendar;

public class PeriodUtils  {

    public static String GetPeriodString(int period, int monthStart, Context context, boolean shortFormat){
        Calendar currentDate = Calendar.getInstance();

        Calendar dateStart = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();

        if(currentDate.get(Calendar.DAY_OF_MONTH) == monthStart){
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        }
        else {
            dateStart.set(Calendar.DAY_OF_MONTH, monthStart);
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        }
        dateEnd.add(Calendar.DAY_OF_MONTH, 1);

        switch (period) {
            case 1:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 2:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -2);
                }
                else {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 3:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -3);
                }
                else {
                    dateStart.add(Calendar.MONTH, -2);
                }
                break;
            case 6:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -6);
                }
                else {
                    dateStart.add(Calendar.MONTH, -5);
                }
                break;
            case 12:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -12);
                }
                else {
                    dateStart.add(Calendar.MONTH, -11);
                }
                break;
        }
        StringBuilder sb = new StringBuilder();

        return TimeUtils.formatWidgetDateRange(dateStart.getTimeInMillis(),currentDate.getTimeInMillis(),sb,context,shortFormat);
    }

}
