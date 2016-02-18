package com.devmoroz.moneyme.utils;

public class Constants {
    public final static String CREATED_ITEM_ID = "createdItemId";
    public final static String CREATED_ITEM_CATEGORY = "createdItemCategory";
    public final static String CREATED_ITEM_AMOUNT= "createdItemAmount";
    public final static String CREATED_ITEM_ACCOUNT= "createdItemAccount";
    public final static String OUTCOME_ACTIVITY= "startOutcomeActivity";
    public final static String INCOME_ACTIVITY= "startIncomeActivity";
    public static final int TAB_HISTORY = 0;
    public static final int TAB_CHART = 1;
    public static final int TAB_GOALS = 3;
    public static final int TAB_ACCOUNTS = 2;
    public static final int TAB_COUNT = 4;
    public final static String DETAILS_ITEM_ID = "detailsItemId";
    public final static String DETAILS_ITEM_TYPE = "detailsItemType";
    public final static String IMAGE_PATH = "itemImagePath";
    public final static String OUTCOME_DEFAULT_CATEGORY = "defaultCategory";
    public static final String SYNC_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String EXTRA_SELECTED_TAGS = "EXTRA_SELECTED_TAGS";
    public static final String STATE_SELECTED_TAGS = "STATE_SELECTED_TAGS";
    public static final String RESULT_EXTRA_TAGS = "RESULT_EXTRA_TAGS";
    public static final String STATE_TRANSACTION_EDIT = "STATE_TRANSACTION_EDIT";
    public static final String[] BACKUP_TABLES = {
            "account",
            "budget",
            "currency",
            "transaction",
            "payee",
    };
}