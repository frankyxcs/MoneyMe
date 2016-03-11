package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.Collections;
import java.util.List;

public class CategoryHistoryAdapter extends RecyclerView.Adapter<CategoryHistoryAdapter.MainViewHolder>{

    private List<Transaction> transactions = Collections.emptyList();
    private Context mContext;
    private final Callback mCallback;

    public interface Callback {
        void onRowClick(Transaction transaction);

        void onRowLongClick(Transaction transaction);
    }

    public CategoryHistoryAdapter(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;

        notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_history_row, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.bind(mContext,transaction,position);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public Transaction getTransaction(int position) {
        if (position <= transactions.size()) {
            return transactions.get(position);
        }
        return null;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView dateTextView;
        TextView amountTextView;
        View mView;

        int position;

        public MainViewHolder(View v) {
            super(v);
            this.mView = v;
            this.dateTextView = (TextView) v.findViewById(R.id.dateTextView);
            this.amountTextView = (TextView) v.findViewById(R.id.amountTextView);
            mView.setOnClickListener(this);
            mView.setOnLongClickListener(this);
        }

        public void bind(Context context, Transaction model, int position) {
            this.position = position;
            dateTextView.setText(TimeUtils.formatHumanFriendlyShortDateTime(context, model.getDateLong()));
            amountTextView.setText(model.getFormatedAmount());
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.categoryHistoryDetailsRow){

            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
