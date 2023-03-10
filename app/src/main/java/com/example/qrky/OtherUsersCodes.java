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

public class OtherUsersCodes extends Fragment {

//    FragmentOtherUsersCodesBinding binding;
    private OtherUsersCodesViewModel mViewModel;
    private GridView gridOfCodes;
    private ImageButton backBttn;

    public OtherUsersCodes() {}
    public static OtherUsersCodes newInstance() {
        return new OtherUsersCodes();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("OtherUsersCodes", "Looking at OtherUser's codes");
        super.onCreate(savedInstanceState);
//        binding = FragmentOtherUsersCodesBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        // hide bottom navigation bar when viewing OtherUser's codes
        MainActivity mainAct = (MainActivity) getActivity();
        assert mainAct != null;
        mainAct.bttmNavView.setVisibility(View.INVISIBLE);

        mViewModel = new ViewModelProvider(this).get(OtherUsersCodesViewModel.class);
        mViewModel.addTestData();  // add test data
        Log.i("OtherUsersCodes", "codeDrawings: " + mViewModel.codeDrawings);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_other_users_codes, container, false);

        // create grid of OtherUser's codes
        gridOfCodes = (GridView) view.findViewById(R.id.otherUsersCodes);
        OtherUsersLibraryAdapter adapter = new OtherUsersLibraryAdapter(OtherUsersCodes.this.getContext(), mViewModel.codeNames, mViewModel.codeScores, mViewModel.codeDrawings);
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