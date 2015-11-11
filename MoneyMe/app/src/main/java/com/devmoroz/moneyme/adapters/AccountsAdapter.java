package com.devmoroz.moneyme.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {

    @Override
    public AccountsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AccountsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class AccountsViewHolder extends RecyclerView.ViewHolder{

        public AccountsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
