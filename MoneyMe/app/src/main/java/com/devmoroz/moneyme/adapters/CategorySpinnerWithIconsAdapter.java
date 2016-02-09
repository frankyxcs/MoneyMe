package com.devmoroz.moneyme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;

public class CategorySpinnerWithIconsAdapter extends ArrayAdapter<String> {

    int[] categoryColors;
    String[] categoryText;

    public CategorySpinnerWithIconsAdapter(Context context, int textViewResourceId,
                                          String[] objectsText,int[] objectsColors) {
        super(context, textViewResourceId, objectsText);
        categoryText = objectsText;
        categoryColors = objectsColors;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getClosedView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.category_row, parent, false);
        TextView label = (TextView) row.findViewById(R.id.categoryText);
        label.setText(categoryText[position]);

        ImageView icon = (ImageView) row.findViewById(R.id.categoryIcon);
        icon.setColorFilter(categoryColors[position]);

        return row;
    }

    public View getClosedView(int position ,View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(categoryText[position]);

        return row;
    }
}
