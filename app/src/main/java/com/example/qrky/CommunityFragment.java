package com.example.qrky;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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

// TODO: Fix data getting
// - For some reason, data only shows up after leaving the fragment and coming back???
public class CommunityFragment extends Fragment {
    private FirebaseFirestore qrkyDB = FirebaseFirestore.getInstance();
    private CollectionReference playersCollection = qrkyDB.collection("Players");
    private CommunityAdapter commAdapter;
//    private OtherUsersCodesViewModel otherUsersCodesVM;
//   CommunityViewModel communityVM;
    private SearchView playersSearch;
    private RecyclerView playersBriefList;
    private HashMap<String, String> playerAndScore = new HashMap<>();  // username and score
    private HashMap<String, String> matchingPlayerAndScore = new HashMap<>();  // username and score that match the search query


    public CommunityFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.i("CommunityFragment", "Looking at Community Fragment");
//        super.onCreate(savedInstanceState);
//
//        // call ViewModels
////        communityVM = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_community, container, false);
//        otherUsersCodesVM = new ViewModelProvider(requireActivity()).get(OtherUsersCodesViewModel.class);

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

    public void setPrettyFont() {
        int id = playersSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = playersSearch.findViewById(id);
        Typeface myCustomFont = ResourcesCompat.getFont(requireActivity(), R.font.josefin_sans_semibold);
        searchText.setTypeface(myCustomFont);
    }

    public void getAllPlayersAndScores() {
        playersCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                playerAndScore.clear();  // clear list before adding new data
                assert value != null;
                for (QueryDocumentSnapshot doc: value) {
                    playerAndScore.put(doc.getString("username"), doc.getString("score"));
                }
            }
        });
        Log.i("CommunityFragment", "All players and scores: " + playerAndScore.toString());
    }

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
                Log.i("CommunityFragment", "Search query: " + newText);
                Log.i("CommunityFragment", "Matching players: " + matchingPlayerAndScore.toString());
                commAdapter.update(matchingPlayerAndScore);
                return false;
            }
        });
    }
}