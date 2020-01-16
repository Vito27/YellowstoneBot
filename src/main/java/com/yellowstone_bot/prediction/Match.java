package com.yellowstone_bot.prediction;

public class Match {
    private Team home;
    private Team away;
    private Referee referee;
    private String date;
    private String time;

    public Team getHome() {
        return home;
    }

    public Team getAway() {
        return away;
    }

    public Referee getReferee() {
        return referee;
    }

    public String getDate() {
        return date;
    }

    public Match(Team home, Team away, Referee referee, String date, String time) {
        this.home = home;
        this.away = away;
        this.referee = referee;
        this.date = date;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Home:" + home +
                ", away:" + away +
                ", referee:" + referee +
                ", date " + date +
                ", time " + time;
    }
}