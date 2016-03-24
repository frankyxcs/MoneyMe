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
            Color.parseColor("#EF5350"),
            Color.parseColor("#EC407A"),
            Color.parseColor("#AB47BC"),
            Color.parseColor("#7E57C2"),
            Color.parseColor("#5C6BC0"),
            Color.parseColor("#42A5F5"),
            Color.parseColor("#0097A7"),
            Color.parseColor("#009688"),
            Color.parseColor("#43A047"),
            Color.parseColor("#689F38"),
            Color.parseColor("#827717"),
            Color.parseColor("#EF6C00"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#8D6E63")
    };

    public static final Integer[] TODO_COLORS = {
            Color.parseColor("#EEEEEE"),
            Color.parseColor("#E6EE9C"),
            Color.parseColor("#81D4FA"),
            Color.parseColor("#A5D6A7"),
            Color.parseColor("#B39DDB"),
            Color.parseColor("#F48FB1"),
            Color.parseColor("#CE93D8"),
            Color.parseColor("#EF9A9A")
    };

    public static final int WHITE_TEXT = Color.parseColor("#FFFFFF");
    public static final int TRANSPARENT = Color.parseColor("#00000000");
    public static final int SECONDARY_TEXT = Color.rgb(114, 114, 114);
    public static final int INCOME_COLOR = Color.parseColor("#388E3C");
    public static final int OUTCOME_COLOR = Color.parseColor("#D32F2F");
    public static final int TRANSFER_COLOR = Color.parseColor("#78909C");
    public static final int ACCENT_COLOR = Color.parseColor("#FFC400");

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
