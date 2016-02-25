package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.devmoroz.moneyme.R;

import java.util.Arrays;
import java.util.List;

public class ColorChooserDialogAdapter extends ArrayAdapter<Integer> {

    List<Integer> colors;
    Context mContext;
    private int checkItem;

    public ColorChooserDialogAdapter(Context context, int textViewResourceId, Integer[] objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.colors = Arrays.asList(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return bindView(position,convertView,parent);
    }

    public View bindView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_colors_item,parent,false);
            holder = new Holder();
            holder.imageViewCircle = (ImageView)convertView.findViewById(R.id.img_circle);
            holder.imageViewIcon = (ImageView)convertView.findViewById(R.id.img_icon);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }
        holder.imageViewCircle.setColorFilter(colors.get(position));
        if (checkItem == position){
            holder.imageViewIcon.setImageResource(R.drawable.ic_fab_check);
        }
        return convertView;
    }

    public int getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(int checkItem) {
        this.checkItem = checkItem;
    }

    static class Holder {
        ImageView imageViewCircle;
        ImageView imageViewIcon;
    }
}
