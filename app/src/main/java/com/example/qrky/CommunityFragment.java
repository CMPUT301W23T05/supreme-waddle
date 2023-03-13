package com.example.qrky;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * Fragment for the Community tab. Displays an ordered list of all players and their scores,
 * and lets the user search for a specific player. Currently, there is a button that takes the
 * user to a sample library of codes of another user.
 *
 * @author Franco Bonilla
 * @version 1.0 2023/03/12
 * @see CommunityAdapter
 * @see OtherUsersCodes
 */
public class CommunityFragment extends Fragment {
    FirebaseFirestore qrkyDB;
    CollectionReference playersCollection;
    CommunityAdapter commAdapter;
//  private OtherUsersCodesViewModel otherUsersCodesVM;
//  CommunityViewModel communityVM;
    SearchView playersSearch;
    RecyclerView playersBriefList;
    HashMap<String, String> playerAndScore = new HashMap<>();  // username and score
    HashMap<String, String> matchingPlayerAndScore = new HashMap<>();  // username and score that match the search query

    /**
     * Constructor (empty) for CommunityFragment.
     *
     * @since 1.0
     */
    public CommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Creates the view for the CommunityFragment.
     *
     * @param inflater The LayoutInflater object for inflating the view(s) in the fragment.
     * @param container Parent view that the fragment's UI should be attached to (if not null).
     * @param savedInstanceState Past saved state of the fragment (if not null).
     * @return View for the CommunityFragment
     * @since 1.0
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_community, container, false);
//        otherUsersCodesVM = new ViewModelProvider(requireActivity()).get(OtherUsersCodesViewModel.class);

        qrkyDB = FirebaseFirestore.getInstance();
        playersCollection = qrkyDB.collection("Players");

        // make list of all players and scores
        // - set up adapter
        commAdapter = new CommunityAdapter(matchingPlayerAndScore);
        playersBriefList = view.findViewById(R.id.community_codes_list);
        playersBriefList.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        getAllPlayersAndScores();
        playersBriefList.setAdapter(commAdapter);
        matchingPlayerAndScore.putAll(playerAndScore);
        commAdapter.update(matchingPlayerAndScore);

        // get the search bar
        playersSearch = view.findViewById(R.id.playersSearch);
        setPrettyFont();
        searchAPlayer();


        // TODO: move this button to OtherUsers
        Button OtherUsersCodesButton = view.findViewById(R.id.SeeOtherUserCodes);
        OtherUsersCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new OtherUsersCodes();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    /**
     * Sets the font of the search bar to Josefin Sans Semibold.
     *
     * @since 1.0
     */
    private void setPrettyFont() {
        int id = playersSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = playersSearch.findViewById(id);
        Typeface myCustomFont = ResourcesCompat.getFont(requireActivity(), R.font.josefin_sans_semibold);
        searchText.setTypeface(myCustomFont);
    }

    /**
     * Gets all players and their scores from the database and updates the adapter.
     *
     * @since 1.0
     */
    public void getAllPlayersAndScores() {
        playersCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                playerAndScore.clear();  // clear list before adding new data
                assert value != null;
                for (QueryDocumentSnapshot doc: value) {
                    playerAndScore.put(doc.getString("username"), doc.getString("score"));
                }
                commAdapter.update(playerAndScore);
            }
        });
//        Log.i("CommunityFragment", "All players and scores: " + playerAndScore.toString());
    }

    /**
     * Searches for a player in the leaderboard and updates the adapter to match the search query.
     *
     * @since 1.0
     */
    public void searchAPlayer() {
        playersSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                matchingPlayerAndScore.clear();
                if (newText.isEmpty()) {
                    matchingPlayerAndScore.putAll(playerAndScore);
                } else {
                    for (String player: playerAndScore.keySet()) {
                        if (player.toLowerCase().contains(newText.toLowerCase())) {
                            matchingPlayerAndScore.put(player, playerAndScore.get(player));
                        }
                    }
                }
//                Log.i("CommunityFragment", "Search query: " + newText);
//                Log.i("CommunityFragment", "Matching players: " + matchingPlayerAndScore.toString());
                commAdapter.update(matchingPlayerAndScore);
                return false;
            }
        });
    }
}