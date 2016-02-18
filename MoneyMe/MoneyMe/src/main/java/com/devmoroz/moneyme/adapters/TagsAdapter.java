package com.devmoroz.moneyme.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.models.Tag;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.MainViewHolder> {

    private List<Tag> tags = Collections.emptyList();
    private Set<Tag> selectedTags = new HashSet<>();
    private final OnTagClickListener listener;

    public interface OnTagClickListener {
        void onModelClick(View view, Tag tag, int position, boolean isSelected);
    }

    public TagsAdapter(OnTagClickListener listener) {
        this.listener = listener;
    }

    public Set<Tag> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(Set<Tag> selectedTags) {
        this.selectedTags.clear();
        if (selectedTags != null) {
            this.selectedTags.addAll(selectedTags);
        }
        notifyDataSetChanged();
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }

    public void toggleTagSelected(Tag tag, int position) {
        if (!selectedTags.add(tag)) {
            selectedTags.remove(tag);
        }
        notifyItemChanged(position);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_row, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Tag tag = tags.get(position);
        holder.bind(tag,position, selectedTags.contains(tag));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final CheckBox selectCheckBox;
        private final TextView titleTextView;
        private Tag tag;
        private int position;
        private boolean isSelected;

        public MainViewHolder(View itemView) {
            super(itemView);
            selectCheckBox = (CheckBox) itemView.findViewById(R.id.selectCheckBox);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }

        public void bind(Tag model,int position, boolean isSelected) {
            this.tag = model;
            this.position = position;
            this.isSelected = isSelected;
            titleTextView.setText(model.getTitle());
            selectCheckBox.setVisibility(View.VISIBLE);
            selectCheckBox.setChecked(isSelected);
        }

        @Override
        public void onClick(View v) {
            listener.onModelClick(v,tag,position,isSelected);
        }
    }
}
