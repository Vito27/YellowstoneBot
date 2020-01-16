package com.yellowstone_bot.prediction;

import com.yellowstone_bot.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchDetails {
    private List<Team> allTeamsList;
    private List<Referee> allRefereesList;
    private String[] nextMatchesArr;

    public MatchDetails(String parserURL) throws IOException {
        Parser parser = new Parser(parserURL);
        this.allTeamsList = initAllTeams(parser);
        this.allRefereesList = initAllReferees(parser);
        this.nextMatchesArr = parser.getNextMatches();
    }

    private static List<Team> initAllTeams(Parser parserURL) throws IOException {
        List<Team> result = new ArrayList<>();
        Map<String, String> TeamsAndStatsMap = parserURL.gepTeamsAndStatsMap();
        for (Map.Entry<String, String> entry : TeamsAndStatsMap.entrySet()) {
            String[] statsArr = entry.getValue().split(",");
            result.add(new Team(entry.getKey(), Float.parseFloat(statsArr[0]), Float.parseFloat(statsArr[1]),
                    Float.parseFloat(statsArr[2])));
        }
        return result;
    }

    private static List<Referee> initAllReferees(Parser parser) {
        List<Referee> result = new ArrayList<>();
        String[] refereesArr = parser.getReferees().toArray(String[]::new);
        for (String str : refereesArr) {
            String[] refereesAttribute = str.split(" ");
            StringBuilder refereeName = new StringBuilder();
            float ignoreNum = 0;
            float refereeMatchesNum = 0;
            float refereeYellowCardsNum = 0;
            for (int startRefereeAttr = 1; startRefereeAttr < refereesAttribute.length; startRefereeAttr++) {
                if (refereesAttribute[startRefereeAttr].matches("[а-яА-ЯёЁa-zA-Z-]+")) {
                    refereeName.append(refereesAttribute[startRefereeAttr]);
                    refereeName.append(" ");
                } else if (refereesAttribute[startRefereeAttr].contains(".")) {
                    continue;
                } else if (ignoreNum == 0) {
                    ignoreNum = Float.parseFloat(refereesAttribute[startRefereeAttr]);
                } else if (refereeMatchesNum == 0) {
                    refereeMatchesNum = Float.parseFloat(refereesAttribute[startRefereeAttr]);
                } else {
                    refereeYellowCardsNum = Float.parseFloat(refereesAttribute[startRefereeAttr]);
                    break;
                }
            }
            if (refereeMatchesNum != 0 && refereeYellowCardsNum != 0) {
                result.add(new Referee(refereeName.toString().trim(), refereeYellowCardsNum / refereeMatchesNum));
            }
        }
        return result;
    }

    public List<Team> getAllTeamsList() {
        return allTeamsList;
    }

    public List<Referee> getAllRefereesList() {
        return allRefereesList;
    }

    public String[] getNextMatchesArr() {
        return nextMatchesArr;
    }
}