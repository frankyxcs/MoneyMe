package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.util.Collections;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    List<Goal> goalsData = Collections.emptyList();
    private final Callback mCallback;

    public interface Callback {
        void onDeleteClick(int id);
        void onEditClick(int id);
    }

    public GoalsAdapter(Context context, List<Goal> goalsData, Callback callback) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.goalsData = goalsData;
        this.mCallback = callback;
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

        holder.goalId = current.getId();
        holder.goalName.setText(current.getName());
        holder.goalProgress.setMax(current.getTotalAmount());
        holder.goalProgress.setProgress(current.getAccumulated());
        String formatted = FormatUtils.goalProgressToString(currency, current.getAccumulated(), current.getTotalAmount());
        holder.goalCurrNeed.setText(formatted);
    }

    @Override
    public int getItemCount() {
        return goalsData.size();
    }

    class GoalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        NumberProgressBar goalProgress;
        ImageView moreMenu;
        TextView goalName;
        TextView goalCurrNeed;
        TextView status;
        int goalId;

        public GoalsViewHolder(View itemView) {
            super(itemView);
            goalProgress = (NumberProgressBar) itemView.findViewById(R.id.goal_progress_bar);
            moreMenu = (ImageView) itemView.findViewById(R.id.goal_more);
            goalName = (TextView) itemView.findViewById(R.id.goal_name);
            goalCurrNeed = (TextView) itemView.findViewById(R.id.goal_needed_available_amount);
            moreMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(context, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.fill_up_goal:
                            mCallback.onEditClick(goalId);
                            return true;
                        case R.id.remove_goal:
                            mCallback.onDeleteClick(goalId);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.inflate(R.menu.menu_goal_item);
            popup.show();
        }
    }
}
