package com.example.qrky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/** A Fragment that displays info of a selected QR Code
 * @author Aaron & Sophia
 */
public class CardDetailsFragment extends DialogFragment {

    static final String ARG_TITLE = "title";
    private ImageView roboticFace;

    private ListView commentsList;
    private ArrayAdapter<String> commentsAdapter;
    private List<String> commentsArray = new ArrayList<>();


    public static CardDetailsFragment newInstance(String title) {
        CardDetailsFragment fragment = new CardDetailsFragment();
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
        View view = inflater.inflate(R.layout.popup_layout, null);

        TextView itemDetailsText = view.findViewById(R.id.item_details_text);
        itemDetailsText.setText(title);
        roboticFace = view.findViewById(R.id.robotic_face);

        commentsList = view.findViewById(R.id.comments_list);
        commentsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, commentsArray);
        commentsList.setAdapter(commentsAdapter);

        EditText commentInput = view.findViewById(R.id.comment_input);
        Button submitButton = view.findViewById(R.id.submit_button);
        ListView listView = view.findViewById(R.id.comments_list);

        qrCodesCollection.whereEqualTo("name", title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> comments = (List<String>) document.get("comments");
                                if (comments != null) {
                                    commentsArray.addAll(comments);
                                    commentsAdapter.notifyDataSetChanged();
                                }

                                // Load the image from the facepath field
                                String facepath = document.getString("facepath");
                                if (facepath != null && !facepath.isEmpty()) {
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageReference = storage.getReference().child(facepath);
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(requireContext())
                                                    .load(uri.toString())
                                                    .listener(new RequestListener<Drawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                            Log.e("CardDetailsFragment", "Image loading failed", e);
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            return false;
                                                        }
                                                    })
                                                    .into(roboticFace);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("CardDetailsFragment", "Image download URL fetch failed", e);
                                        }
                                    });
                                }

                            }
                        }
                    }
                });
        ImageButton button1 = view.findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoImageDialogFragment fragment = GeoImageDialogFragment.newInstance(title);
                fragment.show(getFragmentManager(), "geo_image_dialog");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentInput.getText().toString();
                if (!comment.isEmpty()) {
                    commentsArray.add(comment);
                    commentsAdapter.notifyDataSetChanged();
                    commentInput.getText().clear();

                    qrCodesCollection.whereEqualTo("name", title)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            qrCodesCollection.document(document.getId())
                                                    .update("comments", FieldValue.arrayUnion(comment));
                                        }
                                    }
                                }
                            });
                }
            }
        });

        ImageButton seeButton = view.findViewById(R.id.button2);

        seeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getArguments().getString(ARG_TITLE); // get the argument value
                pop2upFragment fragment = pop2upFragment.newInstance(title);
                fragment.show(getFragmentManager(), "popup");
            }
        });



        builder.setView(view);

        Dialog dialog = builder.create();

        // Set the background color of the root view
        View root = dialog.getWindow().getDecorView().findViewById(android.R.id.content);
        root.setBackgroundColor(ContextCompat.getColor(requireContext(), androidx.cardview.R.color.cardview_dark_background));

        return dialog;
    }



    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

}