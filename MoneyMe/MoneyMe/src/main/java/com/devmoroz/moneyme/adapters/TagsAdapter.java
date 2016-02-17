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

    public TagsAdapter() {
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
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_row, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Tag tag = tags.get(position);
        holder.bind(tag, selectedTags.contains(tag));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{
        private final CheckBox selectCheckBox;
        private final TextView titleTextView;

        public MainViewHolder(View itemView) {
            super(itemView);
            selectCheckBox = (CheckBox) itemView.findViewById(R.id.selectCheckBox);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }

        public void bind(Tag model, boolean isSelected) {
            titleTextView.setText(model.getTitle());
            selectCheckBox.setVisibility(View.VISIBLE);
            selectCheckBox.setChecked(isSelected);
        }

    }
}
