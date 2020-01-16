package com.yellowstone_bot.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Parser {
    private Document nextMatchesMainPage;
    private Document teamsMainPage;
    private Document refereesMainPage;
    private int numOfTeams;

    public Parser(String URL) throws IOException {
        this.nextMatchesMainPage = Jsoup.connect(URL).timeout(15000).get();
        this.teamsMainPage = Jsoup.connect(URL + "teams").timeout(15000).get();
        this.refereesMainPage = Jsoup.connect(URL + "referees/").timeout(15000).get();
    }

    private String[] getTeamsNames() {
        return teamsMainPage.select("div.teams-item__name").html().split("\n");
    }

    private List<String> getTeamsStatsLinks() throws IOException {
        List<String> teamsMainsPagesLinks = getTeamsMainPagesLinks();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < numOfTeams; i++) {
            Document doc = Jsoup.connect(teamsMainsPagesLinks.get(i)).timeout(15000).get();
            result.add(doc.
                    select("div.tournament-tabs").
                    select("a").
                    get(3).
                    absUrl("href"));
        }
        return result;
    }

    private List<String> getTeamsStats() throws IOException {
        List<String> teamsStatsLinks = getTeamsStatsLinks();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < numOfTeams; i++) {
            Document document = Jsoup.connect(teamsStatsLinks.get(i)).timeout(15000).get();
            String[] yellowCards = document.select("table[class=table table-stripe _indent-top]").select("td").text().split(" ");
            String str = yellowCards[61] + "," + yellowCards[63] + "," + yellowCards[65];
            result.add(str);
        }
        return result;
    }

    public Map<String, String> gepTeamsAndStatsMap() throws IOException {
        Map<String, String> result = new HashMap<>();
        String[] teamsNames = getTeamsNames();
        List<String> teamsStats = getTeamsStats();
        for (int i = 0; i < numOfTeams; i++) {
            result.put(teamsNames[i], teamsStats.get(i));
        }
        return result;
    }

    public List<String> getReferees() {
        Elements elements = refereesMainPage.select("tr.table-responsive__row");
        List<String> result = new ArrayList<>();
        for (Element element : elements) {
            result.add(element.text());
        }
        result.remove(0);
        return result;
    }

    private List<String> getTeamsMainPagesLinks() {
        List<String> result = new ArrayList<>();
        Elements elements = teamsMainPage.select("a.teams-item__link");
        for (Element element : elements
        ) {
            result.add((String.format(element.attr("abs:href"), element.text())));
        }
        numOfTeams = result.size();
        return result;
    }

    public String[] getNextMatches() throws IOException {
        List<String> refereesName = new ArrayList<>();
        List<String> matchDetails = new ArrayList<>();
        List<String> result = new ArrayList<>();
        Elements elements = nextMatchesMainPage.select("tr[class=stat-results__row][data-played=0]");
        List<String> matchesDetailsLinksList = nextMatchesMainPage.
                select("tr[class=stat-results__row][data-played=0]").
                select("td[class=stat-results__count]").
                select("a[href]").
                eachAttr("href");
        for (String match : matchesDetailsLinksList) {
            Document matchDetailsPage = Jsoup.connect("https://www.championat.com" + match).timeout(15000).get();
            Elements matchDetailsElements = matchDetailsPage.select("div[class=match-info__extra-row]");
            if (matchDetailsElements.size() > 1) {
                StringBuilder refereeNameStringBuilder = new StringBuilder(matchDetailsElements.get(1).text());
                refereeNameStringBuilder.delete(0, 15);
                int deleteFrom = refereeNameStringBuilder.indexOf("(");
                int deleteTo = refereeNameStringBuilder.lastIndexOf(")");
                refereeNameStringBuilder.delete(deleteFrom - 1, deleteTo + 1);
                refereesName.add(refereeNameStringBuilder.toString());
            } else refereesName.add(null);
        }
        for (Element e : elements) {
            StringBuilder matchDetailsStrBuilder = new StringBuilder();
            String[] matchDetailsAttributes = e.text().split(" ");
            matchDetailsStrBuilder.append(matchDetailsAttributes[1]);
            matchDetailsStrBuilder.append(" ");
            matchDetailsStrBuilder.append(matchDetailsAttributes[2]);
            matchDetailsStrBuilder.append(" ");
            int attrSize = matchDetailsAttributes.length;
            for (int i = 7; i < attrSize; i++) {
                if (matchDetailsAttributes[i].matches("[а-яА-ЯёЁa-zA-Z–0-9-]+")) {
                    matchDetailsStrBuilder.append(matchDetailsAttributes[i]);
                    matchDetailsStrBuilder.append(" ");
                }
            }
            matchDetails.add(matchDetailsStrBuilder.toString());
        }
        for (int i = 0; i < matchDetails.size(); i++) {
            StringBuilder resultStrBuilder = new StringBuilder();
            resultStrBuilder.append(matchDetails.get(i));
            resultStrBuilder.delete(resultStrBuilder.length() - 4, resultStrBuilder.length() + 1);
            resultStrBuilder.append("| ");
            resultStrBuilder.append(refereesName.get(i));
            result.add(resultStrBuilder.toString());
        }
        return result.toArray(String[]::new);
    }
}