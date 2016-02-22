package com.devmoroz.moneyme.dao;

import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

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
        queryBuilder.where().eq("category_id",  UUID.fromString(category)).and().between("dateAdded", new Date(start), new Date(end));
        String[] result = queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
        if (result != null && result.length >= 1 && result[0]!=null) {
            val = Float.parseFloat(result[0]);
        }

        return val;
    }

    public float getTotalIncomeForAccountForPeriod(String accountId, long start, long end) throws SQLException {
        float val = 0f;

        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.selectRaw("SUM(amount)");
        queryBuilder.where().eq("account_id", UUID.fromString(accountId)).and().eq("type", TransactionType.INCOME).and().between("dateAdded", new Date(start), new Date(end));
        String[] result = queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
        if (result != null && result.length >= 1 && result[0]!=null) {
            val = Float.parseFloat(result[0]);
        }

        return val;
    }

    public double getTotalOutcomeForAccount(String accountId) throws SQLException {
        double val = 0;

        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.selectRaw("SUM(amount)");
        queryBuilder.where().eq("account_id", UUID.fromString(accountId)).and().eq("type", TransactionType.OUTCOME);
        String[] result = queryRaw(queryBuilder.prepareStatementString()).getFirstResult();
        if (result != null && result.length >= 1 && result[0]!=null) {
            val = Double.parseDouble(result[0]);
        }

        return val;
    }

    public List<Transaction> queryTransactionsByTypeForAccount(TransactionType type, String accountId) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();
        queryBuilder.where().eq("account_id", UUID.fromString(accountId)).and().eq("type", type);
        queryBuilder.orderBy("dateAdded", true);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryAllForCurrentMonth(int monthStart) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();

        Calendar dateStart = Calendar.getInstance();
        dateStart.set(Calendar.DAY_OF_MONTH, monthStart);

        Calendar dateEnd = Calendar.getInstance();
        dateEnd.set(Calendar.DAY_OF_MONTH, monthStart);
        dateEnd.add(Calendar.MONTH, 1);

        queryBuilder.where().between("dateAdded", dateStart.getTime(), dateEnd.getTime());
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryByTypeForCurrentMonth(int monthStart, TransactionType type) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();

        Calendar dateStart = Calendar.getInstance();
        dateStart.set(Calendar.DAY_OF_MONTH, monthStart);

        Calendar dateEnd = Calendar.getInstance();
        dateEnd.set(Calendar.DAY_OF_MONTH, monthStart);
        dateEnd.add(Calendar.MONTH, 1);

        queryBuilder.where().between("dateAdded", dateStart.getTime(), dateEnd.getTime()).and().eq("type", type);

        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryByTypeForPeriod(int period, int monthStart, TransactionType type) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();

        Calendar currentDate = Calendar.getInstance();

        Calendar dateStart = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();

        if (currentDate.get(Calendar.DAY_OF_MONTH) == monthStart) {
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        } else {
            dateStart.set(Calendar.DAY_OF_MONTH, monthStart);
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        }
        dateEnd.add(Calendar.DAY_OF_MONTH, 1);

        switch (period) {
            case 1:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 2:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -2);
                } else {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 3:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -3);
                } else {
                    dateStart.add(Calendar.MONTH, -2);
                }
                break;
            case 6:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -6);
                } else {
                    dateStart.add(Calendar.MONTH, -5);
                }
                break;
            case 12:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -12);
                } else {
                    dateStart.add(Calendar.MONTH, -11);
                }
                break;
            case 0:
                return queryForAll();
        }

        queryBuilder.where().between("dateAdded", dateStart.getTime(), dateEnd.getTime()).and().eq("type", type);
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }

    public List<Transaction> queryAllForPeriod(int period, int monthStart) throws SQLException {
        QueryBuilder<Transaction, UUID> queryBuilder = queryBuilder();

        Calendar currentDate = Calendar.getInstance();

        Calendar dateStart = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();

        if (currentDate.get(Calendar.DAY_OF_MONTH) == monthStart) {
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        } else {
            dateStart.set(Calendar.DAY_OF_MONTH, monthStart);
            dateStart.add(Calendar.DAY_OF_MONTH, -1);
        }
        dateEnd.add(Calendar.DAY_OF_MONTH, 1);

        switch (period) {
            case 1:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 2:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -2);
                } else {
                    dateStart.add(Calendar.MONTH, -1);
                }
                break;
            case 3:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -3);
                } else {
                    dateStart.add(Calendar.MONTH, -2);
                }
                break;
            case 6:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -6);
                } else {
                    dateStart.add(Calendar.MONTH, -5);
                }
                break;
            case 12:
                if (currentDate.get(Calendar.DAY_OF_MONTH) < monthStart) {
                    dateStart.add(Calendar.MONTH, -12);
                } else {
                    dateStart.add(Calendar.MONTH, -11);
                }
                break;
            case 0:
                return queryForAll();
        }

        queryBuilder.where().between("dateAdded", dateStart.getTime(), dateEnd.getTime());
        PreparedQuery<Transaction> preparedQuery = queryBuilder.prepare();
        List<Transaction> result = query(preparedQuery);

        return result;
    }
}
