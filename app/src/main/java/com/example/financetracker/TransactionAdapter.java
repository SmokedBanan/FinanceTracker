package com.example.financetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private List<Account> accounts;

    public TransactionAdapter(List<Transaction> transactions, List<Account> accounts) {
        this.transactions = (transactions != null) ? transactions : new ArrayList<>();
        this.accounts = (accounts != null) ? accounts : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.amount.setText(transaction.amount + " ₽");
        holder.category.setText(transaction.type);

        // Ищем название счета
        String accName = "Неизвестно";
        for (Account a : accounts) {
            if (a.id == transaction.accountId) {
                accName = a.name;
                break;
            }
        }

        // Соединяем описание и счет в одну строку
        String noteText = (transaction.note != null && !transaction.note.isEmpty()) ? transaction.note : "Нет описания";
        holder.note.setText(noteText + " — [" + accName + "]");
    }

    @Override
    public int getItemCount() {
        return transactions.size(); // Исправил с list на transactions
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category, amount, note;
        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tvCategory);
            amount = itemView.findViewById(R.id.tvAmount);
            note = itemView.findViewById(R.id.tvNote);
        }
    }
}