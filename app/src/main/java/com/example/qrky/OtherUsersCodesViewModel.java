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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// US 02.03.01: As a player, I want to be able to browse QR codes that other players have scanned.
// Artist: Franco Bonilla
// Actors: Player (primary)
// Goal: Player sees the code library of other players
// Trigger: Player clicks on an OtherUser
// Precondition: N/A
// Postcondition: Player sees OtherUser's code library
// Basic Flow:
//     1. Player clicks on an OtherUser (trigger)
//     2. System gets code library from database
//     3. System makes card for each code in library
//     4. System displays cards in a grid (3 columns)
//     5. Player sees OtherUser's code library (goal)
public class OtherUsersCodesViewModel extends ViewModel {
    // - currently no ordering of codes; order chronologically for part 4
    //   - maybe use a hashmap to store codes and their timestamps

    FirebaseFirestore qrkyDB = FirebaseFirestore.getInstance();
    List<String> codeNames = new ArrayList<>();
    List<Integer> codeScores = new ArrayList<>();
    List<List<String>> codeDrawings = new ArrayList<>();  // 0 = eyes, 1 = nose, 2 = mouth
    String otherUsername;  // username of other user
    List<String> playerHashes = new ArrayList<>();  // list of hashes from other user

    // TODO: get code library from database
    // get username from community fragment
    // get hashes from database using username
    public void getOtherUsersHashes() {
        CollectionReference playersCollection = qrkyDB.collection("Players");

        playersCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // clear list before adding new data
                playerHashes.clear();

                // get list of hashes from database
                assert value != null;
                for (QueryDocumentSnapshot doc: value) {
                    if (Objects.equals(doc.getString("username"), otherUsername)) {
                        playerHashes = (List<String>) doc.get("codes");
                        break;
                    }
                }
            }
        });

        if (playerHashes.isEmpty()) {
            Log.i("OtherUsersCodesVM", "No hashes found");
        } else {
            Log.i("OtherUsersCodesVM", "Hashes found!");
        }
    }
    // for each hash, get code from database
    public void getOtherUsersCodes() {
        getOtherUsersHashes();
        CollectionReference qrCodesCollection = qrkyDB.collection("QR Codes");

        for (String hash: playerHashes) {
            qrCodesCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    // clear lists before adding new data
                    codeNames.clear();
                    codeScores.clear();
                    codeDrawings.clear();

                    // add new data
                    assert value != null;
                    for (QueryDocumentSnapshot doc: value) {
                        if (doc.getId().equals(hash)) {
                            codeNames.add(doc.getString("name"));
                            codeScores.add(Objects.requireNonNull(doc.getLong("score")).intValue());
                            List<String> codeDrawing = new ArrayList<>();
                            codeDrawing.add(doc.getString("eyes"));
                            codeDrawing.add(doc.getString("nose"));
                            codeDrawing.add(doc.getString("mouth"));
                            codeDrawings.add(codeDrawing);
                        }
                    }
                }
            });
        }

        if (codeNames.size() > 3) {
            Log.i("OtherUsersCodesVM", "Real data added!");
            Log.i("OtherUsersCodesVM", "Code names: " + codeNames);
        } else {
            Log.i("OtherUsersCodesVM", "No codes found");
        }
    }

    // TODO: Move to testing class
    public void getTestData() {
        codeNames.clear();
        codeScores.clear();
        codeDrawings.clear();

        codeNames.add("CookieMonster");
        codeScores.add(100);
        List<String> codeDrawing = new ArrayList<String>();
        codeDrawing.add("U     U");
        codeDrawing.add("   ~   ");
        codeDrawing.add("  ___  ");
        codeDrawings.add(codeDrawing);


        codeNames.add("Elmo");
        codeScores.add(340);
        List<String> codeDrawing2 = new ArrayList<String>();
        codeDrawing2.add(">     <");
        codeDrawing2.add("   -   ");
        codeDrawing2.add("  UUU  ");
        codeDrawings.add(codeDrawing2);

        codeNames.add("DiegoZombie");
        codeScores.add(8);
        List<String> codeDrawing3 = new ArrayList<String>();
        codeDrawing3.add("T     T");
        codeDrawing3.add("   O   ");
        codeDrawing3.add("  MWM  ");
        codeDrawings.add(codeDrawing3);

        Log.i("OtherUsersCodesViewModel", "Test data added!");
    }

}