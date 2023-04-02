package com.example.qrky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    private String uName = MainActivity.getuName();
    private String Email;
    private int cardsCollected;
    private int totalPoints;
    private TextView username;
    private TextView rank;
    private Button back_button;
    private Button logout_button;
    private TextView contactInfo;
    private TextView cards_collected;
    private TextView total_points;
    private FirebaseFirestore db;
    private FirebaseUser user;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private int Rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = MainActivity.getUser();
        setContentView(R.layout.activity_user_profile);
        username = findViewById(R.id.username);
        db = FirebaseFirestore.getInstance();
        rank = findViewById(R.id.rank);
        back_button = findViewById(R.id.backBttn);
        logout_button = findViewById(R.id.logout_button);
        contactInfo = findViewById(R.id.contact_info);
        cards_collected = findViewById(R.id.cards_collected);
        total_points = findViewById(R.id.total_points);

        db.collection("Players").document(uName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    cardsCollected = Objects.requireNonNull(document.getLong("totalCodes")).intValue();
                    totalPoints = Objects.requireNonNull(document.getLong("score")).intValue();
                    rank.setText(String.valueOf(Rank));
                    cards_collected.setText(String.valueOf(cardsCollected));
                    total_points.setText(String.valueOf(totalPoints));
                }
            }
        });
        updateProfile(user);


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
                startActivityForResult(new Intent(getApplicationContext(), ProfileEditActivity.class), 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (data.getBooleanExtra("changed", false)) {
                Log.d("profile", "onActivityResult: ");
                user.reload();
                updateProfile(data.getParcelableExtra("user"));
                MainActivity.userUpdate(data.getParcelableExtra("user"));





            }
            Log.d("profile", "onActivityResult: " + user.getDisplayName());

        }
    }

    private void updateProfile(FirebaseUser user) {
        Log.d("profile", "updateProfile: " + user.getDisplayName());
        String uName = user.getDisplayName();
        username.setText("@" + uName);
        contactInfo.setText("username: " + uName + "\nemail: " + user.getEmail() +"\npassword: ●●●●●●●●●●● ");
    }
}
