package com.example.qrky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrky.placeholder.PlaceholderContent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

 A fragment that displays a list of nearby QR codes based on the user's current location.

 Uses a RecyclerView and an adapter to display the list.
 @author Aaron Binoy
 @version 1.0
 */
public class nearbyCodesFragment extends Fragment implements LocationListener {
    private boolean fromMapsFragment = false;
    private RecyclerView mRecyclerView;
    private MyItemRecyclerViewAdapter mAdapter;
    private List<PlaceholderContent.PlaceholderItem> mValues = new ArrayList<>();

    private LocationManager locationManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference qrCodesRef = db.collection("QR Codes");
    private ProgressBar mProgressBar;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private SearchView mSearchView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromMapsFragment = getArguments().getBoolean("fromMapsFragment", false); // default value is false
        }
    }

    public nearbyCodesFragment() {}
    /**

     Called when the Fragment is created.

     Sets up the RecyclerView and adapter, location manager, search view, and maps button.

     Checks for location permission and requests it if necessary.

     @param inflater LayoutInflater object used to inflate the Fragment's layout

     @param container ViewGroup object containing the Fragment's layout

     @param savedInstanceState Bundle object containing any saved state information

     @return the View object containing the Fragment's layout
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_codes, container, false);
        // Initialize the ProgressBar
        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        // Set up the recycler view and adapter
        mRecyclerView = view.findViewById(R.id.nearby_codes_recycler_view);
        mAdapter = new MyItemRecyclerViewAdapter(mValues);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Set up location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mSearchView = view.findViewById(R.id.search_view);
        setPrettyFont();
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this); // 1000 ms = 1 second
            } catch (SecurityException e) {
                // Handle the exception
                e.printStackTrace();
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

        return view;
    }

    /**
     Callback method to handle the result of a permission request. If the requested permission is granted,
     the method requests location updates using GPS_PROVIDER. If the requested permission is denied, a toast
     message is displayed to inform the user.
     @param requestCode The code for the requested permission
     @param permissions The requested permissions
     @param grantResults The grant results for the corresponding permissions
     */
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

    /**

     Method to query the Firestore database for QR codes within a certain radius of the user's location.

     The results are added to a list and the adapter is notified of the changes.

     @param latitude The latitude of the user's current location

     @param longitude The longitude of the user's current location

     @param radius The radius in kilometers to search within
     */
    private void queryNearbyCodes(double latitude, double longitude, double radius) {
        mValues.clear(); // Clear the list before adding new items

        qrCodesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<PlaceholderContent.PlaceholderItem> items = new ArrayList<>();

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
                            items.add(item);
                        }
                    }
                }

                mValues.addAll(items);
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the failure to query for nearby codes
                e.printStackTrace();
            }
        });
    }




    /**

     Callback method to handle location updates. The method calls the queryNearbyCodes method with the user's

     current location and a specified search radius.

     @param location The user's current location
     */
    @Override
    public void onLocationChanged(Location location) {
        // Get the user's current location
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Query nearby QR codes
        queryNearbyCodes(latitude, longitude, 5.0);
    }



    /**

     Callback method called when the fragment's view is destroyed. The method removes location updates to conserve

     resources when the fragment is no longer in view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Stop location updates when the fragment is destroyed
        locationManager.removeUpdates(this);
    }

    /**
     * Sets the font of the search bar to Josefin Sans Semibold.
     *
     * @since 2.0
     */
    private void setPrettyFont() {
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = mSearchView.findViewById(id);
        Typeface myCustomFont = ResourcesCompat.getFont(requireActivity(), R.font.josefin_sans_semibold);
        searchText.setTypeface(myCustomFont);
    }

}

