package com.devmoroz.moneyme.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.CommonInOut;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private ArrayList<CommonInOut> inOutData;

    public MainAdapter(ArrayList<CommonInOut> inOutData) {
        this.inOutData = inOutData;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_main, parent, false);

        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        TextView textAmount = holder.textAmount;
        TextView textDescription = holder.textDescription;

        textAmount.setText(inOutData.get(position).getAmount());
        textDescription.setText(inOutData.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return inOutData.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView textAmount;
        TextView textDescription;

        public MainViewHolder(View v) {
            super(v);
            this.textAmount = (TextView)v.findViewById(R.id.card_main_amount);
            this.textDescription = (TextView)v.findViewById(R.id.card_main_description);
        }

    }
}
