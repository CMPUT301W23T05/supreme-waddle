package com.example.qrky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.List;

public class GeoImageDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private ImageView imageView;
    private TextView locationText;

    public static GeoImageDialogFragment newInstance(String title) {
        GeoImageDialogFragment fragment = new GeoImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString(ARG_TITLE);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference qrCodesCollection = firestore.collection("QR Codes");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.geo_image_dialog_fragment, null);

        locationText = view.findViewById(R.id.location_text);
        imageView = view.findViewById(R.id.image_view);

        qrCodesCollection.whereEqualTo("name", title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get geolocation and set the text
                            LatLng location = getLatLngFromGeoPoint(document.getGeoPoint("location"));
                            if (location != null) {
                                DecimalFormat df = new DecimalFormat("#.######");
                                locationText.setText("Location\n" + df.format(location.latitude) + ", " + df.format(location.longitude));
                            }

                            // Get the last image path from the "photo" array field
                            List<String> photoPaths = (List<String>) document.get("photo");
                            if (photoPaths != null && !photoPaths.isEmpty()) {
                                String imagePath = photoPaths.get(photoPaths.size() - 1); // Get the last image path
                                Log.d("GeoImageDialogFragment", "Image path: QRImages/" + imagePath);
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReference().child(imagePath);
                                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Glide.with(requireContext())
                                            .load(uri.toString())
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    Log.e("GeoImageDialogFragment", "Image loading failed", e);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    return false;
                                                }
                                            })
                                            .into(imageView);
                                }).addOnFailureListener(e -> {
                                    Log.e("GeoImageDialogFragment", "Image download URL fetch failed", e);
                                });
                            }
                        }
                    }
                });

        builder.setView(view);
        Dialog dialog = builder.create();

// Set the window background to transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    private LatLng getLatLngFromGeoPoint(GeoPoint geoPoint) {
        if (geoPoint != null) {
            return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        }
        return null;
    }
}