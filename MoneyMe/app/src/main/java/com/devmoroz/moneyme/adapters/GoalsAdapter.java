package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.utils.CurrencyCache;

import java.util.Collections;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder> {

    private LayoutInflater inflater;
    List<Goal> goalsData = Collections.emptyList();

    public GoalsAdapter(Context context, List<Goal> goalsData) {
        inflater = LayoutInflater.from(context);
        this.goalsData = goalsData;
    }

    public void setGoalsData(List<Goal> goalsData){
        this.goalsData = goalsData;
        notifyDataSetChanged();
    }

    @Override
    public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.goal_row,parent,false);
        GoalsViewHolder holder = new GoalsViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(GoalsViewHolder holder, int position) {
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        Goal current = goalsData.get(position);

    }

    @Override
    public int getItemCount() {
        return goalsData.size();
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder{


        public GoalsViewHolder(View itemView) {

            super(itemView);

        }
    }
}
