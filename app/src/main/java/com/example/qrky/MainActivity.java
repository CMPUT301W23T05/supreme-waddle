package com.example.qrky;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.qrky.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;


/**
 * Main activity for the app. Contains the bottom navigation view and the fragment container.
 * The bottom navigation view is used to navigate between the different fragments.
 *
 * @author Franco Bonilla
 * @version 1.0 2023/02/15
 *
 */
public class  MainActivity extends AppCompatActivity {
    private static final String KEY_RESTART_INTENT = "RESTART_INTENT";
    BottomNavigationView bttmNavView;
    public static final String REQUEST_RESULT="REQUEST_RESULT";
    private static String uName;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the bottom navigation view
        bttmNavView = findViewById(R.id.bottom_navigating_view);
        NavHostFragment bttmNavHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);
        assert bttmNavHostFragment != null;
        NavController bttmNavController = bttmNavHostFragment.getNavController();
        NavigationUI.setupWithNavController(bttmNavView, bttmNavController);
        bttmNavView.setVisibility(View.VISIBLE);
        startActivityForResult(new Intent(this, LoginActivity.class), 1);
        bttmNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_fragment);
                switch (item.getItemId()) {
                    case R.id.communityFragment:
                        navController.navigate(R.id.communityFragment);
                        break;
                    case R.id.scannerFragment:
                        navController.navigate(R.id.scannerFragment);
                        break;
                    case R.id.mapsFragment:
                        navController.navigate(R.id.mapsFragment);
                        break;
                    case R.id.libraryFragment:
                        navController.navigate(R.id.libraryFragment);
                        break;
                }
                return true;
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
                user = data.getParcelableExtra("user");
                uName = data.getStringExtra("username");
                Log.d("MainActivity", "onActivityResult: " + uName);
                Log.d("MainActivity", "onActivityResult: " + user.getUid());

        }
    }
    /**
     * Switches between the different tabs in the bottom navigation view.
     * @param id the id of the tab to switch to
     */
    public void switchTab(int id) {
        bttmNavView.setSelectedItemId(id);
    }

    /**
     * Makes navigation bar visible.
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            bttmNavView.setVisibility(View.VISIBLE);
        }
    }

    public static String getuName() {
        return uName;
    }

    public FirebaseUser getUser() {
        return user;
    }
}