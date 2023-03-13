package com.example.qrky;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
/**

 The CardAdapter class is an adapter for the RecyclerView that displays a list of CardData objects.
 It creates and binds views for CardData objects, and provides methods for adding new cards and deleting existing cards.
 The class contains a private List of CardData objects to be displayed in the RecyclerView, and a FirebaseFirestore instance for interacting with the Firebase Firestore database.
 The class extends RecyclerView.Adapter and implements the necessary methods for creating, binding, and updating views for CardData objects.
 The class also includes a nested CardViewHolder class, which extends RecyclerView.ViewHolder and represents a single view for a CardData object. This class sets up and handles click events for the delete button on each CardData view.
 The addCard method is used to add a new CardData object to the list of filteredCards and update the RecyclerView.
 @author Aaron Binoy
 @version 1.0

 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardData> filteredCards;
    private FirebaseFirestore db;

    public CardAdapter(List<CardData> filteredCards) {
        this.filteredCards = filteredCards;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemCount() {
        return filteredCards.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        CardData card = filteredCards.get(position);
        holder.title.setText(card.getTitle());
        holder.score.setText(String.valueOf(card.getScore())); // convert int to String
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView score;
        private Button deleteButton;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            score = itemView.findViewById(R.id.score);
            deleteButton = itemView.findViewById(R.id.button_delete);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CardData card = filteredCards.get(position);
                        String cardTitle = card.getTitle();
                        db.collection("QR Codes")
                                .whereEqualTo("name", cardTitle)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String playerId = "playerID1"; // the ID of the player to remove
                                            List<String> playerIds = (List<String>) documentSnapshot.get("playerID");
                                            if (playerIds != null && playerIds.contains(playerId)) {
                                                playerIds.remove(playerId);
                                                documentSnapshot.getReference().update("playerID", playerIds)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                filteredCards.remove(position);
                                                                notifyItemRemoved(position);
                                                                notifyItemRangeChanged(position, filteredCards.size());
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("CardAdapter", "Error deleting player: " + e.getMessage());
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("CardAdapter", "Error getting document: " + e.getMessage());
                                    }
                                });
                    }
                }
            });

        }

    }

    // This method can be called from the fragment after a new card is added to the database
    public void addCard(CardData card) {
        filteredCards.add(card);
        notifyDataSetChanged();
    }
}











