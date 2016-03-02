package com.devmoroz.moneyme.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
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

    private TransactionType transactionType;
    private double amount;
    private Long date;
    private Account accountFrom;
    private Account accountTo;
    private Category category;
    private List<Tag> tags;
    private String note;
    private String photoPath;
    private Location location;

    private boolean isAmountSet = false;
    private boolean isDateSet = false;
    private boolean isAccountFromSet = false;
    private boolean isAccountToSet = false;
    private boolean isCategorySet = false;
    private boolean isTagsSet = false;
    private boolean isNoteSet = false;
    private boolean isLocationSet = false;
    private boolean isPhotoPathSet = false;


    public TransactionEdit() {
    }

    private TransactionEdit(Parcel in) {
        transactionType = (TransactionType) in.readSerializable();
        amount = (double) in.readValue(Double.class.getClassLoader());
        date = (Long) in.readValue(Long.class.getClassLoader());
        accountFrom = in.readParcelable(Account.class.getClassLoader());
        accountTo = in.readParcelable(Account.class.getClassLoader());
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
        isAccountFromSet = in.readInt() == 1;
        isAccountToSet = in.readInt() == 1;
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
        dest.writeSerializable(transactionType);
        dest.writeValue(amount);
        dest.writeValue(date);
        dest.writeParcelable(accountFrom, flags);
        dest.writeParcelable(accountTo, flags);
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
        dest.writeInt(isAccountFromSet ? 1 : 0);
        dest.writeInt(isAccountToSet ? 1 : 0);
        dest.writeInt(isCategorySet ? 1 : 0);
        dest.writeInt(isTagsSet ? 1 : 0);
        dest.writeInt(isNoteSet ? 1 : 0);
        dest.writeInt(isLocationSet ? 1 : 0);
        dest.writeInt(isPhotoPathSet ? 1 : 0);
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
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

    public Account getAccountFrom() {
        if (isAccountFromSet || accountFrom != null) {
            return accountFrom;
        }
        return null;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
        isAccountFromSet = true;
    }

    public Account getAccountTo() {
        if (isAccountToSet || accountTo != null) {
            return accountTo;
        }
        return null;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
        isAccountToSet = true;
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

    public String getStringTags() {
        if (isTagsSet || tags != null) {
            StringBuilder builder = new StringBuilder();
            for(Tag t : tags) {
                builder.append(t.getTitle());
                builder.append(";");
            }
            return builder.toString();
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

    public String getLocationName() {
        if (isLocationSet || location != null) {
            return location.getName();
        }
        return null;
    }

    public String getLocationLatLng() {
        if (isLocationSet || location != null) {
            return location.getStringLatLng();
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

    public boolean isAccountFromSet() {
        return isAccountFromSet;
    }
    public boolean isAccountToSet() {
        return isAccountToSet;
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

    public boolean validateAccountFrom() {
        if (getTransactionType() == TransactionType.OUTCOME) {
            return true;
        }

        if (getAccountFrom() == null) {
            return false;
        }

        if (getTransactionType() == TransactionType.TRANSFER && getAccountFrom().equals(getAccountTo())) {
            return false;
        }

        return true;
    }

    public boolean validateAccountTo() {
        if (getTransactionType() == TransactionType.INCOME) {
            return true;
        }

        if (getAccountTo() == null) {
            return false;
        }

        if (getTransactionType() == TransactionType.TRANSFER && getAccountTo().equals(getAccountFrom())) {
            return false;
        }

        return true;
    }

    public Transaction getModel() {
        final Transaction transaction = new Transaction();
        transaction.setAccountFrom(getAccountFrom());
        transaction.setAccountTo(getAccountTo());
        transaction.setCategory(getCategory());
        transaction.setTags(getStringTags());
        transaction.setDateAdded(new Date(getDate()));
        transaction.setAmount(getAmount());
        transaction.setNotes(getNote());
        transaction.setType(getTransactionType());
        transaction.setPhoto(getPhotoPath());
        transaction.setLocationName(getLocationName());
        transaction.setLocation(getLocationLatLng());

        return transaction;
    }
}
