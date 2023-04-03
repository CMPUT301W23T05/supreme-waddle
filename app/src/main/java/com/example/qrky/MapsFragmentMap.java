package com.example.qrky;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


/**
 *
 *A fragment class that displays a map with QR code markers retrieved from Firebase Firestore.
 * @author Aaron Binoy
 * @version 1.0
 */
public class MapsFragmentMap extends Fragment implements OnMapReadyCallback {

    private Context mContext;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private Bitmap mBitmap;
    private Button infoButton;
    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("QR Icons");

        // Create a custom layout for the dialog box
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_info, null);
        builder.setView(customLayout);

        builder.setPositiveButton("OK", null);
        builder.show();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
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
        infoButton = rootView.findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoDialog();
            }
        });

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }


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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.my_map));


        // Set current location button listener
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                            }
                        }
                    });
                } else {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
                return false;
            }
        });

        // Enable current location layer if permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
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
                if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
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
                    String name = document.getString("name");
                    Long scoreObj = document.getLong("score");
                    int score = (scoreObj != null) ? scoreObj.intValue() : 0; // Retrieve the score from the DocumentSnapshot, defaulting to 0 if it is null


                    // Derive the rarity from the score using the getRarity method of the CardAdapter class
                    String rarity = CardAdapter.getRarity(score);

                    // Load the appropriate marker image based on the rarity
                    int markerImageResourceId;
                    switch (rarity) {
                        case "Ultra Rare":
                            markerImageResourceId = R.drawable.ultrarare_qr;
                            break;
                        case "Very Rare":
                            markerImageResourceId = R.drawable.veryrare_qr;
                            break;
                        case "Rare":
                            markerImageResourceId = R.drawable.rare_qr;
                            break;
                        case "Uncommon":
                            markerImageResourceId = R.drawable.uncommon_qr;
                            break;
                        default:
                            markerImageResourceId = R.drawable.common_qr;
                            break;
                    }

                    // Calculate the marker size based on the zoom level
                    float zoom = mMap.getCameraPosition().zoom;
                    int size;
                    switch ((int) zoom) {
                        default:
                            size = 200;
                            break;
                    }

                    // Load the marker image with Glide
                    Glide.with(requireContext())
                            .asBitmap()
                            .load(markerImageResourceId)
                            .override(size, size)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    // Create a BitmapDescriptor from the resized bitmap
                                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resource);

                                    mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(bitmapDescriptor));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                }
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String name = marker.getTitle(); // Get the name of the QR code
                    Toast.makeText(requireContext(), name, Toast.LENGTH_SHORT).show(); // Display the name in a toast
                    return true;
                }
            });
        });


        // Add a camera idle listener to update the marker sizes when the zoom level changes
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Reload the markers to update their sizes
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            loadQRCodesFromFirebase();
                        }
                    }
                });
            }
        });
    }



}
