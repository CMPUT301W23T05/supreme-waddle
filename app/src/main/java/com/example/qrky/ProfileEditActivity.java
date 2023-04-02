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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

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
        super.onCreate(savedInstanceState);
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
                final String[] signedEmail = {user.getEmail()};
                String password = mPassword.getText().toString();
                Log.d("update", "Username: " + username + " Email: " + email + " Password: " + password);
                final boolean wait[] = new boolean[]{false, false, false, true, true, true};
                if (!email.isEmpty()) {
                    Log.d("updateEmail", "Email: " + email);
                    wait[0] = true;

                    user.updateEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("updateEmail", "Email updated");
                            signedEmail[0] = email;
                            try {
                            db.collection("Players").document(Objects.requireNonNull(username)).update("email", email);
                            } catch (Exception e) {
                                Log.d("updateEmail", "Username not changed");
                            } try {
                                db.collection("Players").document(Objects.requireNonNull(uName)).update("email", email);
                            } catch (Exception e) {
                                Log.d("updateEmail", "Username not changed");
                            }
                            changed = true;
                        } else {
                            Log.d("updateEmail", "Email update failed");
                        }
                        wait[0] = false;
                        if (!wait[1] && !wait[2] && !wait[3] && !wait[4] && !wait[5]) {
                            resultIntent.putExtra("changed", changed);
                            resultIntent.putExtra("user", user);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    });


                }
                wait[3] = false;
                if (!password.isEmpty()) {
                    Log.d("updatePassword", "Password: " + password);
                    wait[1] = true;
                    user.updatePassword(password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("updatePassword", "Password updated");
                            fAuth.signInWithEmailAndPassword(signedEmail[0], password).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("updatePassword", "Password updated");
                                    changed = true;
                                } else {
                                    Log.d("updatePassword", "Password update failed");
                                }
                                wait[1] = false;
                                if (!wait[0] && !wait[2] && !wait[3] && !wait[4] && !wait[5]) {
                                    resultIntent.putExtra("changed", changed);
                                    resultIntent.putExtra("user", user);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                            });
                            changed = true;
                        } else {
                            Log.d("updatePassword", "Password update failed");
                        }
                        wait[1] = false;
                        if (!wait[0] && !wait[2] && !wait[3] && !wait[4] && !wait[5]) {
                            resultIntent.putExtra("changed", changed);
                            resultIntent.putExtra("user", user);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    });


                }
                wait[4] = false;
                if (!username.isEmpty()) {
                    wait[2] = true;

                    user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(username).build()).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {

                            db.collection("Players").document(Objects.requireNonNull(uName)).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    changed = true;

                                    Log.d("updateUsername", "DocumentSnapshot data: " + task.getResult().getData());
                                    if (!uName.equals(username)) {
                                        db.collection("Players").document(username).set(Objects.requireNonNull(task.getResult().getData()));
                                        db.collection("Players").document(uName).delete();
                                    }
                                    Log.d("updateUsername", "Username updated");

                                } else {
                                    Log.d("updateUsername", "Username update failed");
                                }
                                wait[2] = false;
                                if (!wait[0] && !wait[1] && !wait[3] && !wait[4] && !wait[5]) {
                                    resultIntent.putExtra("changed", changed);
                                    resultIntent.putExtra("user", user);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }

                            });
                        }
                    });
                }
                wait[5] = false;
                if (!wait[0] && !wait[1] && !wait[2] && !wait[3] && !wait[4] && !wait[5]) {
                    resultIntent.putExtra("changed", changed);
                    resultIntent.putExtra("user", user);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }

            }

        });


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}