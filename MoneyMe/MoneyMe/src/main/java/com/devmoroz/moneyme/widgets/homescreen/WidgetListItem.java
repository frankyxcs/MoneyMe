package com.devmoroz.moneyme.widgets.homescreen;

import android.text.format.Time;

public class WidgetListItem {

    private final String mItemTitle;

    private final Time mDueDate;

    private final String mItemId;

    private final WidgetItemType mItemType;

    public WidgetListItem(String mItemTitle, Time mDueDate, String mItemId, WidgetItemType mItemType) {
        this.mItemTitle = mItemTitle;
        this.mDueDate = mDueDate;
        this.mItemId = mItemId;
        this.mItemType = mItemType;
    }

    public String getmItemTitle() {
        return mItemTitle;
    }

    public Time getmDueDate() {
        return mDueDate;
    }

    public String getmItemId() {
        return mItemId;
    }

    public WidgetItemType getmItemType() {
        return mItemType;
    }
}
