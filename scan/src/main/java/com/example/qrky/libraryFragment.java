package com.example.qrky;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class libraryFragment extends Fragment {

    private FrameLayout mProgress;
    private TextView mTvCode;
    private TextView mTvLocation;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgress = view.findViewById(R.id.progress_circular);
        mTvCode = view.findViewById(R.id.tv_code);
        mTvLocation = view.findViewById(R.id.tv_location);

        mDb.collection("libraries")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mProgress.setVisibility(View.GONE);
                    Log.d("TAGTAG", "onViewCreated: " + queryDocumentSnapshots.size());
                    if (!queryDocumentSnapshots.isEmpty()) {
                        boolean first = true;
                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {
                            String code = qds.getString("code");
                            GeoPoint geoPoint = qds.getGeoPoint("location");
                            Log.d("TAGTAG", "onViewCreated: " + code + " " + geoPoint);
                            if (first) {
                                mTvCode.setText(code);
                                if (geoPoint != null &&
                                        geoPoint.getLatitude() > 0 && geoPoint.getLongitude() > 0) {
                                    mTvLocation.setText(
                                            geoPoint.getLatitude() + "," + geoPoint.getLongitude());
                                }
                            }
                            first = false;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    mProgress.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Load Failed", Toast.LENGTH_SHORT).show();
                });

    }
}