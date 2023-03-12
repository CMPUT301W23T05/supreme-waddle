package com.example.qrky;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


public class communityFragment extends Fragment {
    OtherUsersCodesViewModel otherUsersCodesVM;
    CommunityViewModel communityVM;
    SearchView playersSearch;
    ListView playersBriefList;

////     TODO: Rename parameter arguments, choose names that match
////      the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
////    TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;


    public communityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("CommunityFragment", "Looking at Community Fragment");
        super.onCreate(savedInstanceState);

        // call ViewModels
        otherUsersCodesVM = new ViewModelProvider(requireActivity()).get(OtherUsersCodesViewModel.class);
        communityVM = new ViewModelProvider(requireActivity()).get(CommunityViewModel.class);
        communityVM.getPlayersAndScores();  // add data
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_community, container, false);

        // make list of players and scores
        playersBriefList = (ListView) view.findViewById(R.id.communityCodesList);
        CommunityAdapter adapter = new CommunityAdapter(requireActivity(), communityVM.playerAndScore);
        playersBriefList.setAdapter(adapter);

        // get the search bar
        playersSearch = view.findViewById(R.id.playersSearch);
        setPrettyFont();  // set text font to Josefin Sans Semi-bold
        // TODO: add search functionality

        // TODO: move this button to OtherUsers
        Button OtherUsersCodesButton = view.findViewById(R.id.SeeOtherUserCodes);
        OtherUsersCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new OtherUsersCodes();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                // send playerID from community fragment to otherUsersCodes fragment
//                Bundle bundle = new Bundle();
//                bundle.putString("playerID", "playerID");
//                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.nav_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void setPrettyFont() {
        int id = playersSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = (TextView) playersSearch.findViewById(id);
        Typeface myCustomFont = ResourcesCompat.getFont(requireActivity(), R.font.josefin_sans_semibold);
        searchText.setTypeface(myCustomFont);
    }
}