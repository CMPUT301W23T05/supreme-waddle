package com.example.qrky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qrky.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    private String Username = MainActivity.getuName();
    private String Email;
    private int cardsCollected;
    private int totalPoints;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private int Rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        TextView username = findViewById(R.id.username);
        username.setText("@" + Username);
        TextView rank = findViewById(R.id.rank);
        Button back_button = findViewById(R.id.backBttn);
        Button logout_button = findViewById(R.id.logout_button);
        TextView contactInfo = findViewById(R.id.contact_info);
        TextView cards_collected = findViewById(R.id.cards_collected);
        TextView total_points = findViewById(R.id.total_points);
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


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // logout button functionality (add later)
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


        Button edit_button = findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // edit profile button functionality (add later)
            }
        });
    }
}