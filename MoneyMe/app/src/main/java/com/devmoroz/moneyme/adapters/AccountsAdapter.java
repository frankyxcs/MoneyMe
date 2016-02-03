package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.AccountRow;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.util.Collections;
import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    List<AccountRow> accountData = Collections.emptyList();
    final int[] typesOfAccountIcons = {R.drawable.ic_cash_multiple,R.drawable.ic_credit_card,R.drawable.ic_bank};

    public AccountsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
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
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        AccountRow current = accountData.get(position);

        holder.accountName.setText(current.name);
        holder.accountAvailable.setText(FormatUtils.attachAmountToTextWithoutBrackets(context.getString(R.string.balance),currency, current.total,true));
        holder.accountExpense.setText(FormatUtils.attachAmountToTextWithoutBrackets(context.getString(R.string.header_expenses),currency, current.expense, false));
        holder.accountIcon.setImageResource(typesOfAccountIcons[current.type]);
    }

    @Override
    public int getItemCount() {
        return accountData.size();
    }

    public class AccountsViewHolder extends RecyclerView.ViewHolder{
        TextView accountName;
        TextView accountAvailable;
        TextView accountExpense;
        ImageView accountIcon;

        public AccountsViewHolder(View itemView) {
            super(itemView);
            accountName = (TextView)itemView.findViewById(R.id.accountName);
            accountAvailable = (TextView)itemView.findViewById(R.id.accountAvailable);
            accountExpense = (TextView)itemView.findViewById(R.id.accountSpending);
            accountIcon = (ImageView)itemView.findViewById(R.id.accountIcon);
        }
    }
}
