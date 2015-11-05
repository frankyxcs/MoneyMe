package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.CommonInOut;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MainViewHolder> {

    private ArrayList<CommonInOut> inOutData = new ArrayList<>();
    private LayoutInflater wInflater;

    public HistoryAdapter(Context context){
        wInflater = LayoutInflater.from(context);
    }

    public HistoryAdapter(ArrayList<CommonInOut> inOutData) {
        this.inOutData = inOutData;
    }

    public void setInOutData(ArrayList<CommonInOut> inOutData){
        this.inOutData = inOutData;

        notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history, parent, false);

        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        CommonInOut wData = inOutData.get(position);


        TextView textAmount = holder.textAmount;
        TextView textCategory = holder.textCategory;
        TextView textDateAdded = holder.textDateAdded;
        LinearLayout linearLayout = holder.linearLayout;

        linearLayout.setBackgroundColor(wData.getType()== 1 ? Color.parseColor("#80CBC4") : Color.parseColor("#FFAB91"));
        textAmount.setText(wData.getFormatedAmount());
        textCategory.setText(wData.getCategory());
        textDateAdded.setText(wData.getFormatedDate());
    }

    @Override
    public int getItemCount() {
        return inOutData.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView textAmount;
        TextView textCategory;
        TextView textDateAdded;
        LinearLayout linearLayout;

        public MainViewHolder(View v) {
            super(v);
            this.linearLayout = (LinearLayout) v.findViewById(R.id.card_view_main_color);
            this.textAmount = (TextView)v.findViewById(R.id.card_main_amount);
            this.textCategory = (TextView)v.findViewById(R.id.card_main_category);
            this.textDateAdded = (TextView)v.findViewById(R.id.card_main_date);
        }

    }
}
