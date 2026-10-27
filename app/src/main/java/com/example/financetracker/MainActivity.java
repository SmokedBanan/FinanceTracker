package com.example.financetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextView tvBalance, tvAccountsCount;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "finance_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        tvBalance = findViewById(R.id.tv_main_balance);
        tvAccountsCount = findViewById(R.id.tv_accounts_count); // Убедись, что этот ID есть в XML
        pieChart = findViewById(R.id.pieChart);

        checkAndCreateDefaultAccounts();
        updateUI();

        findViewById(R.id.cardExpense).setOnClickListener(v -> showTransactionDialog("Расход"));
        findViewById(R.id.cardIncome).setOnClickListener(v -> showTransactionDialog("Доход"));
        findViewById(R.id.cardAccounts).setOnClickListener(v -> showAccountsDialog());
        findViewById(R.id.cardHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
    }

    private void checkAndCreateDefaultAccounts() {
        List<Account> accounts = db.transactionDao().getAllAccounts();
        if (accounts.isEmpty()) {
            db.transactionDao().insertAccount(new Account("Наличные", 0.0, true));
            db.transactionDao().insertAccount(new Account("Карта", 0.0, false));
        }
    }

    private void updateUI() {
        List<Account> accounts = db.transactionDao().getAllAccounts();
        double totalBalance = 0;
        int activeId = -1;

        for (Account acc : accounts) {
            double inc = db.transactionDao().getIncomeForAccount(acc.id);
            double exp = db.transactionDao().getExpenseForAccount(acc.id);
            totalBalance += (acc.balance + inc - exp);
            if (acc.isSelected) activeId = acc.id;
        }

        tvBalance.setText(String.format("%.2f ₽", totalBalance));
        if (tvAccountsCount != null) {
            tvAccountsCount.setText(accounts.size() + " счетов");
        }

        if (activeId != -1) {
            setupChart(db.transactionDao().getIncomeForAccount(activeId),
                    db.transactionDao().getExpenseForAccount(activeId));
        }
    }

    private void showAccountsDialog() {
        List<Account> accounts = db.transactionDao().getAllAccounts();
        String[] items = new String[accounts.size() + 1];

        for (int i = 0; i < accounts.size(); i++) {
            items[i] = accounts.get(i).name + (accounts.get(i).isSelected ? " [ВЫБРАНО]" : "");
        }
        items[accounts.size()] = "+ Добавить новый счет";

        new AlertDialog.Builder(this)
                .setTitle("Управление счетами")
                .setItems(items, (dialog, which) -> {
                    if (which == accounts.size()) {
                        showAddAccountDialog();
                    } else {
                        for (int i = 0; i < accounts.size(); i++) {
                            Account a = accounts.get(i);
                            a.isSelected = (i == which);
                            db.transactionDao().updateAccount(a);
                        }
                        updateUI();
                    }
                }).show();
    }

    private void showAddAccountDialog() {
        EditText input = new EditText(this);
        input.setHint("Название счета");
        new AlertDialog.Builder(this)
                .setTitle("Новый счет")
                .setView(input)
                .setPositiveButton("Создать", (d, w) -> {
                    String name = input.getText().toString();
                    if (!name.isEmpty()) {
                        db.transactionDao().insertAccount(new Account(name, 0.0, false));
                        updateUI();
                    }
                }).show();
    }

    private void showTransactionDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(type);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 0);

        final EditText amountIn = new EditText(this);
        amountIn.setHint("Сумма");
        amountIn.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(amountIn);

        final EditText noteIn = new EditText(this);
        noteIn.setHint("Описание");
        layout.addView(noteIn);

        builder.setView(layout);
        builder.setPositiveButton("ОК", (d, w) -> {
            String s = amountIn.getText().toString();
            if (!s.isEmpty()) {
                int activeId = db.transactionDao().getSelectedAccountId();
                Transaction t = new Transaction(type, Double.parseDouble(s), "Общее",
                        noteIn.getText().toString(), System.currentTimeMillis(), activeId);
                db.transactionDao().insert(t);
                updateUI();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void setupChart(double inc, double exp) {
        if (pieChart == null) return;
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (inc > 0) entries.add(new PieEntry((float) inc, "Доход"));
        if (exp > 0) entries.add(new PieEntry((float) exp, "Расход"));
        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(new int[]{Color.GREEN, Color.RED});
        pieChart.setData(new PieData(ds));
        pieChart.invalidate();
    }

    @Override
    protected void onResume() { super.onResume(); updateUI(); }
}