package com.example.qrky;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bttmNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the bottom navigation view
        //create bottom navigation view object
        bttmNavView = findViewById(R.id.bottom_navigating_view);
        NavHostFragment bttmNavHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);
        assert bttmNavHostFragment != null;
        NavController bttmNavController = bttmNavHostFragment.getNavController();
        NavigationUI.setupWithNavController(bttmNavView, bttmNavController);
    }

    public void switchTab(int id) {
        bttmNavView.setSelectedItemId(id);
    }
}