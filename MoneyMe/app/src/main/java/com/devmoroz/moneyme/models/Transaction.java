package com.devmoroz.moneyme.models;


import com.devmoroz.moneyme.utils.CurrencyCache;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "transaction")
public class Transaction {

    public static final String ACCOUNT_ID_FIELD_NAME = "account_id";

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

    @DatabaseField(canBeNull = false,dataType = DataType.DOUBLE)
    private double amount;

    @DatabaseField
    private String photo;

    @DatabaseField
    private String location;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ACCOUNT_ID_FIELD_NAME)
    private Account account;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Payee payee;

    @DatabaseField
    private SyncState syncState;

    @DatabaseField
    private String tags;

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

    public Transaction(TransactionType type, String notes, Category category, Date dateAdded, double amount, String photo, String location, Account account) {
        this.type = type;
        this.notes = notes;
        this.category = category;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.photo = photo;
        this.location = location;
        this.account = account;
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type,Date dateAdded, double amount, String photo, String location, Account account, String notes) {
        this.type = type;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.photo = photo;
        this.location = location;
        this.account = account;
        this.notes = notes;
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type, String notes, Date dateAdded, double amount, Account account) {
        this.type = type;
        this.notes = notes;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.account = account;
        this.category = null;
        this.syncState = SyncState.None;
    }

    public Transaction(TransactionType type, String notes, Category category, Date dateAdded, double amount, Account account) {
        this.type = type;
        this.notes = notes;
        this.category = category;
        this.dateAdded = dateAdded;
        this.amount = amount;
        this.account = account;
        this.syncState = SyncState.None;
    }

    public String getId() {
        return id.toString();
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
        return category!=null ? category.getTitle() : "";
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

    public Account getAccount() {
        return account;
    }

    public String getAccountName() { return account.getName();}

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

    public long getDateLong(){ return dateAdded.getTime();}

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAccount(Account account) {
        this.account = account;
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
}
