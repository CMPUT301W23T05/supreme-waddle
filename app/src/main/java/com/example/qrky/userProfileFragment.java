package com.example.qrky;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;


import android.app.AlertDialog;
import android.app.Dialog;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Represents an instance of user profile
 */
public class userProfileFragment extends DialogFragment {


    private String Username;
    private String Email;
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

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_profile, null);
        AlertDialog.Builder  builder = new AlertDialog.Builder((getContext()));

        // functions add here
        // edit profile button
        return builder
                .setView(view)
                .setNegativeButton("Exit", null)
                .setPositiveButton("Ok", null).create();

    }


}