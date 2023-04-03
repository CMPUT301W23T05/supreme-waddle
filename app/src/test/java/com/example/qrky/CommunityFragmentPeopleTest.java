package com.example.qrky;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;


/**
 * Unit tests for CommunityFragmentPeople.
 *
 * @author Franco Bonilla
 * @see CommunityFragmentPeople
 */
@RunWith(MockitoJUnitRunner.class)
public class CommunityFragmentPeopleTest {
    @Mock
    private FirebaseFirestore db;
    @Mock
    private CollectionReference collectionReference;
    @Mock
    private CommunityAdapter adapter;
    @Mock
    private androidx.appcompat.widget.SearchView searchView;
    @Mock
    private RecyclerView recyclerView;
    private CommunityFragmentPeople fragment;

    @Before
    public void setUp() {
        fragment = new CommunityFragmentPeople();
        fragment.playersCollection = collectionReference;
        fragment.qrkyDB = db;
        fragment.commAdapter = adapter;
        fragment.playersSearch = searchView;
        fragment.playersBriefList = recyclerView;
    }

    @Test
    public void testGetAllPlayersAndScores() {
        when(db.collection(eq("Players"))).thenReturn(collectionReference);
        when(collectionReference.addSnapshotListener(any())).thenReturn(() -> {});
        fragment.getAllPlayersAndScores();
        verify(collectionReference).addSnapshotListener(any());
    }

    @Test
    public void testSearchPlayer() {
        String query = "test";
        HashMap<String, String> playerAndScore = new HashMap<>();
        playerAndScore.put("testuser1", "100");
        playerAndScore.put("testuser2", "200");
        playerAndScore.put("testuser3", "50");
        HashMap<String, String> playerAndRank = new HashMap<>();
        playerAndRank.put("testuser1", "1");
        playerAndRank.put("testuser2", "2");
        playerAndRank.put("testuser3", "3");

        // now put playerAndScore into the adapter
        fragment.commAdapter = new CommunityAdapter(playerAndScore, playerAndRank);

        // now test if the search works
        fragment.searchAPlayer();
        verify(searchView).setOnQueryTextListener(any());
    }
}
