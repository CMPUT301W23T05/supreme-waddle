package com.example.qrky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link pop2upFragment#newInstance} factory method to
 * create an instance of this fragment.
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
    // TODO: Rename and change types and number of parameters
    public static pop2upFragment newInstance(String title) {
        pop2upFragment fragment = new pop2upFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreateView(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getArguments().getString(ARG_TITLE);

        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        CollectionReference qrCodesCollection = fireStore.collection("QR Codes");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_pop2up, null);

        TextView itemDetailsText = view.findViewById(R.id.item_details_text);
        itemDetailsText.setText(title);

        pplList = view.findViewById(R.id.ppl_list);
        pplAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, pplArray);
        pplList.setAdapter(pplAdapter);

        qrCodesCollection.whereEqualTo("name", title)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> IDs = (List<String>) document.get("playerID");
                                if (IDs != null) {
                                    pplArray.addAll(IDs);
                                    pplAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pop2up, container, false);
    }
}