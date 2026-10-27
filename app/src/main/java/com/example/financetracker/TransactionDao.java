package com.example.financetracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    List<Transaction> getAllTransactions();

    @Insert
    void insert(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM accounts")
    List<Account> getAllAccounts();

    @Insert
    void insertAccount(Account account);

    @Update
    void updateAccount(Account account);

    @Query("SELECT id FROM accounts WHERE isSelected = 1 LIMIT 1")
    int getSelectedAccountId();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Доход' AND accountId = :accId")
    double getIncomeForAccount(int accId);

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Расход' AND accountId = :accId")
    double getExpenseForAccount(int accId);
}