package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devmoroz.moneyme.DetailsActivity;
import com.devmoroz.moneyme.FullScreenImageActivity;
import com.devmoroz.moneyme.MapActivity;
import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.logging.L;
import com.devmoroz.moneyme.models.MapMode;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.AppUtils;
import com.devmoroz.moneyme.utils.Constants;
import com.devmoroz.moneyme.utils.CustomColorTemplate;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.PhotoUtil;
import com.devmoroz.moneyme.utils.ThemeUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.devmoroz.moneyme.widgets.TextBackgroundSpan;

import org.joda.time.DateTime;

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
        TextView textAccountDetails = holder.textDateAdded;
        TextView textCircle = holder.textCircle;
        ImageView photoView = holder.attachedPhoto;
        TextView textNotes = holder.textNotes;
        ImageView colorCircle = holder.colorCircle;
        TextView tagsTextView = holder.textTags;

        if (wData.getType() == TransactionType.INCOME) {
            colorCircle.setColorFilter(CustomColorTemplate.INCOME_COLOR);
        } else {
            colorCircle.setColorFilter(wData.getCategory().getColor());
        }

        textAmount.setText(wData.getFormatedAmount());

        String accountName = wData.getAccountName();
        String title = "";
        switch (wData.getType()){
            case INCOME:
                title = appContext.getString(R.string.income_toolbar_name);
                break;
            case OUTCOME:
                title = wData.getCategory().getTitle();
                break;
            case TRANSFER:
                title = appContext.getString(R.string.transfer_toolbar_name);
        }
        textCategory.setText(title);
        DateTime date = new DateTime(wData.getDateAdded());
        textCircle.setText(date.dayOfWeek().getAsShortText());

        textAccountDetails.setText(accountName);

        if (FormatUtils.isNotEmpty(wData.getNotes())) {
            textNotes.setText(wData.getNotes());
            textNotes.setVisibility(View.VISIBLE);
        } else {
            textNotes.setText(wData.getNotes());
            textNotes.setVisibility(View.GONE);
        }
        if (wData.getTags() != null && wData.getTags().length() > 0) {
            final int tagBackgroundColor = ThemeUtils.getColor(appContext, R.attr.backgroundColorSecondary);
            final float tagBackgroundRadius = appContext.getResources().getDimension(R.dimen.tag_radius);
            final SpannableStringBuilder tags = new SpannableStringBuilder();
            String[] transactionTags = wData.getTags().split(";");
            for (String tag : transactionTags) {
                tags.append(tag);
                tags.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), tags.length() - tag.length(), tags.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tags.append(" ");
            }
            tagsTextView.setVisibility(View.VISIBLE);
            tagsTextView.setText(tags);
        } else {
            tagsTextView.setVisibility(View.INVISIBLE);
        }

        if (FormatUtils.isNotEmpty(wData.getPhoto())) {
            PhotoUtil.setImageWithGlide(appContext, wData.getPhoto(), photoView);
            photoView.setVisibility(View.VISIBLE);
            holder.attachedPhoto.setOnClickListener(v -> {
                Intent intent = new Intent(appContext, FullScreenImageActivity.class);
                intent.putExtra(Constants.IMAGE_PATH, wData.getPhoto());

                appContext.startActivity(intent);
            });
        } else {
            photoView.setVisibility(View.GONE);
            photoView.setImageDrawable(null);
        }

        if (FormatUtils.isNotEmpty(wData.getLocationName())) {
            SpannableString content = new SpannableString(wData.getLocationName());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.textLocation.setText(content);
            holder.textLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppUtils.isNetworkOn(appContext)) {
                        Intent intent = new Intent(appContext, MapActivity.class);
                        intent.putExtra(Constants.EXTRA_MAP_MODE, MapMode.Single);
                        intent.putExtra(Constants.EXTRA_MAP_ITEM_DETAILS, holder.itemId);

                        appContext.startActivity(intent);

                    }
                    else{
                        L.T(appContext,appContext.getString(R.string.no_internet_connection));
                    }
                }
            });
            holder.locationContainer.setVisibility(View.VISIBLE);
        } else {
            holder.locationContainer.setVisibility(View.GONE);
            holder.textLocation.setText("");
        }

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(appContext, DetailsActivity.class);
            intent.putExtra(Constants.DETAILS_ITEM_TYPE, holder.itemType.toString());
            intent.putExtra(Constants.DETAILS_ITEM_ID, holder.itemId);

            appContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView textAmount;
        TextView textCategory;
        TextView textDateAdded;
        ImageView colorCircle;
        TextView textCircle;
        ImageView attachedPhoto;
        TextView textNotes;
        TextView textTags;
        ImageButton deleteButton;
        ImageButton editButton;
        LinearLayout locationContainer;
        Button textLocation;
        String itemId;
        TransactionType itemType;

        public void setItemDetails(String id, TransactionType type) {
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
            this.textTags = (TextView) v.findViewById(R.id.card_main_tags);
            this.deleteButton = (ImageButton) v.findViewById(R.id.card_main_deleteButton);
            this.editButton = (ImageButton) v.findViewById(R.id.card_main_editButton);
            this.locationContainer = (LinearLayout) v.findViewById(R.id.card_main_location_container);
            this.textLocation = (Button) v.findViewById(R.id.card_main_location_link);

            this.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onDeleteClick(itemId, itemType);
                }
            });

            this.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onEditClick(itemId, itemType);
                }
            });
        }

    }
}
