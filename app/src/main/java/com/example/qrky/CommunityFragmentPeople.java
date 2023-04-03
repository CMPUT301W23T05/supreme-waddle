package com.example.qrky;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Objects;

/**
 * Displays an ordered list of all players and their scores, and lets the user
 * search for a specific player.
 *
 * @author Franco Bonilla
 * @version 2.0 2023/04/01
 * @see CommunityFragment
 * @see CommunityAdapter
 */
public class CommunityFragmentPeople extends Fragment {
    public FirebaseFirestore qrkyDB;
    public CollectionReference playersCollection;
    CommunityAdapter commAdapter;
    //  private OtherUsersCodesViewModel otherUsersCodesVM;
    SearchView playersSearch;
    RecyclerView playersBriefList;
    HashMap<String, String> playerAndScore = new HashMap<>();  // username and score
    HashMap<String, String> matchingPlayerAndScore = new HashMap<>();  // username and score that match the search query
    public HashMap<String, String> playerAndRank;  // username and rank

    /**
     * Constructor (empty) for CommunityFragment.
     *
     * @since 1.0
     */
    public CommunityFragmentPeople() {
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
        final View view = inflater.inflate(R.layout.fragment_community_people, container, false);
//        otherUsersCodesVM = new ViewModelProvider(requireActivity()).get(OtherUsersCodesViewModel.class);

        qrkyDB = FirebaseFirestore.getInstance();
        playersCollection = qrkyDB.collection("Players");
        playerAndRank = storeRanks();

        // get the search bar
        playersSearch = view.findViewById(R.id.playersSearch);
        setPrettyFont();
        searchAPlayer();


        // make list of all players and scores
        // - set up adapter
        commAdapter = new CommunityAdapter(matchingPlayerAndScore, playerAndRank);
        playersBriefList = view.findViewById(R.id.community_codes_list);
        playersBriefList.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        getAllPlayersAndScores();
        playersBriefList.setAdapter(commAdapter);
        commAdapter.update(matchingPlayerAndScore, playerAndRank);
        matchingPlayerAndScore.putAll(playerAndScore);
        commAdapter.notifyDataSetChanged();

        playersBriefList.addOnItemTouchListener(new RecyclerItemClickListener(requireActivity(), playersBriefList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(requireActivity(), UserProfileActivity.class);
                intent.putExtra("username", commAdapter.getItem(position));
                intent.putExtra("viewOther", true);
                startActivity(intent);
            }
            @Override
            public void onLongItemClick(View view, int position) {}
        }));

//        // TODO: move this button to OtherUsers
//        Button OtherUsersCodesButton = view.findViewById(R.id.SeeOtherUserCodes);
//        String otherUserUsername = "ToBFrank";  // given by OtherUser
//        OtherUsersCodesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment = new OtherUsersCodes(otherUserUsername);
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.nav_fragment, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });

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
                    try {
                        playerAndScore.put(doc.getId(), Objects.requireNonNull(doc.get("score")).toString());
                    } catch (NullPointerException e) {
                        playerAndScore.put(doc.getId(), "0");
                    }

                }
                commAdapter.update(playerAndScore, playerAndRank);
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
                commAdapter.update(matchingPlayerAndScore, playerAndRank);
                return false;
            }
        });
    }

    public HashMap<String, String> storeRanks() {
        // TODO: store ranks in database
        // Rank players starting from 1
        // give tied players the same rank

        HashMap<String, String> playerAndRank = new HashMap<>();
        playersCollection.orderBy("score", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int rank = 1;
                    int prevScore = -1;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int score = Integer.parseInt(document.get("score").toString());
                        if (score != prevScore && prevScore != -1) {
                            rank++;
                        }
                        prevScore = score;
                        playerAndRank.put(document.getId(), String.valueOf(rank));
                        playersCollection.document(document.getId()).update("rank", rank);
                    }
                } else {
                    Log.d("CommunityFragment", "Error getting documents: ", task.getException());
                }
                commAdapter.notifyDataSetChanged();
            }
        });
        return playerAndRank;
    }
}