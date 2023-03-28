package com.example.qrky;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Main activity for the app. Contains the bottom navigation view and the fragment container.
 * The bottom navigation view is used to navigate between the different fragments.
 *
 * @author Franco Bonilla
 * @version 1.0 2023/02/15
 *
 */
public class  MainActivity extends AppCompatActivity {
    BottomNavigationView bttmNavView;

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
}