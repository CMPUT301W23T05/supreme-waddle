package com.example.qrky;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

/**
 * Allows user to edit their profile and password
 */
public class ProfileEditActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private Button mSaveButton;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button mBackButton;
    private Intent resultIntent = new Intent();
    private boolean changed = false;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_profile_edit);
        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.confirm_password);
        mSaveButton = findViewById(R.id.save_changes);
        user = MainActivity.getUser();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mBackButton = findViewById(R.id.backBttn);

        mUsername.setHint(user.getDisplayName());
        mEmail.setHint(MainActivity.getUser().getEmail());
        mPassword.setHint("********");


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String uName = user.getDisplayName();
                String email = mEmail.getText().toString();
                Boolean nameChanged = false;
                Boolean emailChanged = false;
                Boolean passwordChanged = false;
                String password = mPassword.getText().toString();
                Log.d("update", "Username: " + username + " Email: " + email + " Password: " + password);
                if (!username.equals("")) {
                    nameChanged = true;
                }
                if (!email.equals("")) {
                    emailChanged = true;
                }
                if (!password.equals("")) {
                    passwordChanged = true;
                }
                updateUsername(uName, nameChanged, emailChanged, passwordChanged);
                finishUpdate();
            }
        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Updates the user's username
     * @param oldName the user's old username
     * @param nameChanged whether the user changed their username
     * @param emailChanged whether the user changed their email
     * @param passwordChanged whether the user changed their password
     */
    private void updateUsername(String oldName, Boolean nameChanged, Boolean emailChanged, Boolean passwordChanged) {
        if (nameChanged) {
            String newUName = mUsername.getText().toString();
            if (newUName.equals(oldName)) {
            Log.d("updateUsername", "Username not changed");
            updatePassword(mPassword.getText().toString(), oldName, emailChanged, passwordChanged);
            } else {
                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(newUName).build()).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {

                        db.collection("Players").document(Objects.requireNonNull(oldName)).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                changed = true;

                                Log.d("updateUsername", "DocumentSnapshot data: " + task.getResult().getData());

                                db.collection("Players").document(newUName).set(Objects.requireNonNull(task.getResult().getData()));
                                db.collection("Players").document(oldName).delete();

                                db.collection("QR Codes").whereArrayContains("playerID", oldName).get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        for (DocumentSnapshot document : Objects.requireNonNull(task1.getResult())) {
                                            Log.d("updateUsername", document.getId() + " => " + document.getData());
                                            List<String> playerID = (List<String>) document.get("playerID");
                                            playerID.remove(oldName);
                                            playerID.add(newUName);
                                            db.collection("QR Codes").document(document.getId()).update("playerID", playerID);
                                        }
                                    } else {
                                        Log.d("updateUsername", "Error getting documents: ", task1.getException());
                                    }
                                });

                                Log.d("updateUsername", "Username updated");

                            } else {
                                Log.d("updateUsername", "Username update failed");
                            }

                            updatePassword(mPassword.getText().toString(), newUName, emailChanged, passwordChanged);

                        });
                    }
                });
            }
        } else {
            updatePassword(mPassword.getText().toString(), oldName, emailChanged, passwordChanged);
        }
    }


    /**
     * Updates the user's email
     * @param email the user's new email
     * @param username the user's username
     * @param emailChanged whether the user changed their email
     */
    private void updateEmail(String email, String username, Boolean emailChanged) {
        if (emailChanged){
            user.updateEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("updateEmail", "Email updated");
                    try {
                        db.collection("Players").document(Objects.requireNonNull(username)).update("email", email);
                    } catch (Exception e) {
                        Log.d("updateEmail", "Username not changed");
                    }
                    changed = true;
                } else {
                    Log.d("updateEmail", "Email update failed");
                }

                finishUpdate();
            });
        }
        else {
            finishUpdate();
        }
    }

    /**
     * Updates the user's password
     * @param password the user's new password
     * @param username the user's username
     * @param emailChanged whether the user changed their email
     * @param passwordChanged whether the user changed their password
     */
    private void updatePassword(String password, String username,  Boolean emailChanged, Boolean passwordChanged) {
        if (passwordChanged) {
            user.updatePassword(password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("updatePassword", "Password updated");
                    changed = true;
                    db.collection("Players").document(username).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            fAuth.signInWithEmailAndPassword(Objects.requireNonNull(task1.getResult().get("email")).toString(), password).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    Log.d("updatePassword", "Signed in");
                                } else {
                                    Log.d("updatePassword", "Sign in failed");
                                }
                                user = fAuth.getCurrentUser();
                                updateEmail(mEmail.getText().toString(), username, emailChanged);
                            });
                        } else {
                            Log.d("updatePassword", "Username update failed");
                            updateEmail(mEmail.getText().toString(), username, emailChanged);
                        }
                    });
                } else {
                    Log.d("updatePassword", "Password update failed");
                    updateEmail(mEmail.getText().toString(), username, emailChanged);
                }


            });
        }
        else {
            updateEmail(mEmail.getText().toString(), username, emailChanged);
        }
    }


    /**
     * Finishes the update process and reloads the app
     */
    private void finishUpdate() {
        user.reload();
        resultIntent.putExtra("changed", changed);
        resultIntent.putExtra("user", user);
        setResult(Activity.RESULT_OK, resultIntent);

        if (changed) {
            startActivity(new Intent(ProfileEditActivity.this, MainActivity.class));
        }
        finish();
    }
}