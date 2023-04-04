package com.example.qrky;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *  A Fragment that displays a list of CardData objects in a RecyclerView, with search functionality.
 *  @author Aaron Binoy
 *  @version 1.0
 */
public class libraryFragment extends Fragment {
    private int totalScore = 0;

    RecyclerView recyclerView;
    CardAdapter adapter;
    TextView totalCodes;
    private int title = 1;
    private int score = 1;
    List<CardData> allCards;
    List<CardData> filteredCards;
    SearchView searchView;

    public libraryFragment() {
        // Required empty public constructor
    }

    /**
     *     Inflates the layout for the fragment and initializes the RecyclerView, adapter, and search view.
     *
     *      Retrieves data from Firebase and generates a list of CardData objects to display in the RecyclerView.
     *
     *      @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     *
     *      @param container The parent view that the fragment UI should be attached to
     *
     *      @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     *      @return The View for the fragment's UI, or null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        totalCodes = view.findViewById(R.id.total_codes);

        recyclerView = view.findViewById(R.id.normal_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        filteredCards = new ArrayList<>();
        adapter = new CardAdapter(filteredCards, false);
        recyclerView.setAdapter(adapter);
        Button scoreButton = view.findViewById(R.id.score_button);
        Button title_sort = view.findViewById(R.id.title_sort);
        searchView = view.findViewById(R.id.search_view);
        setPrettyFont();

        Button profileButton =  view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewProfile = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(viewProfile);

            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.sortCards(3);
            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scoreButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_background, null));
                title_sort.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_background, null));
                title_sort.setText("Title");
                if (score == 1) {
                    adapter.sortCards(3);
                    scoreButton.setText("Score (>)");
                }
                else {
                    adapter.sortCards(2);
                    scoreButton.setText("Score (<)");
                }
                score *= -1;
            }
        });

        title_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scoreButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_background, null));
                title_sort.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_background, null));

                scoreButton.setText("Score");
                if (title == 1) {
                    adapter.sortCards(1);
                    title_sort.setText("Title (Z-A)");
                }
                else {
                    adapter.sortCards(0);
                    title_sort.setText("Title (A-Z)");
                }
                title *= -1;
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
// Retrieve data from the Firebase database
        db.collection("Players").document(MainActivity.getuName()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("score", totalScore);
                        db.collection("Players").document(MainActivity.getuName()).update(data);
                        totalCodes.setText("Total Codes: " + Objects.requireNonNull(document.get("totalCodes")));



                        // set the text of the totalPoints TextView here, after the total score has been calculated
                        TextView totalPoints = view.findViewById(R.id.total_points);
                        totalPoints.setText("Full Score: " + Objects.requireNonNull(document.get("score")));
                    }
                }
            }
        });
        db.collection("QR Codes")
                .whereArrayContains("playerID", MainActivity.getuName())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Generate the list of cards with name and score fields
                        List<CardData> cards = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String name = (String) document.get("name");
                            Long scoreLong = document.getLong("score");
                            Integer score = (scoreLong != null) ? scoreLong.intValue() : null;
                            String hash = (String) document.getId();

                            // Only add the card to the list if the playerID field contains "playerID1"
                            List<String> playerIds = (List<String>) document.get("playerID");
                            if (name != null && score != null && playerIds != null && playerIds.contains(MainActivity.getuName())) {
                                cards.add(new CardData(name, score, hash));
                                totalScore += score;
                            }
                        }

                        allCards = cards;
                        filteredCards.addAll(cards);
                        adapter.notifyDataSetChanged();

                    }

                });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCards(newText);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        TextView totalPoints = view.findViewById(R.id.total_points);
        totalPoints.setText("Full Score: " + totalScore);

        return view;
    }
    /**
     * Adds a CardData object to the list of all cards.
     *
     * @param card The CardData object to add to the list.
     */
    public void addToAllCards(CardData card) {
        allCards.add(card);
    }
    /**
     * Adds a CardData object to the list of filtered cards.
     *
     * @param card The CardData object to add to the list.
     */
    public void addToFilteredCards(CardData card) {
        filteredCards.add(card);
    }
    /**
     * Filters the list of all cards based on the given query string, and populates the
     * list of filtered cards with the matching cards.
     *
     * @param query The query string to filter the cards by.
     */
    void filterCards(String query) {
        filteredCards.clear();
        if (allCards == null || allCards.isEmpty()) {
            // handle case where allCards is null or empty
        } else if (query.isEmpty()) {
            filteredCards.addAll(allCards);
        } else {
            for (CardData card : allCards) {
                if (card.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredCards.add(card);
                }
            }
        }
        totalCodes.setText("Total Codes: " + filteredCards.size());
    }


    /**
     * Sets the font of the search bar to Josefin Sans Semibold.
     *
     * @since 2.0
     */
    private void setPrettyFont() {
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(id);
        Typeface myCustomFont = ResourcesCompat.getFont(requireActivity(), R.font.josefin_sans_semibold);
        searchText.setTypeface(myCustomFont);
    }
}

