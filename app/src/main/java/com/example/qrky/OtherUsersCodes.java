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
import androidx.lifecycle.ViewModelProvider;


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
    public OtherUsersCodesViewModel otherUsersCodeVM;
    private GridView gridOfCodes;
    private ImageButton backBttn;

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

        // call ViewModel
        otherUsersCodeVM = new ViewModelProvider(requireActivity()).get(OtherUsersCodesViewModel.class);
        otherUsersCodeVM.getOtherUsersCodes();  // add data
        otherUsersCodeVM.getTestData();  // add test data
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
        OtherUsersLibraryAdapter adapter = new OtherUsersLibraryAdapter(requireActivity(), otherUsersCodeVM.codeNames, otherUsersCodeVM.codeScores, otherUsersCodeVM.codeDrawings);
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


}