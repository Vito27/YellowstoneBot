package com.yellowstone_bot.prediction;

public class Team {
    private String name;
    private float AVGYellowCards;
    private float AVGYellowCardsHome;
    private float AVGYellowCardsAway;

    public Team(String name, float AVGYellowCards, float AVGYellowCardsHome, float AVGYellowCardsAway) {
        this.name = name;
        this.AVGYellowCards = AVGYellowCards;
        this.AVGYellowCardsHome = AVGYellowCardsHome;
        this.AVGYellowCardsAway = AVGYellowCardsAway;
    }

    public String getName() {
        return name;
    }

    public float getAVGYellowCards() {
        return AVGYellowCards;
    }

    public float getAVGYellowCardsHome() {
        return AVGYellowCardsHome;
    }

    public float getAVGYellowCardsAway() {
        return AVGYellowCardsAway;
    }

    @Override
    public String toString() {
        return "Team name:" + name +
                ", AVGYellowCards - " + AVGYellowCards +
                ", AVGYellowCardsHome - " + AVGYellowCardsHome +
                ", AVGYellowCardsAway - " + AVGYellowCardsAway;
    }
}