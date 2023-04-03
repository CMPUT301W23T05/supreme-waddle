package com.example.qrky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays an popup for see others who scanned same QR code
 *
 * @author Suyeon Kim
 */
public class pop2upFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";

    private ListView pplList;
    private ArrayAdapter<String> pplAdapter;
    private List<String> pplArray = new ArrayList<>();

    public pop2upFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title QR Name
     * @return A new instance of fragment pop2upFragment.
     */
    public static pop2upFragment newInstance(String title) {
        pop2upFragment fragment = new pop2upFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates the view for the CommunityFragment.
     *
     * @param inflater layoutInflater object to inflate view for the fragment
     * @param container Parent view
     * @param savedInstanceState Past saved state of the fragment (if not null).
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pop2up, container, false);

        String title = getArguments().getString(ARG_TITLE);

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        CollectionReference qrCodesCollection = fireStore.collection("QR Codes");

        pplList = view.findViewById(R.id.ppl_list);
        pplAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, pplArray);
        pplList.setAdapter(pplAdapter);

        qrCodesCollection.whereEqualTo("name", title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pplArray.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> IDs = (List<String>) document.get("playerID");
                                if (IDs != null) {
                                    pplArray.addAll(IDs);
                                }
                            }
                            pplAdapter.notifyDataSetChanged();
                        }
                    }
                });
        return view;
    }

    /**
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogFragmentStyle; // Optional animation
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.gd1); // Set custom background
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}