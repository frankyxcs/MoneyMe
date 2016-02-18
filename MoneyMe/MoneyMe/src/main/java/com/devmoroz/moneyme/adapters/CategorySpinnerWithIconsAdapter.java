package com.devmoroz.moneyme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Category;

import java.util.List;

public class CategorySpinnerWithIconsAdapter extends ArrayAdapter<String> {

    List<Category> categories;

    public CategorySpinnerWithIconsAdapter(Context context, int textViewResourceId,
                                           String[] objectsText, List<Category> categories) {
        super(context, textViewResourceId, objectsText);
        this.categories = categories;
    }

    public Category getCategory(int position) {
        if (position <= categories.size()) {
            return categories.get(position);
        }
        return null;
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
        Category model = categories.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.category_row, parent, false);
        TextView label = (TextView) row.findViewById(R.id.categoryText);
        label.setText(model.getTitle());

        ImageView icon = (ImageView) row.findViewById(R.id.categoryIcon);
        icon.setColorFilter(model.getColor());

        return row;
    }

    public View getClosedView(int position, View convertView, ViewGroup parent) {
        Category model = categories.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(model.getTitle());

        return row;
    }
}
