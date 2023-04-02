package com.example.qrky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardDetailsFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";

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

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(ARG_TITLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_layout, null);

        TextView itemDetailsText = view.findViewById(R.id.item_details_text);
        itemDetailsText.setText(title);

        commentsList = view.findViewById(R.id.comments_list);
        commentsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, commentsArray);
        commentsList.setAdapter(commentsAdapter);

        EditText commentInput = view.findViewById(R.id.comment_input);
        Button submitButton = view.findViewById(R.id.submit_button);
        ListView listView = view.findViewById(R.id.comments_list);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentInput.getText().toString();
                if (!comment.isEmpty()) {
                    commentsArray.add(comment);
                    databaseReference.child("comments").push().setValue(comment);
                    commentsAdapter.notifyDataSetChanged();
                    commentInput.getText().clear();
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsAdapter.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String sValue = dataSnapshot.child("comments").getValue(String.class);
                    commentsArray.add(sValue);
                    commentsAdapter.add(sValue);
                }
                commentsAdapter.notifyDataSetChanged();
                listView.setSelection(commentsAdapter.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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