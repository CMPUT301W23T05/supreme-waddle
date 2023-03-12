package com.example.qrky;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class CommunityViewModel extends ViewModel {
    FirebaseFirestore qrkyDB = FirebaseFirestore.getInstance();
    CollectionReference playersCollection = qrkyDB.collection("Players");

    HashMap<String, String> playerAndScore = new HashMap<>();  // username and score

    public void getPlayersAndScores() {
        playerAndScore.clear();  // clear list before adding new data
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
        Log.i("CommunityViewModel", "Player and scores: " + playerAndScore);

    }
}
