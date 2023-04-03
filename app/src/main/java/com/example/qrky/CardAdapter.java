package com.example.qrky;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
/**
 * The CardAdapter class is an adapter for the RecyclerView that displays a list of CardData objects.
 * It creates and binds views for CardData objects, and provides methods for adding new cards and deleting existing cards.The class contains a private List of CardData objects to be displayed in the RecyclerView, and a FirebaseFirestore instance for interacting with the Firebase Firestore database.
 * The class extends RecyclerView.Adapter and implements the necessary methods for creating, binding, and updating views for CardData objects.
 * The class also includes a nested CardViewHolder class, which extends RecyclerView.ViewHolder and represents a single view for a CardData object. This class sets up and handles click events for the delete button on each CardData view.
 * The addCard method is used to add a new CardData object to the list of filteredCards and update the RecyclerView.
 * @author Aaron Binoy
 * @version 1.0
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardData> filteredCards;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Boolean otherUser;
    private final Executor executor = Executors.newFixedThreadPool(4);

    /**
     * Constructor for the CardAdapter class. Initializes the filteredCards list with the provided list and gets an instance of the FirebaseFirestore class.
     * @param filteredCards The list of CardData objects to be displayed in the RecyclerView.
     */
    public CardAdapter(List<CardData> filteredCards, Boolean otherUser) {
        this.otherUser = otherUser;
        this.filteredCards = filteredCards;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        setHasStableIds(true);
    }


    /**
     * Loads face images from Firebase Storage and displays them in the RecyclerView.
     * @param title The title of the card to be displayed.
     * @param imageView The ImageView to display the image in.
     */

    private void loadImageByTitle(String title, ImageView imageView) {
        int index = generateIndexFromTitle(title);
    }

    /**
     * Gets id for the QR Code at position.
     * @param position Adapter position to query
     * @return
     */
    @Override
    public long getItemId(int position) {
        return filteredCards.get(position).hashCode();
    }

    /**
     * Loads face images from Firebase Storage and displays them in the RecyclerView.
     * @param title The title of the card to be displayed.
     * @param holder The CardViewHolder to display the image in.
     */
    private void loadImageByTitle(String title, CardViewHolder holder) {
        int index = generateIndexFromTitle(title);
        holder.progressBar.setVisibility(View.VISIBLE);
        StorageReference cartoonsetRef = storage.getReference().child("cartoonset");
        cartoonsetRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        List<StorageReference> items = listResult.getItems();
                        Log.d("CardAdapter", "Number of items found: " + items.size());
                        if (index >= 0 && index < items.size()) {
                            // Get the image reference
                            StorageReference imageRef = items.get(index);

                            // Load the image into the ImageView
                            GlideApp.with(holder.itemView.getContext())
                                    .load(imageRef)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            holder.progressBar.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            holder.progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(holder.roboticFace);

                            // Save the image path to the Firebase document
                            imageRef.getPath();
                            saveImagePathToFirebase(title, imageRef.getPath());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
    }


    /**
     * Saves the face image path to the Firebase document.
     * @param title The title of the card.
     * @param imagePath The path of the face image.
     */
    private void saveImagePathToFirebase(String title, String imagePath) {
        executor.execute(() -> {
        db.collection("QR Codes")
                .whereEqualTo("name", title)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().update("facepath", imagePath)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("CardAdapter", "Successfully updated facepath.");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("CardAdapter", "Error updating facepath: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("CardAdapter", "Error getting document: " + e.getMessage());
                    }
                });
        });
    }

    /** get an index for the image based on the title
     * @param title title of the card
     * @return index of face image to use
     */

    private int generateIndexFromTitle(String title) {
        int index = 0;
        for (int i = 0; i < title.length(); i++) {
            index += title.charAt(i);
        }
        return Math.abs(index) % 2427;
    }
    /**
     * public int getItemCount()
     * Gets the number of items in the filteredCards list.
     * @return The number of items in the filteredCards list.
     */
    @Override
    public int getItemCount() {
        return filteredCards.size();
    }

    /**
     * public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
     * Creates a new CardViewHolder object that represents a single item in the RecyclerView.
     * @param parent The ViewGroup that the new CardViewHolder will be added to after it is created.
     * @param viewType An integer that represents the type of view to be created (not used in this implementation).
     * @return A new CardViewHolder object that represents a single item in the RecyclerView.
     */
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        if (otherUser) {
            Button deleteButton = view.findViewById(R.id.button_delete);
            deleteButton.setVisibility(View.GONE);
        }

        return new CardViewHolder(view);
    }

    /**
     * public void sortCardsByScore()
     * Sorts the filteredCards list by score in descending order.
     */
    public static String getRarity(int value) {
        double percentile = (double) value / 1000.0; // assuming values are between 1 and 999
        if (percentile >= 0.95) {
            return "Ultra Rare";
        } else if (percentile >= 0.75) {
            return "Very Rare";
        } else if (percentile >= 0.5) {
            return "Rare";
        } else if (percentile >= 0.3) {
            return "Uncommon";
        } else {
            return "Common";
        }
    }


    /**
     * public void onBindViewHolder(CardViewHolder holder, int position)
     * Binds the data from a CardData object to a CardViewHolder object.
     * @param holder The CardViewHolder object that will display the data.
     * @param position The position of the CardData object in the filteredCards list.
     */
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        CardData card = filteredCards.get(position);
        holder.title.setText(card.getTitle());
        holder.score.setText(String.valueOf(card.getScore()));
        String rarity = getRarity(card.getScore());
        holder.rarity.setText(rarity);
        ProgressBar progressBar = holder.progressBar;
        int backgroundColor = ContextCompat.getColor(holder.itemView.getContext(), getBackgroundColor(rarity));
        Drawable circularBackground = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.circular_border_background);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(backgroundColor);
        gradientDrawable.setCornerRadius(16);

        Drawable[] layers = new Drawable[2];
        layers[0] = circularBackground;
        layers[1] = gradientDrawable;
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        holder.rarity.setBackground(layerDrawable);

        // Load the image based on the title of the card or from the facepath if it exists
        ImageView roboticFace = holder.roboticFace;
        if (card.getFacepath() != null && !card.getFacepath().isEmpty()) {
            StorageReference imageRef = storage.getReference().child(card.getFacepath());
            GlideApp.with(roboticFace.getContext())
                    .load(imageRef)
                    .into(roboticFace);
        } else {
            loadImageByTitle(card.getTitle(), holder);
        }
    }



/** The CardViewHolder class represents a single item in the RecyclerView. */


    public class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView roboticFace;
        TextView rarity;
        TextView title;
        TextView score;
        private Button deleteButton;
        ProgressBar progressBar;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            score = itemView.findViewById(R.id.score);
            rarity = itemView.findViewById(R.id.difficulty);
            deleteButton = itemView.findViewById(R.id.button_delete);
            progressBar = itemView.findViewById(R.id.progress_bar);
            roboticFace = itemView.findViewById(R.id.robotic_face);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * public void onClick(View view)
                 *
                 * The onClick method for the deleteButton UI element in the CardViewHolder. Deletes a player from a QR code in the Firebase database and removes the corresponding CardData object from the filteredCards list.
                 *
                 * @param view The View object that represents the UI element that was clicked.
                 */
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
                                            String playerId = MainActivity.getuName(); // the ID of the player to remove
                                            List<String> playerIds = (List<String>) documentSnapshot.get("playerID");
                                            if (playerIds != null && playerIds.contains(playerId)) {
                                                playerIds.remove(playerId);
                                                int score = Objects.requireNonNull(documentSnapshot.getLong("score")).intValue();
                                                db.collection("Players")
                                                        .document(playerId)
                                                        .update("score", FieldValue.increment(0-score));
                                                db.collection("Players")
                                                        .document(playerId)
                                                        .update("codes", FieldValue.arrayRemove(card.getHash()));
                                                db.collection("Players")
                                                        .document(playerId)
                                                        .update("totalCodes", FieldValue.increment(-1));
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CardData card = filteredCards.get(position);
                        String cardTitle = card.getTitle();
                        CardDetailsFragment fragment = CardDetailsFragment.newInstance(cardTitle);
                        fragment.show(((FragmentActivity) view.getContext()).getSupportFragmentManager(), "card_details");
                    }
                }
            });
        }

    }

    /**
     * private int getBackgroundColor(String rarity)
     * Returns the color resource ID for the given rarity.
     * @param rarity The rarity of the card.
     * @return The color resource ID for the given rarity.
     */
    private int getBackgroundColor(String rarity) {
        switch (rarity) {
            case "Ultra Rare":
                return R.color.ultra_rare;
            case "Very Rare":
                return R.color.very_rare;
            case "Rare":
                return R.color.rare;
            case "Uncommon":
                return R.color.uncommon;
            case "Common":
            default:
                return R.color.common;
        }
    }
    /**
     * public void addCard(CardData card)
     * Adds a new CardData object to the filteredCards list and notifies the RecyclerView that the data set has changed.
     * @param card The new CardData object to add to the list.
     */
    // This method can be called from the fragment after a new card is added to the database
    public void addCard(CardData card) {
        // assign rarity based on card score
        int score = card.getScore();
        double percentile = (double) score / 1000.0; // assuming values are between 1 and 999
        String rarity;
        if (percentile <= 0.01) {
            rarity = "Ultra Rare";
        } else if (percentile <= 0.15) {
            rarity = "Very Rare";
        } else if (percentile <= 0.3) {
            rarity = "Rare";
        } else if (percentile <= 0.6) {
            rarity = "Uncommon";
        } else {
            rarity = "Common";
        }

        int rareInt;
        switch (rarity) {
            case "Ultra Rare":
                rareInt = 0;
                break;
            case "Very Rare":
                rareInt = 1;
                break;
            case "Rare":
                rareInt = 2;
                break;
            case "Uncommon":
                rareInt = 3;
                break;
            case "Common":
            default:
                rareInt = 4;
                break;
        }

        card.setRarity(rareInt);
        filteredCards.add(card);
        notifyDataSetChanged();
    }

    /**
     * public void sortCards(int method)
     * Sorts the filteredCards list based on the given method.
     * @param method The method to sort the list by. 0 = sort by title ascending, 1 = sort by title descending, 2 = sort by score ascending, 3 = sort by score descending.
     */
    public void sortCards(int method) {
        switch (method) {
            case 0:
                Collections.sort(filteredCards, new Comparator<CardData>() {
                    @Override
                    public int compare(CardData card1, CardData card2) {
                        return card1.getTitle().compareTo(card2.getTitle());
                    }
                });
                break;
            case 1:
                Collections.sort(filteredCards, new Comparator<CardData>() {
                    @Override
                    public int compare(CardData card1, CardData card2) {
                        return card2.getTitle().compareTo(card1.getTitle());
                    }
                });
                break;
            case 2:
                Collections.sort(filteredCards, new Comparator<CardData>() {
                    @Override
                    public int compare(CardData card1, CardData card2) {
                        return card1.getScore() - card2.getScore();
                    }
                });
                break;
            case 3:
                Collections.sort(filteredCards, new Comparator<CardData>() {
                    @Override
                    public int compare(CardData card1, CardData card2) {
                        return card2.getScore() - card1.getScore();
                    }
                });
                break;

        }
        notifyDataSetChanged();
    }

}











