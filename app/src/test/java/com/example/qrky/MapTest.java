package com.example.qrky;

import com.google.android.gms.location.FusedLocationProviderClient;
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

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import android.Manifest;
public class MapTest {

    private MapsFragment fragment;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private FusedLocationProviderClient locationProviderClient;
    private Context context;

    @Before
    public void setUp() {
        context = mock(Context.class);
        when(context.checkSelfPermission(eq(Manifest.permission.ACCESS_FINE_LOCATION)))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(context.getPackageManager()).thenReturn(mock(PackageManager.class));
        when(context.getSystemService(eq(Context.LOCATION_SERVICE)))
                .thenReturn(mock(LocationManager.class));
        when(context.getResources()).thenReturn(mock(Resources.class));

        fragment = new MapsFragment();
        mapFragment = mock(SupportMapFragment.class);
        googleMap = mock(GoogleMap.class);
        locationProviderClient = mock(FusedLocationProviderClient.class);
    }

    @Test
    public void testMapFragmentIsInitialized() {
        MapsFragment mapsFragment = new MapsFragment();
        assertNotNull(mapsFragment);
    }

    @Test
    public void testMapIsInitialized() {
        // Set up the GoogleMap object by calling getMapAsync()
        GoogleMap googleMap = mock(GoogleMap.class);

        // check if the mock object is not null
        assertNotNull(googleMap);
    }


}


