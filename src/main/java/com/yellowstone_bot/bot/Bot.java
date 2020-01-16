package com.yellowstone_bot.bot;

import com.yellowstone_bot.prediction.Prediction;
import com.yellowstone_bot.prediction.PredictionImp;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    private List<Prediction> mainPredictionsList;
    private String mainDate = "0";
    private final List<String> strForMatchesWithPredictionList = Arrays.asList(
            "Привет друг, вот мой прогноз на сегодня:\n",
            "Я думаю, это должно зайти:\n",
            "Вот прогноз, не благодари:\n",
            "Попробуй вот это, но это не точно:\n",
            "Я конечно не Нострадамус, но это верочка:\n",
            "Ладно, так уж и быть, попробуй вот это:\n",
            "Think about it:\n",
            "Надеюсь, если зайдет, ты поделишься:\n",
            "Под мою ответственность:\n",
            "Судья мой родственник, так что не парься:\n",
            "\uD83E\uDD11\uD83E\uDD11\uD83E\uDD11\n"
    );
    private final List<String> strForMatchesWithoutPredictionList = Arrays.asList(
            "К сожалению, на этот чемпионат, у меня на сегодня нет прогноза 😔",
            "Не ругайся, но у меня нет прогноза на этих ребят сегодня",
            "По моим данным, сегодня ребята договорились играть чисто (но это не точно), так что мы остались без прогноза",
            "Сегодня нет прогноза на этот чемпионат",
            "Прости, сегодня без прогноза на этот чемпионат"
    );
    private final String strForNotMatchesDay = "Сегодня нет матчей этого чемпионата, заходи в другой день!\uD83D\uDC4B";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "Yellowstone";
    }

    public String getBotToken() {
        return "1041246602:AAGWCLLwqxk3VS_lB3itRKpYHGo07OhBcrA";
    }

    private synchronized List<String> getStrPredictionList() throws IOException {
        String todayDate = new Date().toString().substring(8, 10);
        if (!todayDate.equals(mainDate)) {
            mainDate = todayDate;
            mainPredictionsList = new ArrayList<>();
            mainPredictionsList.add(new PredictionImp("https://www.championat.com/football/_spain/tournament/3001/"));
            mainPredictionsList.add(new PredictionImp("https://www.championat.com/football/_england/tournament/2995/"));
            mainPredictionsList.add(new PredictionImp("https://www.championat.com/football/_germany/tournament/2999/"));
            mainPredictionsList.add(new PredictionImp("https://www.championat.com/football/_italy/tournament/2997/"));
            mainPredictionsList.add(new PredictionImp("https://www.championat.com/football/_france/tournament/3003/"));
        }
        List<String> result = new ArrayList<>();
        for (Prediction prediction : mainPredictionsList) {
            List<String> predictionForMatchList = prediction.getPrediction();
            Random random = new Random();
            if (predictionForMatchList == null) {
                result.add(strForNotMatchesDay);
            } else if (predictionForMatchList.isEmpty()) {
                result.add(strForMatchesWithoutPredictionList.get(random.nextInt(5)));
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(strForMatchesWithPredictionList.get(random.nextInt(11)));
                stringBuilder.append("\n");
                for (String predictionStr : predictionForMatchList) {
                    stringBuilder.append(predictionStr);
                    stringBuilder.append("\n");
                }
                result.add(stringBuilder.toString());
            }
        }
        return result;
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    String startStr = "Привет, " + message.getFrom().getFirstName() +
                            "! Я был создан для того, чтобы собирать всю нужную статистику" +
                            " и прогнозировать количество ЖК в определенных матчах, мой алгоритм не идеален" +
                            ", но я учусь и буду становиться лучше, выбери чемпионат внизу и желаю тебе удачи!\uD83D\uDE09";
                    sendMsg(message, startStr);
                    break;
                case "\uD83C\uDDE9\uD83C\uDDEA Bundesliga":
                    try {
                        sendMsg(message, getStrPredictionList().get(2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83C\uDDEA\uD83C\uDDF8 La Liga":
                    try {
                        sendMsg(message, getStrPredictionList().get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83C\uDDEB\uD83C\uDDF7 Ligue 1":
                    try {
                        sendMsg(message, getStrPredictionList().get(4));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83C\uDDEE\uD83C\uDDF9 Serie A":
                    try {
                        sendMsg(message, getStrPredictionList().get(3));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F" +
                        " Premier League":
                    try {
                        sendMsg(message, getStrPredictionList().get(1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83E\uDDD0":
                    String infoStr = "Привет, я всегда даю прогнозы утром в день матчей чемпионата на один игровой день," +
                            " поэтому лучше пользуйся мной в день матчей\uD83D\uDE09\nЯ стараюсь выбрать матчи в которых" +
                            " вероятность ЖК выше всего, но к сожалению, футбол – это спорт и иногда у меня тоже может быть сбой\uD83D\uDE14\n" +
                            message.getFrom().getFirstName() + ", желаю тебе удачи и хорошего дня\uD83D\uDE0A";
                    sendMsg(message, infoStr);
                    break;
                default:
                    sendMsg(message, "Я тебя не понимаю, попробуй еще раз!");
                    break;
            }
        }
    }

    private void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();

        keyboardRow1.add("\uD83C\uDDE9\uD83C\uDDEA Bundesliga");
        keyboardRow1.add("\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F" + " Premier League");
        keyboardRow2.add("\uD83C\uDDEB\uD83C\uDDF7 Ligue 1");
        keyboardRow2.add("\uD83C\uDDEE\uD83C\uDDF9 Serie A");
        keyboardRow3.add("\uD83C\uDDEA\uD83C\uDDF8 La Liga");
        keyboardRow3.add("\uD83E\uDDD0");

        keyboardRowList.add(keyboardRow1);
        keyboardRowList.add(keyboardRow2);
        keyboardRowList.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}