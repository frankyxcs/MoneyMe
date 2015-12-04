package com.devmoroz.moneyme.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.CommonInOut;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.PhotoUtil;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MainViewHolder> {

    private ArrayList<CommonInOut> inOutData = new ArrayList<>();
    private LayoutInflater wInflater;
    private Context appContext;
    private final Callback mCallback;

    public interface Callback {
        void onDeleteClick(int id, int type);
        void onEditClick(int id, int type);
    }

    public HistoryAdapter(Context context, Callback callback) {
        appContext = context;
        wInflater = LayoutInflater.from(context);
        mCallback = callback;
    }

    public void setInOutData(ArrayList<CommonInOut> inOutData) {
        this.inOutData = inOutData;

        notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = wInflater.inflate(R.layout.card_history, parent, false);

        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        CommonInOut wData = inOutData.get(position);
        holder.setItemDetails(wData.getId(), wData.getType());

        TextView textAmount = holder.textAmount;
        TextView textCategory = holder.textCategory;
        TextView textDateAdded = holder.textDateAdded;
        TextView textCircle = holder.textCircle;
        ImageView photoView = holder.attachedPhoto;
        TextView textNotes = holder.textNotes;

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Drawable background = wData.getType() == 1 ? appContext.getResources().getDrawable(R.drawable.circle_green) : appContext.getResources().getDrawable(R.drawable.circle_red);
            textCircle.setBackgroundDrawable(background);
        } else {
            Drawable background = wData.getType() == 1 ? appContext.getDrawable(R.drawable.circle_green) : appContext.getDrawable(R.drawable.circle_red);
            textCircle.setBackground(background);
        }

        textAmount.setText(wData.getFormatedAmount());
        String categ = wData.getCategory() != null ? wData.getCategory() : wData.getAccount();
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
            PhotoUtil.setImageWithPicasso(appContext,wData.getPhoto(),photoView);
            photoView.setVisibility(View.VISIBLE);
        }
        else
        {
            photoView.setVisibility(View.GONE);
            photoView.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return inOutData.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{

        TextView textAmount;
        TextView textCategory;
        TextView textDateAdded;
        TextView textCircle;
        ImageView attachedPhoto;
        TextView textNotes;
        ImageButton deleteButton;
        ImageButton editButton;
        int itemId;
        int itemType;

        public void setItemDetails(int id,int type){
            itemId = id;
            itemType = type;
        }

        public MainViewHolder(View v) {
            super(v);
            this.textCircle = (TextView) v.findViewById(R.id.card_main_icon);
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
