package com.devmoroz.moneyme.utils;

import com.devmoroz.moneyme.MoneyApplication;
import com.devmoroz.moneyme.helpers.DBHelper;
import com.devmoroz.moneyme.models.Account;
import com.devmoroz.moneyme.models.Currency;
import com.devmoroz.moneyme.models.Goal;
import com.devmoroz.moneyme.models.Transaction;
import com.devmoroz.moneyme.models.TransactionType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CommonUtils {

    public void sortWalletEntriesByDate(List<Transaction> inout, boolean desc) {
        if (desc) {
            Collections.sort(inout, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction lhs, Transaction rhs) {
                    Date lhsDate = lhs.getDateAdded();
                    Date rhsDate = rhs.getDateAdded();
                    if (lhsDate != null && rhsDate != null) {
                        return rhsDate.compareTo(lhsDate);
                    } else {
                        return 0;
                    }
                }
            });
        } else {
            Collections.sort(inout, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction lhs, Transaction rhs) {
                    Date lhsDate = lhs.getDateAdded();
                    Date rhsDate = rhs.getDateAdded();
                    if (lhsDate != null && rhsDate != null) {
                        return lhsDate.compareTo(rhsDate);
                    } else {
                        return 0;
                    }
                }
            });
        }

    }

    public String[] getTotalInOut(List<Transaction> inout, Currency currency) {
        if (inout != null && !inout.isEmpty()) {
            double totalIn = 0f;
            double totalOut = 0f;
            for (Transaction io : inout) {
                if (io.getType() == TransactionType.INCOME) {
                    totalIn += io.getAmount();
                } else {
                    totalOut += io.getAmount();
                }
            }
            return new String[]{FormatUtils.amountToString(currency, totalIn), FormatUtils.amountToString(currency, totalOut)};
        }
        return new String[]{"0.00", "0.00"};
    }

    public static int deleteItem(String id, TransactionType type) {
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        try {
            Transaction transaction = dbHelper.getTransactionDAO().queryForId(UUID.fromString(id));
            Account account = transaction.getAccount();
            if (type == TransactionType.INCOME) {
                account.setBalance(account.getBalance() - transaction.getAmount());
            } else {
                account.setBalance(account.getBalance() + transaction.getAmount());
            }
            dbHelper.getAccountDAO().update(account);

            return dbHelper.getTransactionDAO().delete(transaction);
        } catch (SQLException ex) {
            return 9999999;
        }
    }

    public static int deleteGoal(String id) {
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        try {
            return dbHelper.getGoalDAO().deleteById(UUID.fromString(id));
        } catch (SQLException ex) {
            return -1;
        }
    }

    public static int updateGoal(String id, int amount) {
        DBHelper dbHelper = MoneyApplication.getInstance().GetDBHelper();
        try {
            Goal goal = dbHelper.getGoalDAO().queryForId(UUID.fromString(id));
            goal.setAccumulated(goal.getAccumulated() + amount);
            return dbHelper.getGoalDAO().update(goal);
        } catch (SQLException ex) {
            return -1;
        }
    }
}
