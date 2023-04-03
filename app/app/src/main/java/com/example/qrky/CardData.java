package com.example.qrky;

/**
 * A class representing a card with a title and score.
 */
public class CardData {
    private String title;
    private int score;
    /**
     * Constructs a new CardData object with the given title and score.
     * @param title the title of the card
     * @param score the score of the card
     */
    public CardData(String title, int score) {
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
}
