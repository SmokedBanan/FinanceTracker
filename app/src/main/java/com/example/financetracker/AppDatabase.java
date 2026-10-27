package com.example.financetracker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class, Account.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
}