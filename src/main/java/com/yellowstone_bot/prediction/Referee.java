package com.yellowstone_bot.prediction;

public class Referee {
    private String name;
    private float AVGYellowCardsInMatch;

    public Referee(String name, float AVGYellowCardsInMatch) {
        this.name = name;
        this.AVGYellowCardsInMatch = AVGYellowCardsInMatch;
    }

    public String getName() {
        return name;
    }

    public float getAVGYellowCardsInMatch() {
        return AVGYellowCardsInMatch;
    }

    @Override
    public String toString() {
        return "Referee name:" + name + ", AVGYellowCardsInMatch:" + AVGYellowCardsInMatch;
    }
}