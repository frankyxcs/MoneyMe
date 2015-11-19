package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.AccountRow;

import java.util.Collections;
import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {

    private LayoutInflater inflater;
    List<AccountRow> accountData = Collections.emptyList();

    public AccountsAdapter(Context context, List<AccountRow> data) {
        inflater = LayoutInflater.from(context);
        accountData = data;
    }

    public void setAccountsData(List<AccountRow> accountData){
        this.accountData = accountData;
        notifyDataSetChanged();
    }

    @Override
    public AccountsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.account_row,parent,false);
        AccountsViewHolder holder = new AccountsViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AccountsViewHolder holder, int position) {
        AccountRow current = accountData.get(position);

        holder.accountName.setText(current.name);
        holder.accountAvailable.setText(String.valueOf(current.total));
        holder.accountExpense.setText(String.valueOf(current.expense));
    }

    @Override
    public int getItemCount() {
        return accountData.size();
    }

    class AccountsViewHolder extends RecyclerView.ViewHolder{
        TextView accountName;
        TextView accountAvailable;
        TextView accountExpense;

        public AccountsViewHolder(View itemView) {
            super(itemView);
            accountName = (TextView)itemView.findViewById(R.id.accountName);
            accountAvailable = (TextView)itemView.findViewById(R.id.accountAvailable);
            accountExpense = (TextView)itemView.findViewById(R.id.accountSpending);
        }
    }
}
