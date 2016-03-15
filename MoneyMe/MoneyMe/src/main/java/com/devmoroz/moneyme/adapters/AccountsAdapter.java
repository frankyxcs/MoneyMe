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

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.AccountRow;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.util.Collections;
import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder> {

    private Context context;
    private final Callback mCallback;
    List<AccountRow> accountData = Collections.emptyList();
    final int[] typesOfAccountIcons = {R.drawable.ic_cash_multiple,R.drawable.ic_credit_card,R.drawable.ic_bank};

    public interface Callback {
        void onDeleteClick(String id);
        void onEditClick(String id);
    }

    public AccountsAdapter(Context context, Callback callback) {
        this.context = context;
        this.mCallback = callback;
    }

    public void setAccountsData(List<AccountRow> accountData){
        this.accountData = accountData;
        notifyDataSetChanged();
    }

    @Override
    public AccountsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_row, parent, false);

        return new AccountsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountsViewHolder holder, int position) {
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        AccountRow current = accountData.get(position);
        holder.bind(current,currency,position);
    }

    @Override
    public int getItemCount() {
        return accountData.size();
    }

    public class AccountsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView accountName;
        TextView accountAvailable;
        TextView accountExpense;
        ImageView accountIcon;
        ImageView moreMenu;
        String accountId;
        int position;

        public AccountsViewHolder(View itemView) {
            super(itemView);
            accountName = (TextView)itemView.findViewById(R.id.accountName);
            accountAvailable = (TextView)itemView.findViewById(R.id.accountAvailable);
            accountExpense = (TextView)itemView.findViewById(R.id.accountSpending);
            accountIcon = (ImageView)itemView.findViewById(R.id.accountIcon);
            moreMenu = (ImageView) itemView.findViewById(R.id.account_more);
            moreMenu.setOnClickListener(this);
        }

        public void bind(AccountRow model, Currency currency,int position){
            accountName.setText(model.name);
            accountAvailable.setText(FormatUtils.attachAmountToTextWithoutBrackets(context.getString(R.string.balance),currency, model.total, false));
            accountExpense.setText(FormatUtils.attachAmountToTextWithoutBrackets(context.getString(R.string.header_expenses),currency, model.expense, false));
            accountIcon.setImageResource(typesOfAccountIcons[model.type]);
            this.accountId = model.id;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            PopupMenu popup = new PopupMenu(context, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit_account:
                            mCallback.onEditClick(accountId);
                            return true;
                        case R.id.remove_account:
                            mCallback.onDeleteClick(accountId);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.inflate(R.menu.menu_account);
            popup.show();
        }
    }
}
