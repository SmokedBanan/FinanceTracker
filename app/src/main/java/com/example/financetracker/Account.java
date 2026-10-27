package com.example.financetracker;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double balance;
    public boolean isSelected; // Вот это поле обязательно!

    public Account(String name, double balance, boolean isSelected) {
        this.name = name;
        this.balance = balance;
        this.isSelected = isSelected;
    }
}