package com.example.qrky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Displays a user's profile
 */
public class UserProfileActivity extends AppCompatActivity {
    private String uName;
    private int cardsCollected;
    private int totalPoints;
    private TextView username;
    private TextView rank;
    private TextView contactInfo;
    private TextView cards_collected;
    private TextView total_points;
    private FirebaseUser user;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        Intent intent = getIntent();

        user = MainActivity.getUser();
        setContentView(R.layout.activity_user_profile);
        username = findViewById(R.id.username);
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        rank = findViewById(R.id.rank);
        Button back_button = findViewById(R.id.backBttn);
        Button logout_button = findViewById(R.id.logout_button);
        contactInfo = findViewById(R.id.contact_info);
        cards_collected = findViewById(R.id.cards_collected);
        total_points = findViewById(R.id.total_points);
        Button edit_button = findViewById(R.id.edit_button);
        updateProfile(user);
        Log.d("UserProfileActivity", "editButton" + edit_button);
        if (intent.getBooleanExtra("viewOther", false) && !Objects.equals(intent.getStringExtra("username"), user.getDisplayName())) {
            uName = intent.getStringExtra("username");
            logout_button.setVisibility(View.GONE);
            contactInfo.setVisibility(View.GONE);
            edit_button.setVisibility(View.GONE);
            RecyclerView codesRecycler = findViewById(R.id.normal_recycler);
            codesRecycler.setVisibility(View.VISIBLE);
            codesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            ArrayList<CardData> filteredCards = new ArrayList<>();
            CardAdapter adapter = new CardAdapter(filteredCards, true);
            codesRecycler.setAdapter(adapter);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
// Retrieve data from the Firebase database
            db.collection("QR Codes")
                    .whereArrayContains("playerID", uName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Generate the list of cards with name and score fields
                            List<CardData> cards = new ArrayList<>();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                String name = (String) document.get("name");
                                Long scoreLong = document.getLong("score");
                                Integer score = (scoreLong != null) ? scoreLong.intValue() : null;
                                String hash = (String) document.getId();

                                // Only add the card to the list if the playerID field contains "playerID1"
                                List<String> playerIds = (List<String>) document.get("playerID");
                                if (name != null && score != null && playerIds != null && playerIds.contains(uName)) {
                                    cards.add(new CardData(name, score, hash));
                                }
                            }

                            filteredCards.addAll(cards);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        else {
            uName = user.getDisplayName();
            logout_button.setVisibility(View.VISIBLE);
            contactInfo.setVisibility(View.VISIBLE);
            edit_button.setVisibility(View.VISIBLE);
        }
        username.setText(uName);
        db1.collection("Players").document(uName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    cardsCollected = Objects.requireNonNull(document.getLong("totalCodes")).intValue();
                    totalPoints = Objects.requireNonNull(document.getLong("score")).intValue();
                    rank.setText(Objects.requireNonNull(document.getLong("rank")).toString());
                    cards_collected.setText(String.valueOf(cardsCollected));
                    total_points.setText(String.valueOf(totalPoints));
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

    /**
     * Updates the profile with the user's information
     * @param user the user whose information is being displayed
     */
    private void updateProfile(FirebaseUser user) {
        Log.d("profile", "updateProfile: " + user.getDisplayName());
        String uName = user.getDisplayName();
        username.setText("@" + uName);
        contactInfo.setText("username: " + uName + "\nemail: " + user.getEmail() +"\npassword: ●●●●●●●●●●● ");
    }
}
