package com.example.qrky;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

/**
 * Fragment for the OtherUser's codes. Displays a grid of cards with the codes that the OtherUser
 * has scanned. The cards should be clickable to see more info, but that functionality is not
 * implemented yet.
 *
 * @author Franco Bonilla
 * @version 1.0 2023/03/07
 * @see OtherUsersLibraryAdapter
 */
public class OtherUsersCodes extends Fragment {
//    public OtherUsersCodesViewModel otherUsersCodeVM;
    GridView gridOfCodes;
    ImageButton backBttn;

    FirebaseFirestore qrkyDB;
    List<String> codeNames = new ArrayList<>();
    List<Integer> codeScores = new ArrayList<>();
    List<List<String>> codeDrawings = new ArrayList<>();  // 0 = eyes, 1 = nose, 2 = mouth
    String otherUsername;  // username of other user
    List<String> playerHashes = new ArrayList<>();  // list of hashes from other user

    /**
     * Constructor (empty) for OtherUsersCodes.
     *
     * @since 1.0
     */
    public OtherUsersCodes() {}

    /**
     * Creates the fragment and hides the bottom navigation bar. Calls the ViewModel to get the
     * data for the grid of cards.
     *
     * @param savedInstanceState Past saved state of the fragment (if not null).
     * @since 1.0
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("OtherUsersCodes", "Looking at OtherUser's codes");
        super.onCreate(savedInstanceState);

        // hide bottom navigation bar when viewing OtherUser's codes
        MainActivity mainAct = (MainActivity) getActivity();
        assert mainAct != null;
        mainAct.bttmNavView.setVisibility(View.INVISIBLE);

        qrkyDB = FirebaseFirestore.getInstance();

        // call ViewModel
//        otherUsersCodeVM = new ViewModelProvider(requireActivity()).get(OtherUsersCodesViewModel.class);
        getOtherUsersCodes();  // add data
        getTestData();  // add test data
    }

    /**
     * Creates the view for the fragment. Creates the grid of cards with the OtherUser's codes.
     *
     * @param inflater inflater for the fragment.
     * @param container The parent view that the fragment's UI should be attached to (if not null).
     * @param savedInstanceState Past saved state of the fragment (if not null).
     * @return Return the View for the other user's library (fragment UI)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_other_users_codes, container, false);

        // create grid of OtherUser's codes
        gridOfCodes = (GridView) view.findViewById(R.id.otherUsersCodes);
        OtherUsersLibraryAdapter adapter = new OtherUsersLibraryAdapter(requireActivity(), getCodeNames(), getCodeScores(), getCodeDrawings());
        gridOfCodes.setAdapter(adapter);

        // go back to the OtherUser Profile
        backBttn = (ImageButton) view.findViewById(R.id.backBttn);
        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainAct = (MainActivity) getActivity();
                assert mainAct != null;
                mainAct.bttmNavView.setVisibility(View.VISIBLE);
                mainAct.onBackPressed();
            }
        });

        return view;
    }

    /**
     * Gets list of hashes from database based on username. Stores hashes in playerHashes.
     *
     * @since 1.0
     */
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
//
//        if (playerHashes.isEmpty()) {
//            Log.i("OtherUsersCodesVM", "No hashes found");
//        } else {
//            Log.i("OtherUsersCodesVM", "Hashes found!");
//        }
    }

    // for each hash, get code from database
    /**
     * Gets library of codes from the database. Uses the hashes of the codes from the player document
     * to get the codes from the QR Codes collection. Stores the names, scores, and drawings of the
     * codes in codeNames, codeScores, and codeDrawings. Currently does not update the adapter.
     *
     * @since 1.0
     */
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

//        if (codeNames.size() > 3) {
//            Log.i("OtherUsersCodesVM", "Real data added!");
//            Log.i("OtherUsersCodesVM", "Code names: " + codeNames);
//        } else {
//            Log.i("OtherUsersCodesVM", "No codes found");
//        }
    }

    /**
     * Makes test data for the code library. This is used for testing purposes.
     * Adds three codes to the code library.
     */
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

//        Log.i("OtherUsersCodesViewModel", "Test data added!");
    }

    public List<String> getCodeNames() {
        return codeNames;
    }

    public List<Integer> getCodeScores() {
        return codeScores;
    }

    public List<List<String>> getCodeDrawings() {
        return codeDrawings;
    }

    public int getLibrarySize() {
        return codeNames.size();
    }
}