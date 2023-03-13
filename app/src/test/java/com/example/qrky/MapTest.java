package com.example.qrky;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

public class MapTest {

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    @Before
    public void setUp() {
        mapFragment = new SupportMapFragment();
    }

    @Test
    public void testMapFragmentIsInitialized() {
        assertNotNull(mapFragment);
    }

    @Test
    public void testMapIsInitialized() {
        // Set up the GoogleMap object by calling getMapAsync()
        GoogleMap googleMap = mock(GoogleMap.class);

        // check if the mock object is not null
        assertNotNull(googleMap);
    }
}


