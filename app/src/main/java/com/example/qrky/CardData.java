package com.example.qrky;

public class CardData {
    private String title;
    private int score;

    public CardData(String title, int score) {
        this.title = title;
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public int getScore() {
        return score;
    }
}
