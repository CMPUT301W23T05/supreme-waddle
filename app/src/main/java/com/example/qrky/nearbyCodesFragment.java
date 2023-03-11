package com.example.qrky;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.qrky.placeholder.PlaceholderContent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of nearby QR codes.
 */
public class nearbyCodesFragment extends Fragment implements LocationListener {

    private RecyclerView mRecyclerView;
    private MyItemRecyclerViewAdapter mAdapter;
    private List<PlaceholderContent.PlaceholderItem> mValues = new ArrayList<>();

    private LocationManager locationManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference qrCodesRef = db.collection("QR Codes");

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private Button mapsButton;
    private SearchView mSearchView;
    public nearbyCodesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_codes, container, false);

        // Set up the recycler view and adapter
        mRecyclerView = view.findViewById(R.id.nearby_codes_recycler_view);
        mAdapter = new MyItemRecyclerViewAdapter(mValues);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Set up location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mSearchView = view.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return false;
            }
        });
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } catch (SecurityException e) {
                // Handle the exception
                e.printStackTrace();
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        mapsButton = view.findViewById(R.id.maps_button);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_fragment);
                navController.navigate(R.id.mapsFragment);
            }
        });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, perform the action
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                } catch (SecurityException e) {
                    // Handle the exception
                    e.printStackTrace();
                }

            } else {
                // Permission has been denied, handle it
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void queryNearbyCodes(double latitude, double longitude, double radius) {
        mValues.clear(); // Clear the list before adding new items
        qrCodesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    // Get the name attribute and location attribute
                    String name = documentSnapshot.getString("name");
                    GeoPoint docLocation = documentSnapshot.getGeoPoint("location");

                    // Check if both attributes are not null
                    if (name != null && docLocation != null) {
                        // Calculate the distance between the document's location and the current location
                        float[] distance = new float[1];
                        Location.distanceBetween(latitude, longitude, docLocation.getLatitude(), docLocation.getLongitude(), distance);

                        // Check if the distance is within the radius
                        if (distance[0] <= radius * 1000) {
                            String s = String.format("%.2f", distance[0]/1000.0);
                            PlaceholderContent.PlaceholderItem item = new PlaceholderContent.PlaceholderItem(documentSnapshot.getId(), name, s);
                            mValues.add(item);
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double radius = 6.0; // Search within a 5km radius

        queryNearbyCodes(latitude, longitude, radius);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Stop location updates when the fragment is destroyed
        locationManager.removeUpdates(this);
    }
}

