package com.example.qrky;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.DialogInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 *
 *A fragment class that displays a map with QR code markers retrieved from Firebase Firestore.
 * @author Aaron Binoy
 * @version 1.0
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    private FragmentManager parentFragmentManager;
    /**

     Called when the fragment is attached to the activity.
     Sets the parentFragmentManager.
     @param context The context in which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentFragmentManager = getActivity().getSupportFragmentManager();
    }

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Button nearbyCodesButton;
    /**
     * Called when the fragment is being created.
     * Initializes the mFusedLocationProviderClient.
     * @param savedInstanceState The saved instance state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }
    /**
     * Called to create the view hierarchy associated with the fragment.
     * Initializes the map fragment, checks location permission, and sets the click listener for nearbyCodesButton.
     * Starts a handler to refresh the data every 5 minutes.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The saved instance state of the fragment.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        // Get the button view and set the click listener
        nearbyCodesButton = rootView.findViewById(R.id.nearby_codes_button);
        nearbyCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_fragment);
                navController.navigate(R.id.nearbyCodesFragment);
            }
        });

        // Refresh the data every 5 minutes
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                loadQRCodesFromFirebase();
                handler.postDelayed(this, 5 * 60 * 1000); // Repeat after 5 minutes
            }
        };
        handler.postDelayed(runnable, 5 * 60 * 1000); // Start after 5 minutes

        return rootView;
    }


    /**
     * Called when the map is ready to be used.
     * Initializes the map and sets the click listener for the current location button.
     * Enables the current location layer if permission is granted.
     * Loads QR code markers from Firebase Firestore.
     * @param googleMap The GoogleMap object representing the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Set current location button listener
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                            }
                        }
                    });
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
                return false;
            }
        });

        // Enable current location layer if permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                }
            }
        });

        loadQRCodesFromFirebase();
    }
    /**
     * Called when the user responds to a permission request.
     * @param requestCode The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                // Permission denied
                Toast.makeText(getActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Load QR codes from Firebase Firestore and display them on the map.
     */
    private void loadQRCodesFromFirebase() {
        // Access the Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Access the "QR Codes" collection
        CollectionReference qrCodesRef = db.collection("QR Codes");

        // Build a query to retrieve documents that have a "Location" attribute with a non-null geopoint value
        Query query = qrCodesRef.whereNotEqualTo("location", null);

        // Add a listener to the query to get real-time updates
        query.addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                Log.e(TAG, "Error getting QR codes: ", error);
                return;
            }

            // Clear the map
            mMap.clear();

            // Loop through the documents and add markers to the map
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                GeoPoint location = document.getGeoPoint("location");
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(latLng));
                }
            }
        });
    }





}
