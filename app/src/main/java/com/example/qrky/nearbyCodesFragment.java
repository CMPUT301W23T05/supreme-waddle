package com.example.qrky;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.qrky.placeholder.PlaceholderContent;

/**
 * A fragment representing a list of nearby QR codes.
 */
public class nearbyCodesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MyItemRecyclerViewAdapter mAdapter;

    public nearbyCodesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_codes, container, false);

        // Set up the button and search view
        Button mapsButton = view.findViewById(R.id.maps_button);
        SearchView searchView = view.findViewById(R.id.search_view);
        Button nearbyCodesButton = view.findViewById(R.id.nearby_codes_button);

        // Set up the RecyclerView
        mRecyclerView = view.findViewById(R.id.nearby_codes_recycler_view);
        mAdapter = new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        // Set up the title
        TextView titleTextView = view.findViewById(R.id.nearby_codes_title);
        titleTextView.setText("Nearby Codes");

        return view;
    }
}
