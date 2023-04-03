package com.example.qrky;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Displays an ordered list of all codes and their scores.
 *
 * @author Franco Bonilla
 * @version 1.0 2023/04/01
 * @see CommunityFragment
 * @see CommunityAdapter
 */
public class CommunityFragmentQrs extends Fragment {

    FirebaseFirestore qrkyDB;
    CollectionReference codesCollection;
    CommunityAdapter commAdapter;
    RecyclerView codesBriefList;
    HashMap<String, String> codesAndScore = new HashMap<>();  // username and score
    HashMap<String, String> codeAndRank;  // username and rank

    /**
     * Constructor (empty) for CommunityFragment.
     *
     * @since 1.0
     */
    public CommunityFragmentQrs() {
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
        final View view = inflater.inflate(R.layout.fragment_community_qrs, container, false);

        qrkyDB = FirebaseFirestore.getInstance();
        codesCollection = qrkyDB.collection("QR Codes");
        codeAndRank = getRanks();

        // make list of all players and scores
        // - set up adapter
        commAdapter = new CommunityAdapter(codesAndScore, codeAndRank);
        codesBriefList = view.findViewById(R.id.community_codes_leaderboard);
        codesBriefList.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        getAllCodesAndScores();
        codesBriefList.setAdapter(commAdapter);
        commAdapter.update(codesAndScore, codeAndRank);
        codesBriefList.addOnItemTouchListener(new RecyclerItemClickListener(requireActivity(), codesBriefList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String cardTitle = commAdapter.getItem(position);
                // get code name
                CardDetailsFragment fragment = CardDetailsFragment.newInstance(cardTitle);
                fragment.show(((FragmentActivity) view.getContext()).getSupportFragmentManager(), "card_details");

            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do nothing
            }
        }));
        return view;
    }

    /**
     * Gets all players and their scores from the database and updates the adapter.
     *
     * @since 1.0
     */
    public void getAllCodesAndScores() {
        codesCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                codesAndScore.clear();  // clear list before adding new data
                assert value != null;
                for (QueryDocumentSnapshot doc: value) {
                    try {
                        codesAndScore.put(Objects.requireNonNull(doc.get("name")).toString(), Objects.requireNonNull(doc.get("score")).toString());
                    } catch (NullPointerException e) {
                        try {
                            codesAndScore.put(Objects.requireNonNull(doc.get("name")).toString(), "0");
                        } catch (NullPointerException ignored) {}
                    }

                }
                commAdapter.update(codesAndScore, codeAndRank);
            }
        });
    }

    public HashMap<String, String> getRanks() {
        // get code from database
        // rank the code
        // put code and rank in hashmap
        // return hashmap
        HashMap<String, String> codeAndRank = new HashMap<>();
        codesCollection.orderBy("score", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int rank = 1;
                assert value != null;
                for (QueryDocumentSnapshot doc: value) {
                    try {
                        codeAndRank.put(Objects.requireNonNull(doc.get("name")).toString(), String.valueOf(rank));
                        rank++;
                    } catch (NullPointerException e) {
                        try {
                            codeAndRank.put(Objects.requireNonNull(doc.get("name")).toString(), "0");
                        } catch (NullPointerException ignored) {}
                    }

                }
            }
        });
        return codeAndRank;
    }
}