package com.example.financetracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String type;
    public double amount;
    public String category;
    public String note;
    public long timestamp;
    public int accountId;

    public Transaction(String type, double amount, String category, String note, long timestamp, int accountId) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.timestamp = timestamp;
        this.accountId = accountId;
    }
}