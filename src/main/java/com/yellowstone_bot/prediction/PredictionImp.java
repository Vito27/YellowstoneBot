package com.yellowstone_bot.prediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PredictionImp implements Prediction {
    private List<Match> matchesForPrediction;
    private List<Referee> refereesForPrediction;
    private List<Team> teamsForPrediction;


    public PredictionImp(String parserURL) throws IOException {
        PredictionDetails pr = new PredictionDetails(parserURL);
        this.matchesForPrediction = pr.getMatches();
        this.refereesForPrediction = pr.getRefereesForPrediction();
        this.teamsForPrediction = pr.getTeamsForPrediction();
    }

    @Override
    public List<String> getPrediction() {
        String todayDate = new Date().toString().substring(8, 10);
        List<Match> todayMatches = new ArrayList<>();
        for (Match match : matchesForPrediction) {
            String matchDate = match.getDate().substring(0, 2);
            if (todayDate.equals(matchDate) && match.getReferee() != null) {
                todayMatches.add(match);
            }
        }
        if (todayMatches.isEmpty()) {
            return null;
        }

        List<String> result = new ArrayList<>();
        for (Match match : todayMatches) {
            boolean isMatchReady = teamsForPrediction.contains(match.getHome()) &&
                    teamsForPrediction.contains(match.getAway()) &&
                    refereesForPrediction.contains(match.getReferee());
            if (isMatchReady) {
                float homeAVGCards = match.getHome().getAVGYellowCardsHome() + match.getHome().getAVGYellowCards();
                float awayAVGCards = match.getAway().getAVGYellowCardsAway() + match.getAway().getAVGYellowCards();
                float refereeAVGCards = match.getReferee().getAVGYellowCardsInMatch();
                float cardsQuantity = (homeAVGCards + awayAVGCards + refereeAVGCards) / 5;
                cardsQuantity = getCardsQuantity(cardsQuantity);
                result.add(match.getHome().getName() + " – " + match.getAway().getName() + " \uD83D\uDD38TБ " + cardsQuantity + "\uD83D\uDD38");
            }
        }
        return result;
    }

    private float getCardsQuantity(float cardsQuantity) {
        if (cardsQuantity < 3) {
            return 3;
        } else if (cardsQuantity > 3 && cardsQuantity <= 3.5) {
            return (float) 3.5;
        } else if (cardsQuantity > 3.5 && cardsQuantity <= 4) {
            return 4;
        } else if (cardsQuantity > 4 && cardsQuantity <= 4.5) {
            return (float) 4.5;
        } else {
            return 5;
        }
    }
}