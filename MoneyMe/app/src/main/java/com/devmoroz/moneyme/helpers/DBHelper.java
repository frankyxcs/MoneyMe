package com.devmoroz.moneyme.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.dao.AccountDAO;
import com.devmoroz.moneyme.dao.BudgetDAO;
import com.devmoroz.moneyme.dao.CategoryDAO;
import com.devmoroz.moneyme.dao.CurrencyDAO;
import com.devmoroz.moneyme.dao.GoalDAO;
import com.devmoroz.moneyme.dao.PayeeDAO;
import com.devmoroz.moneyme.dao.TransactionDAO;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Budget;
import com.devmoroz.moneyme.models.Category;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Payee;
import com.devmoroz.moneyme.models.Transaction;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "moneyme.db";

    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    private GoalDAO goalDao = null;
    private TransactionDAO transactionDAO = null;
    private CurrencyDAO currencyDAO = null;
    private AccountDAO accountDAO = null;
    private CategoryDAO categoryDAO = null;
    private BudgetDAO budgetDAO = null;
    private PayeeDAO payeeDAO = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Payee.class);
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, Goal.class);
            TableUtils.createTable(connectionSource, Transaction.class);
            TableUtils.createTable(connectionSource, Currency.class);
            TableUtils.createTable(connectionSource, Account.class);
            TableUtils.createTable(connectionSource, Budget.class);
            addDefaults();
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource,Payee.class,true);
            TableUtils.dropTable(connectionSource, Category.class,true);
            TableUtils.dropTable(connectionSource, Goal.class, true);
            TableUtils.dropTable(connectionSource, Transaction.class, true);
            TableUtils.dropTable(connectionSource, Currency.class, true);
            TableUtils.dropTable(connectionSource, Account.class, true);
            TableUtils.dropTable(connectionSource, Budget.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "error upgrading db " + DATABASE_NAME + "from ver " + oldVersion);
            throw new RuntimeException(e);
        }
    }

    private void addDefaults() {
        new DBDefaults(mContext, MoneyApplication.getInstance().GetDBHelper()).addDefaults();
    }

    @Override
    public void close() {
        super.close();
        goalDao = null;
        currencyDAO = null;
        accountDAO = null;
        transactionDAO = null;
        categoryDAO = null;
        budgetDAO = null;
        payeeDAO = null;
    }

    public GoalDAO getGoalDAO() throws SQLException {
        if (goalDao == null) {
            goalDao = new GoalDAO(getConnectionSource(), Goal.class);
        }
        return goalDao;
    }

    public TransactionDAO getTransactionDAO() throws SQLException {
        if (transactionDAO == null) {
            transactionDAO = new TransactionDAO(getConnectionSource(), Transaction.class);
        }
        return transactionDAO;
    }

    public CurrencyDAO getCurrencyDAO() throws SQLException {
        if (currencyDAO == null) {
            currencyDAO = new CurrencyDAO(getConnectionSource(), Currency.class);
        }
        return currencyDAO;
    }

    public AccountDAO getAccountDAO() throws SQLException {
        if (accountDAO == null) {
            accountDAO = new AccountDAO(getConnectionSource(), Account.class);
        }
        return accountDAO;
    }

    public CategoryDAO getCategoryDAO() throws SQLException {
        if (categoryDAO == null) {
            categoryDAO = new CategoryDAO(getConnectionSource(), Category.class);
        }
        return categoryDAO;
    }

    public BudgetDAO getBudgetDAO() throws SQLException {
        if (budgetDAO == null) {
            budgetDAO = new BudgetDAO(getConnectionSource(), Budget.class);
        }
        return budgetDAO;
    }

    public PayeeDAO getPayeeDAO() throws SQLException {
        if (payeeDAO == null) {
            payeeDAO = new PayeeDAO(getConnectionSource(), Payee.class);
        }
        return payeeDAO;
    }

}
