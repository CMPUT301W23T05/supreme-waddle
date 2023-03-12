package com.example.qrky;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;


import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Represents an instance of user profile
 */
public class userProfileFragment extends DialogFragment {


    private String Username = "@toBFrank";
    private String Email;
    private String Password;
    private int Rank;
    private int cardsCollected;
    private int totalPoints;

    public userProfileFragment() {
        // empty constructor
    }
    @SuppressWarnings("unused")
    public static userProfileFragment newInstance() {
       userProfileFragment fragment = new userProfileFragment();
       return fragment;
    }
    /**
     * java docs
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_profile, null);
        AlertDialog.Builder  builder = new AlertDialog.Builder((getContext()));


        // functions add here
        TextView username = view.findViewById(R.id.username);
        TextView rank = view.findViewById(R.id.rank);
        TextView cards_collected = view.findViewById(R.id.cards_collected);
        TextView total_points = view.findViewById(R.id.total_points);

        username.setText(getResources().getString(R.string.username1,Username));

        Button edit_button = view.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // edit profile button functionality (add later)
            }
        });
        return builder
                .setView(view)
                .setNegativeButton("Exit", null)
                .setPositiveButton("Ok", null).create();

    }



    public int getRank() {
        return Rank;
    }

    public void setRank(int rank) {
        Rank = rank;
    }

    public int getCardsCollected() {
        return cardsCollected;
    }

    public void setCardsCollected(int cardsCollected) {
        this.cardsCollected = cardsCollected;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}