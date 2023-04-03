package com.example.qrky.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrky.R;
import com.example.qrky.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private Intent result;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;
    private EditText emailEditText;
    private TextView loginRegister;
    private Button back_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        result = new Intent();

        super.onCreate(savedInstanceState);

        Log.d("LoginActivity", "onCreate: 1");
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        Log.d("LoginActivity", "onCreate: 2");
        setContentView(binding.getRoot());
        Log.d("LoginActivity", "onCreate: 3");
        mAuth = FirebaseAuth.getInstance();
        Log.d("LoginActivity", "onCreate: 4");
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        Log.d("LoginActivity", "onCreate: 5");
        loginRegister = binding.pageTitle;
        emailEditText = binding.email;
        usernameEditText = binding.username;
        passwordEditText = binding.password;
        loginButton = binding.login;
        loadingProgressBar = binding.loading;
        emailEditText.setVisibility(View.GONE);
        back_button = findViewById(R.id.back);
        back_button.setVisibility(View.GONE);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRegister.setText("Login");
                emailEditText.setVisibility(View.GONE);
                back_button.setVisibility(View.GONE);
                loginButton.setText("Login");
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        try {
                            login(usernameEditText.getText().toString(),
                                    passwordEditText.getText().toString());
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /**
             * calls login() when the done button is pressed on the keyboard
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
                return false;
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * calls login() when the login button is clicked
             */
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                try {
                    login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    /**
     * logs in the user with the given username and password
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    public void login(String username, String password) throws NoSuchAlgorithmException {
        // can be launched in a separate asynchronous job
        final UUID[] mCustomToken = new UUID[1];

        db.collection("Players").document(username).get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.firestore.DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Auth", "DocumentSnapshot data: " + document.getData());
                        signInViaEmail(username, password);
                    } else {
                        Log.d("Auth", "No such document");
                        loadingProgressBar.setVisibility(View.GONE);
                        emailEditText.setVisibility(View.VISIBLE);
                        back_button.setVisibility(View.VISIBLE);
                        loginButton.setText("Register");
                        loginRegister.setText("Register");
                        loginButton.setOnClickListener(new View.OnClickListener() {
                            /**
                             * switches Login page to Register page
                             */
                            @Override
                            public void onClick(View v) {
                                loadingProgressBar.setVisibility(View.VISIBLE);
                                try {
                                    register(username, password, emailEditText.getText().toString());
                                } catch (Exception e) {
                                    Log.d("Auth", "Error: " + e);
                                    loadingProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });

                    }
                } else {
                    Log.d("Auth", "get failed with ", task.getException());
                }
            }
        });


    }

    /**
     * registers the user with the given email, username and password
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param email the email of the user
     */
    private void register(String username, String password, String email) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        try {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        /**
                         * called when the user is registered
                         * @param task the task of registering the user
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Auth", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Map<String, Object> player = new HashMap<>();
                                player.put("email", email);
                                player.put("score", 0);
                                player.put("totalCodes", 0);
                                db.collection("Players").document(username).set(player);
                                updateUiWithUser(user, username);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Auth", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Email already in use!",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        } catch (Exception e) {
            Log.d("Auth", "Error: " + e);
            loadingProgressBar.setVisibility(View.GONE);
        }

    }

    /**
     * signs into firebase with the given username and password
     * @param uName the username of the user
     * @param pass the password of the user
     */
    private void signInViaEmail(String uName, String pass) {
        final String[] email = new String[1];
        db.collection("Players").document(uName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            /**
             * gets the email of the user
             * @param task the task to be completed
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("signInEmail ", "DocumentSnapshot data: " + document.getData());
                        email[0] = Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("email")).toString();
                        Log.d("signInEmail ", "Email: " + email[0]);
                        authorize(email[0], pass, uName);
                    } else {
                        Log.d("Auth", "No such document");
                    }
                } else {
                    Log.d("Auth", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * updates the UI with the given user
     *
     * @param model the user to update the UI with
     * @param uName the username of the user
     */
    private void updateUiWithUser(FirebaseUser model, String uName) {
        loadingProgressBar.setVisibility(View.GONE);
        model.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(uName).build());
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), "Welcome " + uName + "!", Toast.LENGTH_LONG).show();
        result.putExtra("user", model);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    /**
     * authorizes the user with the given email and password
     *
     * @param email the email of the user
     * @param pass  the password of the user
     * @param uName the username of the user
     */
    private void authorize(String email, String pass, String uName) {
        Log.d("Auth", "Signing in with email: " + email + " and password: " + pass);
        mAuth.signInWithEmailAndPassword(email.trim(), pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    /**
                     * called when the task is complete
                     * @param task the task that was completed
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUiWithUser(user, uName);
                        } else {
                            Log.d("Auth", "Wrong password");
                            passwordEditText.setText("");
                            usernameEditText.setText("");
                            loadingProgressBar.setVisibility(View.GONE);

                            // If sign in fails, display a message to the user.
                            Log.w("Auth", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}