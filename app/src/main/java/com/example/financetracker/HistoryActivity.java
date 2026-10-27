package com.example.financetracker;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "finance_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // 1. Привязываем новый ID из твоего XML
        recyclerView = findViewById(R.id.rvHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Кнопка "Назад"
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 3. Кнопка "Очистить"
        findViewById(R.id.btnClearHistory).setOnClickListener(v -> {
            clearAllHistory();
        });

        updateHistory();

        // Удаление свайпом (оставляем, это удобно)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Transaction deletedTransaction = transactionList.get(position);
                db.transactionDao().delete(deletedTransaction);
                transactionList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(HistoryActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void updateHistory() {
        transactionList = db.transactionDao().getAllTransactions();
        List<Account> allAccounts = db.transactionDao().getAllAccounts(); // Добавили получение счетов

        adapter = new TransactionAdapter(transactionList, allAccounts);
        recyclerView.setAdapter(adapter);
    }

    // Метод для полной очистки истории
    private void clearAllHistory() {
        if (transactionList != null && !transactionList.isEmpty()) {
            for (Transaction t : transactionList) {
                db.transactionDao().delete(t);
            }
            transactionList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "История очищена", Toast.LENGTH_SHORT).show();
        }
    }
}