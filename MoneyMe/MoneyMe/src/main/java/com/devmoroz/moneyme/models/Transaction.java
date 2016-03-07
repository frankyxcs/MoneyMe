package com.devmoroz.moneyme.models;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.devmoroz.moneyme.utils.CurrencyCache;
import com.devmoroz.moneyme.utils.FormatUtils;
import com.devmoroz.moneyme.utils.datetime.TimeUtils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "transactions")
public class Transaction implements Parcelable{

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @DatabaseField(generatedId = true)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private TransactionType type;

    @DatabaseField(canBeNull = true)
    private String notes;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private Category category;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date dateAdded;

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE)
    private double amount;

    @DatabaseField
    private String photo;

    @DatabaseField
    private String location;

    @DatabaseField
    private String locationName;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Account accountFrom;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Account accountTo;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Payee payee;

    @DatabaseField
    private SyncState syncState;

    @DatabaseField
    private String tags;

    private Transaction(Parcel in) {
        setId(in.readString());
        type = TransactionType.fromInt(in.readInt());
        setNotes(in.readString());
        category = in.readParcelable(Category.class.getClassLoader());
        setDateAddedLong(in.readLong());
        setAmount(in.readDouble());
        setPhoto(in.readString());
        setLocation(in.readString());
        setLocationName(in.readString());
        accountFrom = in.readParcelable(Account.class.getClassLoader());
        accountTo = in.readParcelable(Account.class.getClassLoader());
        payee = in.readParcelable(Payee.class.getClassLoader());
        syncState = SyncState.fromInt(in.readInt());
        setTags(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeInt(type.asInt());
        dest.writeString(getNotes());
        dest.writeParcelable(category, flags);
        dest.writeLong(getDateLong());
        dest.writeDouble(getAmount());
        dest.writeString(getPhoto());
        dest.writeString(getLocation());
        dest.writeString(getLocationName());
        dest.writeParcelable(accountFrom, flags);
        dest.writeParcelable(accountTo, flags);
        dest.writeParcelable(payee, flags);
        dest.writeInt(syncState.asInt());
        dest.writeString(getTags());
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    public Transaction() {
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type, String notes, Category category, Date dateAdded, double amount, String photo, String location, Account accountFrom) {
        this.type = type;
        this.notes = notes;
        this.category = category;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.photo = photo;
        this.location = location;
        this.accountFrom = accountTo;
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type, Date dateAdded, double amount, String photo, String location, Account accountFrom, String notes) {
        this.type = type;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.photo = photo;
        this.location = location;
        this.accountFrom = accountFrom;
        this.notes = notes;
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type, String notes, Date dateAdded, double amount, Account accountFrom) {
        this.type = type;
        this.notes = notes;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.category = null;
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type, String notes, Category category, Date dateAdded, double amount, Account accountFrom) {
        this.type = type;
        this.notes = notes;
        this.category = category;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type, String notes, Date dateAdded, double amount, Account accountFrom, Account accountTo, Payee payee, String tags) {
        this.type = type;
        this.notes = notes;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.payee = payee;
        this.tags = tags;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public TransactionType getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public Category getCategory() {
        return category;
    }

    public String getCategoryName() {
        return category != null ? category.getTitle() : "";
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public double getAmount() {
        return amount;
    }

    public String getPhoto() {
        return photo;
    }

    public String getLocation() {
        return location;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public String getAccountFromName() {
        return accountFrom.getName();
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public String getAccountToName() {
        return accountTo.getName();
    }

    public void setId(String id) {
        if (FormatUtils.isNotEmpty(id)) {
            this.id = UUID.fromString(id);
        }
    }

    public void setDateAddedLong(long date){
        if(date!=0){
            this.dateAdded = new Date(date);
        }
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateLong() {
        return dateAdded.getTime();
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public String getFormatedAmount() {
        String sign = CurrencyCache.getCurrencyOrEmpty().getSymbol();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,###.00 " + sign, symbols);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(amount);
    }

    public String getMarkerSnippet(Context context, Currency c) {
        StringBuilder sb = new StringBuilder();
        if (c == null) {
            c = Currency.EMPTY;
        }
        sb.append(category != null ? category.getTitle() : getAccountFromName());
        sb.append(":");
        String a = c.getFormat().format(amount);
        sb.append(a);
        sb.append(System.getProperty("line.separator"));

        sb.append(TimeUtils.formatHumanFriendlyShortDate(context, getDateLong()) + "," + TimeUtils.formatShortTime(context, getDateLong()));

        return sb.toString();
    }

    public String getAccountId() {
        if (type == TransactionType.INCOME) {
            return accountTo.getId();
        } else if (type == TransactionType.OUTCOME) {
            return accountFrom.getId();
        } else return "";
    }

    public Account getAccount() {
        if (type == TransactionType.INCOME) {
            return accountTo;
        } else if (type == TransactionType.OUTCOME) {
            return accountFrom;
        } else return null;
    }
}
