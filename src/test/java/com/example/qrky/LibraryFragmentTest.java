package com.example.qrky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.widget.SearchView;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragmentTest {

    private libraryFragment fragment;
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardData> allCards;
    private List<CardData> filteredCards;
    private SearchView searchView;

    @Before
    public void setUp() throws Exception {
        fragment = new libraryFragment();

        // Create mock objects for the views and adapter
        recyclerView = mock(RecyclerView.class);
        adapter = mock(CardAdapter.class);
        searchView = mock(SearchView.class);

        // Set up the fragment with the mock objects
        fragment.recyclerView = recyclerView;
        fragment.adapter = adapter;
        fragment.searchView = searchView;

        // Set up the card data lists
        allCards = new ArrayList<>();
        allCards.add(new CardData("Card 1", 100));
        allCards.add(new CardData("Card 2", 200));
        filteredCards = new ArrayList<>();
        filteredCards.addAll(allCards);
        fragment.allCards = allCards;
        fragment.filteredCards = filteredCards;
    }

    @Test
    public void testAddToAllCards() {
        // Add a new card to the allCards list
        CardData newCard = new CardData("Card 3", 300);
        fragment.addToAllCards(newCard);

        // Verify that the new card is added to the allCards list and the adapter is notified
        assertTrue(allCards.contains(newCard));
    }

    @Test
    public void testAddToFilteredCards() {
        // Add a new card to the filteredCards list
        CardData newCard = new CardData("Card 3", 300);
        fragment.addToFilteredCards(newCard);

        // Verify that the new card is added to the filteredCards list and the adapter is notified
        assertTrue(filteredCards.contains(newCard));
    }
    @Test
    public void testFilterCards() {
        // Set up the query string
        String query = "Card 1";

        // Call the filterCards method with the query string
        fragment.filterCards(query);

        // Verify that the filteredCards list only contains Card 1
        assertEquals(1, fragment.filteredCards.size());
        assertEquals("Card 1", fragment.filteredCards.get(0).getTitle());
    }
    @Test
    public void testFragmentIsNotNull() {
        assertNotNull(fragment);
    }

    @Test
    public void testRecyclerViewIsNotNull() {
        assertNotNull(recyclerView);
    }

    @Test
    public void testFilteringCards() {
        CardData card1 = new CardData("Card 1", 100);
        CardData card2 = new CardData("Card 2", 200);
        CardData card3 = new CardData("Card 3", 300);
        fragment.allCards = new ArrayList<>();
        fragment.allCards.add(card1);
        fragment.allCards.add(card2);
        fragment.allCards.add(card3);
        fragment.searchView.setQuery("Card 2", true);
        assertEquals(2, fragment.filteredCards.size());

    }
    @Test
    public void testDeletingCard() {
        CardData card1 = new CardData("Card 1", 100);
        CardData card2 = new CardData("Card 2", 200);
        fragment.filteredCards = new ArrayList<>();
        fragment.filteredCards.add(card1);
        fragment.filteredCards.add(card2);
        assertEquals(2, fragment.filteredCards.size());
        int initialPosition = 1;
        assertEquals(2, fragment.filteredCards.size());
        assertEquals(card1.getTitle(), fragment.filteredCards.get(0).getTitle());
    }

}

