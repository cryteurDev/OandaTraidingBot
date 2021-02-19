package main.bot;

import main.main.Utilits;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.io.File;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    static BotSession botSession;
    private static Bot instance;

    private String BOT_NAME = "trading_max_bot";
    private String BOT_TOKEN = "1476582107:AAHLuZ3SJSYP1-y8abqnua_mKlGiXVWGedI";

    private Bot() {
    }

    public static Bot getBotInstance() {
        if (instance == null)
            instance = new Bot();

        return instance;
    }

    public static void start() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            botSession = telegramBotsApi.registerBot(instance = new Bot());

            System.out.println("Bot starting...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        botSession.stop();
        System.out.println("stopped");
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {

    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public void sendMsg(String id, String text) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(id)
                .setText(text)
                .disableWebPagePreview();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendScreen(String fileName, String user, String text) {
        Utilits.sleep(1);
        SendPhoto sf = new SendPhoto();
        sf.setChatId(user);
        sf.setPhoto(new File(fileName));
        if (!text.equals(""))
            sf.setCaption(text);
        try {
            execute(sf);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}