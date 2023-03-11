package com.example.qrky;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardData> cards;

    public CardAdapter(List<CardData> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardData card = cards.get(position);
        holder.title.setText(card.getTitle());
        holder.score.setText(String.valueOf(card.getScore()));

            }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView score;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            score = itemView.findViewById(R.id.score);
            itemView.findViewById(R.id.button_delete);


        }
    }

    private void removeAt(int position) {
        cards.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cards.size());
    }

}


