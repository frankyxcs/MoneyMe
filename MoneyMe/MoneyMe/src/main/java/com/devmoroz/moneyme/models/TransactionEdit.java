package com.devmoroz.moneyme.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TransactionEdit implements Parcelable {

    public static final Parcelable.Creator<TransactionEdit> CREATOR = new Parcelable.Creator<TransactionEdit>() {
        public TransactionEdit createFromParcel(Parcel in) {
            return new TransactionEdit(in);
        }

        public TransactionEdit[] newArray(int size) {
            return new TransactionEdit[size];
        }
    };

    private double amount;
    private Long date;
    private Account account;
    private Category category;
    private List<Tag> tags;
    private String note;
    private String photoPath;
    private Location location;

    private boolean isAmountSet = false;
    private boolean isDateSet = false;
    private boolean isAccountSet = false;
    private boolean isCategorySet = false;
    private boolean isTagsSet = false;
    private boolean isNoteSet = false;
    private boolean isLocationSet = false;
    private boolean isPhotoPathSet = false;


    public TransactionEdit() {
    }

    private TransactionEdit(Parcel in) {
        amount = (double) in.readValue(Double.class.getClassLoader());
        date = (Long) in.readValue(Long.class.getClassLoader());
        account = in.readParcelable(Account.class.getClassLoader());
        category = in.readParcelable(Category.class.getClassLoader());
        final boolean hasTags = in.readInt() != 0;
        if (hasTags) {
            tags = new ArrayList<>();
            in.readTypedList(tags, Tag.CREATOR);
        }
        note = in.readString();
        photoPath = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        isAmountSet = in.readInt() == 1;
        isDateSet = in.readInt() == 1;
        isAccountSet = in.readInt() == 1;
        isCategorySet = in.readInt() == 1;
        isTagsSet = in.readInt() == 1;
        isNoteSet = in.readInt() == 1;
        isLocationSet = in.readInt() == 1;
        isPhotoPathSet = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(amount);
        dest.writeValue(date);
        dest.writeParcelable(account, flags);
        dest.writeParcelable(category, flags);
        final boolean hasTags = tags != null;
        dest.writeInt(hasTags ? 1 : 0);
        if (hasTags) {
            dest.writeTypedList(tags);
        }
        dest.writeString(note);
        dest.writeString(photoPath);
        dest.writeParcelable(location, flags);
        dest.writeInt(isAmountSet ? 1 : 0);
        dest.writeInt(isDateSet ? 1 : 0);
        dest.writeInt(isAccountSet ? 1 : 0);
        dest.writeInt(isCategorySet ? 1 : 0);
        dest.writeInt(isTagsSet ? 1 : 0);
        dest.writeInt(isNoteSet ? 1 : 0);
        dest.writeInt(isLocationSet ? 1 : 0);
        dest.writeInt(isPhotoPathSet ? 1 : 0);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        isAmountSet = amount > 0;
    }

    public Long getDate() {
        if (isDateSet || date != null) {
            return date;
        }
        return System.currentTimeMillis();
    }

    public void setDate(Long date) {
        this.date = date;
        isDateSet = true;
    }

    public Account getAccount() {
        if (isAccountSet || account != null) {
            return account;
        }
        return null;
    }

    public void setAccount(Account account) {
        this.account = account;
        isAccountSet = true;
    }

    public Category getCategory() {
        if (isCategorySet || category != null) {
            return category;
        }
        return null;
    }

    public void setCategory(Category category) {
        this.category = category;
        isCategorySet = true;
    }

    public List<Tag> getTags() {
        if (isTagsSet || tags != null) {
            return tags;
        }
        return null;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        isTagsSet = true;
    }

    public String getNote() {
        if (isNoteSet || note != null) {
            return note;
        }
        return null;
    }

    public void setNote(String note) {
        this.note = note;
        isNoteSet = true;
    }

    public Location getLocation() {
        if (isLocationSet || location != null) {
            return location;
        }
        return null;
    }

    public void setLocation(Location location) {
        this.location = location;
        isLocationSet = true;
    }

    public String getPhotoPath() {
        if (isPhotoPathSet || photoPath != null) {
            return photoPath;
        }
        return null;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        isPhotoPathSet = true;
    }

    public boolean isAmountSet() {
        return isAmountSet;
    }

    public boolean isDateSet() {
        return isDateSet;
    }

    public boolean isAccountSet() {
        return isAccountSet;
    }

    public boolean isCategorySet() {
        return isCategorySet;
    }

    public boolean isTagsSet() {
        return isTagsSet;
    }

    public boolean isNoteSet() {
        return isNoteSet;
    }
}
