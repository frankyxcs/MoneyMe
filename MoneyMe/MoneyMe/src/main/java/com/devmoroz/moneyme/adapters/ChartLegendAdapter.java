package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.LegendDetails;
import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.util.Collections;
import java.util.List;

public class ChartLegendAdapter extends RecyclerView.Adapter<ChartLegendAdapter.LegendsViewHolder>{

    private LayoutInflater inflater;
    List<LegendDetails> legendData = Collections.emptyList();

    public ChartLegendAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<LegendDetails> legendData){
        this.legendData = legendData;
        notifyDataSetChanged();
    }

    @Override
    public LegendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chart_legend_detail_row,parent,false);
        LegendsViewHolder holder = new LegendsViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(LegendsViewHolder holder, int position) {
        Currency currency = CurrencyCache.getCurrencyOrEmpty();
        LegendDetails details = legendData.get(position);

        TextView sliceAmount = holder.sliceAmount;
        TextView sliceCategory = holder.sliceCategory;
        TextView sliceAmountPercent = holder.sliceAmountPercent;
        LinearLayout sliceColor = holder.sliceColor;

        sliceColor.setBackgroundColor(details.ColorCode);
        sliceCategory.setText(details.CategoryName);
        sliceAmount.setText(FormatUtils.amountToString(currency, details.Amount));
        sliceAmountPercent.setText(FormatUtils.roundValueToString(details.AmountPercent));
    }

    @Override
    public int getItemCount() {
        return legendData.size();
    }

    class LegendsViewHolder extends RecyclerView.ViewHolder{

        TextView sliceCategory;
        TextView sliceAmount;
        TextView sliceAmountPercent;
        LinearLayout sliceColor;

        public LegendsViewHolder(View itemView) {
            super(itemView);
            sliceColor = (LinearLayout) itemView.findViewById(R.id.sliceColor);
            sliceCategory = (TextView)itemView.findViewById(R.id.sliceCategory);
            sliceAmount = (TextView)itemView.findViewById(R.id.sliceAmount);
            sliceAmountPercent = (TextView)itemView.findViewById(R.id.sliceAmountPercent);
        }
    }
}
