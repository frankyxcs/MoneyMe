package com.devmoroz.moneyme.utils;


import android.content.Context;
import android.graphics.Color;

import com.devmoroz.moneyme.R;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class CustomColorTemplate {

    public static final int[] PIECHART_COLORS = {
            Color.rgb(192, 255, 140),
            Color.rgb(26, 255, 255),
            Color.rgb(255, 247, 140),
            Color.rgb(255, 208, 140),
            Color.rgb(176, 190, 196),
            Color.rgb(181, 138, 255),
            Color.rgb(255, 66, 129),
            Color.rgb(139, 195, 74),
            Color.rgb(106, 240, 176),
            Color.rgb(140, 234, 255),
            Color.rgb(255, 140, 157),
            Color.rgb(188, 170, 164),
            Color.rgb(140, 158, 255),
            Color.rgb(121, 85, 72)
    };

    public static final int SECONDARY_TEXT = Color.rgb(114, 114, 114);
    public static final int INCOME_COLOR = Color.parseColor("#388E3C");
    public static final int OUTCOME_COLOR = Color.parseColor("#D32F2F");

    public static int[] getCategoriesColors(Context context, ArrayList<String> categories) {
        int[] colors = new int[categories.size()];
        String[] allCategories = context.getResources().getStringArray(R.array.outcome_categories);
        HashMap<String, Integer> catCol = new HashMap<>();

        int i = 0;
        for (String category : allCategories) {
            catCol.put(category, PIECHART_COLORS[i]);
            i++;
        }
        int j = 0;
        for (String cat : categories) {
            colors[j] = catCol.get(cat);
            j++;
        }
        return colors;
    }

    public static int getColorForCategory(Context context, String category) {
        String[] allCategories = context.getResources().getStringArray(R.array.outcome_categories);
        HashMap<String, Integer> catCol = new HashMap<>();
        int i = 0;
        for (String cat : allCategories) {
            catCol.put(cat, PIECHART_COLORS[i]);
            i++;
        }
        if (catCol.containsKey(category)) {
            return catCol.get(category);
        }

        return OUTCOME_COLOR;
    }
}
