package com.example.qrky;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CardDataTest {

    @Test
    public void testConstructorAndGetters() {
        String title = "My Card";
        int score = 10;
        String hash = "abc123";
        String facepath = "path/to/face";
        CardData cardData = new CardData(title, score, hash, facepath);
        assertEquals(title, cardData.getTitle());
        assertEquals(score, cardData.getScore());
        assertEquals(hash, cardData.getHash());
        assertEquals(facepath, cardData.getFacepath());
    }

    @Test
    public void testSetAndGetRarity() {
        CardData cardData = new CardData("My Card", 10, "abc123");
        int rarity = 5;
        cardData.setRarity(rarity);
        assertEquals(rarity, cardData.getRarity());
    }

}

