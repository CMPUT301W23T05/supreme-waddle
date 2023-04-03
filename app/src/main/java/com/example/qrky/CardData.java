package com.example.qrky;

/**
 * A class representing a card with a title and score.
 */
public class CardData {
    private String title;
    private String hash;
    private int score;
    private int rarity;
    private String facepath;

    public String getFacepath() {
        return facepath;
    }

    public void setFacepath(String facepath) {
        this.facepath = facepath;
    }
    public CardData(String title, int score, String hash, String facepath) {
        this.title = title;
        this.score = score;
        this.hash = hash;
        this.facepath = facepath;
    }
    /**
     * Constructs a new CardData object with the given title and score.
     * @param title the title of the card
     * @param score the score of the card
     */
    public CardData(String title, int score, String hash) {
        this.hash = hash;
        this.title = title;
        this.score = score;
    }
    /**
     * Gets the title of the card.
     * @return the title of the card
     */
    public String getTitle() {
        return title;
    }
    /**
     * Gets the score of the card.
     * @return the score of the card
     */
    public int getScore() {
        return score;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public int getRarity() {
        return this.rarity;
    }
    public String getHash() {
        return hash;
    }
}
