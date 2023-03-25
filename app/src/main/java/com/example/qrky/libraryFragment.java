package com.example.qrky;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  A Fragment that displays a list of CardData objects in a RecyclerView, with search functionality.
 *  @author Aaron Binoy
 *  @version 1.0
 */
public class libraryFragment extends Fragment {

    RecyclerView recyclerView;
    CardAdapter adapter;

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

        recyclerView = view.findViewById(R.id.normal_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        filteredCards = new ArrayList<>();
        adapter = new CardAdapter(filteredCards);
        recyclerView.setAdapter(adapter);

        searchView = view.findViewById(R.id.search_view);

        Button profileButton =  view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileFragment userFragment = userProfileFragment.newInstance();
                userFragment.show(getChildFragmentManager(),"ss");
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
// Retrieve data from the Firebase database
        db.collection("QR Codes")
                .whereArrayContains("playerID", "playerID1")
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

                            // Only add the card to the list if the playerID field contains "playerID1"
                            List<String> playerIds = (List<String>) document.get("playerID");
                            if (name != null && score != null && playerIds != null && playerIds.contains("playerID1")) {
                                cards.add(new CardData(name, score));
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
    }


}

