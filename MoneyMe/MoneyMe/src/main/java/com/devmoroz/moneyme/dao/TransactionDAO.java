package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.devmoroz.moneyme.utils.datetime.DataInterval;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TransactionDAO extends BaseDaoImpl<Transaction, UUID> {

    public TransactionDAO(ConnectionSource connectionSource,
                          Class<Transaction> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Transaction> queryTransactionsForCategory(String category) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("category_id", UUID.fromString(category));
        queryBuilder.orderBy("dateAdded", true);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryTransactionsWithLocationForCategory(String category) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("category_id", UUID.fromString(category)).and().isNotNull("location");
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryTransactionsWithLocation() throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.where().isNotNull("location");
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public float getTotalSpendingForCategoryForPeriod(String category, long start, long end) throws SQLException {
        float val = 0f;
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.selectRaw("SUM(amount)");
        queryBuilder.where().eq("category_id", UUID.fromString(category)).and().between("dateAdded", new Date(start), new Date(end));
        String[] result = queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
        if (result != null && result.length >= 1 && result[0] != null) {
            val = Float.parseFloat(result[0]);
        }

        return val;
    }

    public float getTotalIncomeForAccountForPeriod(String accountId, long start, long end) throws SQLException {
        float val = 0f;

        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.selectRaw("SUM(amount)");
        queryBuilder.where().eq("accountTo_id", UUID.fromString(accountId)).and().eq("type", TransactionType.INCOME).and().between("dateAdded", new Date(start), new Date(end));
        String[] result = queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
        if (result != null && result.length >= 1 && result[0] != null) {
            val = Float.parseFloat(result[0]);
        }

        return val;
    }

    public double getTotalOutcomeForAccount(String accountId) throws SQLException {
        double val = 0;

        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.selectRaw("SUM(amount)");
        queryBuilder.where().eq("accountFrom_id", UUID.fromString(accountId)).and().eq("type", TransactionType.OUTCOME);
        String[] result = queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
        if (result != null && result.length >= 1 && result[0] != null) {
            val = Double.parseDouble(result[0]);
        }

        return val;
    }

    public List<Transaction> queryTransactionsByTypeForAccount(TransactionType type, String accountId) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("accountFrom_id", UUID.fromString(accountId)).or().eq("accountTo_id", UUID.fromString(accountId)).and().eq("type", type);
        queryBuilder.orderBy("dateAdded", true);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryAllForCurrentMonth(int monthStart) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();


        final Interval historyInterval = DataInterval.getHistoryInterval(System.currentTimeMillis(), 1, monthStart);
        Date start;
        Date end = new Date(historyInterval.getEndMillis());
        if(monthStart == 1){
            start = new Date(historyInterval.getStartMillis());
        }else{
            start = new Date(historyInterval.getStart().minusDays(1).getMillis());
        }

        queryBuilder.where().between("dateAdded", start, end);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryByTypeForCurrentMonth(int monthStart, TransactionType type) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();

        final Interval historyInterval = DataInterval.getHistoryInterval(System.currentTimeMillis(), 1, monthStart);
        Date start;
        Date end = new Date(historyInterval.getEndMillis());
        if(monthStart == 1){
            start = new Date(historyInterval.getStartMillis());
        }else{
            start = new Date(historyInterval.getStart().minusDays(1).getMillis());
        }

        queryBuilder.where().between("dateAdded", start, end).and().eq("type", type);

        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryByTypeForPeriod(int period, int monthStart, TransactionType type) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();

        if (period != 0) {
            final Interval historyInterval = DataInterval.getHistoryInterval(System.currentTimeMillis(), period, monthStart);
            Date start;
            Date end = new Date(historyInterval.getEndMillis());
            if(monthStart == 1){
                start = new Date(historyInterval.getStartMillis());
            }else{
                start = new Date(historyInterval.getStart().minusDays(1).getMillis());
            }

            queryBuilder.where().between("dateAdded", start, end).and().eq("type", type);
            PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
            return query(preparedQuery);
        }else{
            return queryForAll();
        }
    }

    public List<Transaction> queryAllForPeriod(int period, int monthStart) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();

        if (period != 0) {
            final Interval historyInterval = DataInterval.getHistoryInterval(System.currentTimeMillis(), period, monthStart);
            Date start;
            Date end = new Date(historyInterval.getEndMillis());
            if(monthStart == 1){
                start = new Date(historyInterval.getStartMillis());
            }else{
                start = new Date(historyInterval.getStart().minusDays(1).getMillis());
            }

            queryBuilder.where().between("dateAdded", start, end);
            PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
            return query(preparedQuery);
        }else{
            return queryForAll();
        }
    }

    public int deleteAllForAccount(String accountId) throws SQLException {
        DeleteBuilder<Transaction, UUID> deleteBuilder = deleteBuilder();
        deleteBuilder.where().eq("accountTo_id", UUID.fromString(accountId)).and().isNull("accountFrom_id");
        int inCount = deleteBuilder.delete();
        deleteBuilder.reset();
        deleteBuilder.where().eq("accountFrom_id", UUID.fromString(accountId)).and().isNull("accountTo_id");
        int outCount = deleteBuilder.delete();

        return inCount + outCount;
    }

    public boolean hasTransferTransactions(String accountId) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("accountTo_id", UUID.fromString(accountId)).and().isNotNull("accountFrom_id")
                .or().eq("accountFrom_id", UUID.fromString(accountId)).and().isNotNull("accountTo_id");
        long numRows = queryBuilder.countOf();

        return numRows > 0;
    }
}
