package com.yellowstone_bot.prediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PredictionDetails {
    private MatchDetails dataOfMatches;

    public PredictionDetails(String parserURL) throws IOException {
        this.dataOfMatches = new MatchDetails(parserURL);
    }

    private Team getTeamByName(String nameStr) {
        for (Team team : dataOfMatches.getAllTeamsList()) {
            if (nameStr.equals(team.getName())) {
                return team;
            }
        }
        throw new RuntimeException("Server Error");
    }

    private Referee getRefereeByName(String nameStr) {
        for (Referee referee : dataOfMatches.getAllRefereesList()) {
            String completeRefereeName = referee.getName();
            String[] refereeNameInitials = nameStr.split(" ");
            boolean flag = true;
            for (int i = 0; i < refereeNameInitials.length; i++) {
                if (!completeRefereeName.contains(refereeNameInitials[i])) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return referee;
            }
        }
        return null;
    }

    protected List<Match> getMatches() {
        List<Match> nextMatchesList = new ArrayList<>();
        String[] nextMatchesArr = dataOfMatches.getNextMatchesArr();
        for (String nextMatch : nextMatchesArr) {
            String[] nextMatchAttr = nextMatch.split(" ");
            String date = nextMatchAttr[0];
            String time = nextMatchAttr[1];
            String home = "";
            String away = "";
            String referee = "";
            int matchAttrSize = nextMatchAttr.length;
            boolean firstTeamFullNameEnd = false;
            for (int startMatchAttr = 2; startMatchAttr < matchAttrSize; startMatchAttr++) {
                if (nextMatchAttr[startMatchAttr].equals("|")) {
                    int num = nextMatch.indexOf("|") + 2;
                    referee = nextMatch.substring(num);
                    break;
                } else if (nextMatchAttr[startMatchAttr].equals("–")) {
                    firstTeamFullNameEnd = true;
                } else if (!firstTeamFullNameEnd) {
                    home += nextMatchAttr[startMatchAttr] + " ";
                } else {
                    away += nextMatchAttr[startMatchAttr] + " ";
                }
            }
            nextMatchesList.add(new Match(getTeamByName(home.trim()), getTeamByName(away.trim()), getRefereeByName(referee), date, time));
        }
        return nextMatchesList;
    }

    protected List<Referee> getRefereesForPrediction() {
        List<Referee> allRefereesList = dataOfMatches.getAllRefereesList();
        List<Referee> result = new ArrayList<>();
        float allRefereesYellowCards = 0;
        for (Referee referee : allRefereesList) {
            allRefereesYellowCards += referee.getAVGYellowCardsInMatch();
        }
        float refereesAVGYellowCards = allRefereesYellowCards / allRefereesList.size();
        for (Referee referee : allRefereesList) {
            if (refereesAVGYellowCards < referee.getAVGYellowCardsInMatch()) {
                result.add(referee);
            }
        }
        return result;
    }

    protected List<Team> getTeamsForPrediction() {
        List<Team> allTeamsList = dataOfMatches.getAllTeamsList();
        List<Team> result = new ArrayList<>();
        float allTeamsYellowCards = (float) allTeamsList.stream().mapToDouble(Team::getAVGYellowCards).sum();
        float teamsAVGYellowCards = allTeamsYellowCards / allTeamsList.size();
        for (Team team : allTeamsList) {
            if (teamsAVGYellowCards < team.getAVGYellowCards()) {
                result.add(team);
            }
        }
        return result;
    }
}