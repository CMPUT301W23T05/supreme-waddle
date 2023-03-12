package com.example.qrky;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class libraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter adapter;

    private List<CardData> allCards;
    private List<CardData> filteredCards;
    private SearchView searchView;

    public libraryFragment() {
        // Required empty public constructor
    }


//...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerView = view.findViewById(R.id.normal_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        allCards = cards();
        filteredCards = new ArrayList<>(allCards);

        adapter = new CardAdapter(filteredCards);
        recyclerView.setAdapter(adapter);

        searchView = view.findViewById(R.id.search_view);

        Button profileButton =  view.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileFragment userFragment = userProfileFragment.newInstance();
                userFragment.show(getChildFragmentManager(),"ss");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredCards.clear();
                if (newText.isEmpty()) {
                    filteredCards.addAll(allCards);
                } else {
                    for (CardData card : allCards) {
                        if (card.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                            filteredCards.add(card);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return view;
    }


    private List<CardData> cards() {
        List<CardData> cards = new ArrayList<>();
        String[] titles = {"QR Code 1", "QR Code 2", "QR Code 3", "QR Code 4"};

        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            int score = 0;
            for (int j = 0; j < title.length(); j++) {
                char c = title.charAt(j);
                if (Character.isLetter(c)) {
                    score += (int) c;
                }
            }
            cards.add(new CardData(title, score));
        }

        return cards;
    }




}

