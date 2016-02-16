package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.devmoroz.moneyme.DetailsActivity;
import com.devmoroz.moneyme.FullScreenImageActivity;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.PhotoUtil;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.Collections;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MainViewHolder> {

    private List<Transaction> transactions = Collections.emptyList();
    private LayoutInflater wInflater;
    private Context appContext;
    private final Callback mCallback;

    public interface Callback {
        void onDeleteClick(String id, TransactionType type);
        void onEditClick(String id, TransactionType type);
    }

    public HistoryAdapter(Context context, Callback callback) {
        appContext = context;
        wInflater = LayoutInflater.from(context);
        mCallback = callback;
    }

    public void setInOutData(List<Transaction> transactions) {
        this.transactions = transactions;

        notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = wInflater.inflate(R.layout.card_history, parent, false);

        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, int position) {
        final Transaction wData = transactions.get(position);
        holder.setItemDetails(wData.getId(), wData.getType());

        TextView textAmount = holder.textAmount;
        TextView textCategory = holder.textCategory;
        TextView textDateAdded = holder.textDateAdded;
        TextView textCircle = holder.textCircle;
        ImageView photoView = holder.attachedPhoto;
        TextView textNotes = holder.textNotes;
        ImageView colorCircle = holder.colorCircle;

        if(wData.getType() == TransactionType.INCOME){
            colorCircle.setColorFilter(CustomColorTemplate.INCOME_COLOR);
        }else{
            colorCircle.setColorFilter(wData.getCategory().getColor());
        }

        textAmount.setText(wData.getFormatedAmount());
        String categ = wData.getType() == TransactionType.OUTCOME ? wData.getCategory().getTitle() : wData.getAccountName();
        textCategory.setText(categ);
        textCircle.setText(categ.substring(0, 1));
        textDateAdded.setText(TimeUtils.formatHumanFriendlyShortDate(appContext, wData.getDateLong()));

        if(FormatUtils.isNotEmpty(wData.getNotes())){
            textNotes.setText(wData.getNotes());
            textNotes.setVisibility(View.VISIBLE);
        }
        else {
            textNotes.setText(wData.getNotes());
            textNotes.setVisibility(View.GONE);
        }

        if(FormatUtils.isNotEmpty(wData.getPhoto())){
            PhotoUtil.setImageWithGlide(appContext,wData.getPhoto(),photoView);
            photoView.setVisibility(View.VISIBLE);
            holder.attachedPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra(Constants.IMAGE_PATH, wData.getPhoto());

                    context.startActivity(intent);
                }
            });
        }
        else
        {
            photoView.setVisibility(View.GONE);
            photoView.setImageDrawable(null);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Constants.DETAILS_ITEM_TYPE, holder.itemType.toString());
                intent.putExtra(Constants.DETAILS_ITEM_ID, holder.itemId);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView textAmount;
        TextView textCategory;
        TextView textDateAdded;
        ImageView colorCircle;
        TextView textCircle;
        ImageView attachedPhoto;
        TextView textNotes;
        ImageButton deleteButton;
        ImageButton editButton;
        String itemId;
        TransactionType itemType;

        public void setItemDetails(String id, TransactionType type){
            itemId = id;
            itemType = type;
        }

        public MainViewHolder(View v) {
            super(v);
            this.mView = v;
            this.textCircle = (TextView) v.findViewById(R.id.card_main_categoryLetter);
            this.colorCircle = (ImageView) v.findViewById(R.id.card_main_categoryColor);
            this.textAmount = (TextView) v.findViewById(R.id.card_main_amount);
            this.textCategory = (TextView) v.findViewById(R.id.card_main_category);
            this.textDateAdded = (TextView) v.findViewById(R.id.card_main_date);
            this.attachedPhoto = (ImageView) v.findViewById(R.id.card_main_attachedPhoto);
            this.textNotes = (TextView) v.findViewById(R.id.card_main_notes);
            this.deleteButton = (ImageButton) v.findViewById(R.id.card_main_deleteButton);
            this.editButton = (ImageButton) v.findViewById(R.id.card_main_editButton);

            this.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onDeleteClick(itemId,itemType);
                }
            });

            this.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onEditClick(itemId,itemType);
                }
            });
        }

    }
}
