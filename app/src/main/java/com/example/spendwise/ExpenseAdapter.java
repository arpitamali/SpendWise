package com.example.spendwise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter
        extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    ArrayList<ExpenseModel> list;
    OnItemLongClickListener listener;

    public ExpenseAdapter(ArrayList<ExpenseModel> list,
                          OnItemLongClickListener listener) {

        this.list = list;

        this.listener = listener;
    }
    public interface OnItemLongClickListener {

        void onLongClick(int position);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_expense,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        ExpenseModel model = list.get(position);

        holder.tvTitle.setText(
                model.getTitle()
        );

        holder.tvAmount.setText(
                "- ₹" + model.getAmount()
        );

        holder.tvDate.setText(
                model.getDate()
        );

        // Emoji Based on Category

        String category =
                model.getCategory();

        if(category.contains("Food")) {

            holder.tvEmoji.setText("🍔");

        }
        else if(category.contains("Travel")) {

            holder.tvEmoji.setText("🚗");

        }
        else if(category.contains("Shopping")) {

            holder.tvEmoji.setText("🛍");

        }
        else if(category.contains("Bills")) {

            holder.tvEmoji.setText("💡");

        }
        else if(category.contains("Health")) {

            holder.tvEmoji.setText("🏥");

        }
        else if(category.contains("Entertainment")) {

            holder.tvEmoji.setText("🎬");

        }
        else {

            holder.tvEmoji.setText("📦");

        }
        holder.itemView.setOnLongClickListener(v -> {

            listener.onLongClick(position);

            return true;

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTitle,
                tvAmount,
                tvDate,
                tvEmoji;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle =
                    itemView.findViewById(R.id.tvTitle);

            tvAmount =
                    itemView.findViewById(R.id.tvAmount);

            tvDate =
                    itemView.findViewById(R.id.tvDate);

            tvEmoji =
                    itemView.findViewById(R.id.tvEmoji);
        }
    }
}