package com.example.qrky;
// fix community fragment
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link communityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class communityFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final FirebaseFirestore db = null;
    ListView listView;



    public communityFragment() {
        // Required empty public constructor
    }

    /**
     * do java docs
     * */
    // TODO: Rename and change types and number of parameters
    public static communityFragment newInstance() {
        communityFragment fragment = new communityFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_community, container, false);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view,savedInstanceState);

            String[] profiles = {"Soup                                                  396",
                    "warms                                               450 ",
                    "Franco                                               486",
                    "pr0d1gy                                           9001",
                    "Maia                                                   572",
                    "jjbo                                                     999"};
            ListView listview = view.findViewById(R.id.CommunityCodeList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,profiles);

            listview.setAdapter(adapter);
            listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position ==0) {
            Toast.makeText(getActivity(),"Rank 1",Toast.LENGTH_SHORT).show();
        }

        if (position ==1) {
            Toast.makeText(getActivity(),"Rank 2",Toast.LENGTH_SHORT).show();

        }
        if (position ==2) {
            Toast.makeText(getActivity(),"Rank 3",Toast.LENGTH_SHORT).show();

        }
        if (position ==3) {
            Toast.makeText(getActivity(),"Rank 4",Toast.LENGTH_SHORT).show();
        }
        if (position ==4) {
            Toast.makeText(getActivity(),"Rank 5",Toast.LENGTH_SHORT).show();
        }
    }


}