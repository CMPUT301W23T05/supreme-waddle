package com.example.qrky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import android.widget.SearchView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;


/**
 * Unit tests for CommunityFragment
 *
 * @author Franco Bonilla
 */
public class CommunityFragmentTest {
    private CommunityFragment fragment;
    private FirebaseFirestore db;
    private CollectionReference collRef;
    private CommunityAdapter adapter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private HashMap<String, String> pAndS = new HashMap<>();  // username and score
    private HashMap<String, String> mPAndS = new HashMap<>();  // username and score that match the search query


    @Before
    public void setUp() {
        fragment = new CommunityFragment();
        db = mock(FirebaseFirestore.class);
        collRef = mock(CollectionReference.class);
        adapter = mock(CommunityAdapter.class);
        searchView = mock(SearchView.class);
        recyclerView = mock(RecyclerView.class);

        fragment.qrkyDB = db;
        fragment.playersCollection = collRef;
        fragment.commAdapter = adapter;
        fragment.playersSearch = searchView;
        fragment.playersBriefList = recyclerView;
        fragment.playerAndScore = pAndS;
        fragment.matchingPlayerAndScore = mPAndS;
    }

    @Test
    public void testEmptyLeaderboard() {
        fragment.getAllPlayersAndScores();
        assertEquals(0, fragment.playerAndScore.size());
    }

}
