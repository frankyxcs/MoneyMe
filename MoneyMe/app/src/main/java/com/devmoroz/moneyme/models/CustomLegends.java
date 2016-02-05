package com.devmoroz.moneyme.models;


import java.util.ArrayList;

public class CustomLegends {
    public String[] titles;
    public int[] colors;

    public CustomLegends(ArrayList<String> titles, int[] colors) {
        this.titles = titles.toArray(new String[titles.size()]);
        this.colors = colors;
    }
}
