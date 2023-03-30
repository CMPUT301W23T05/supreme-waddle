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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * Represents an instance of user profile
 */
public class userProfileFragment extends DialogFragment {
    private String Username = MainActivity.getuName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String Email;
    private String Password;
    private int Rank;
    private int cardsCollected;
    private int totalPoints;

    public userProfileFragment() {
        // empty constructor
    }

    /**
     * creates instance of userProfileFragment
     * @return userProfileFragment
     */
    @SuppressWarnings("unused")

    public static userProfileFragment newInstance() {
       userProfileFragment fragment = new userProfileFragment();
       return fragment;
    }
    /**
     * creating profile dialog displaying user information
     * @param savedInstanceState -  reference to a Bundle object
     * @return builder view
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_profile, null);
        AlertDialog.Builder  builder = new AlertDialog.Builder(getContext(),android.R.style.Theme_Wallpaper_NoTitleBar_Fullscreen);


        // functions add here
        TextView username = view.findViewById(R.id.username);
        username.setText("@" + Username);
        TextView rank = view.findViewById(R.id.rank);
        TextView contactInfo = view.findViewById(R.id.contact_info);
        TextView cards_collected = view.findViewById(R.id.cards_collected);
        TextView total_points = view.findViewById(R.id.total_points);
        db.collection("Players").document(Username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Email = document.getString("email");
                    cardsCollected = Objects.requireNonNull(document.getLong("totalCodes")).intValue();
                    totalPoints = Objects.requireNonNull(document.getLong("score")).intValue();
                    rank.setText(String.valueOf(Rank));
                    cards_collected.setText(String.valueOf(cardsCollected));
                    total_points.setText(String.valueOf(totalPoints));
                    contactInfo.setText("username: " + Username + "\nemail: " + Email +"\npassword: ●●●●●●●●●●● ");
                }
            }
        });


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

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}