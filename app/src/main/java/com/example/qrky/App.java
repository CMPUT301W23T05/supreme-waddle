package com.example.qrky;
import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * set up for firebase database
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
    }
}
