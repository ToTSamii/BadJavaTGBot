package com.example;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.example.bot.Bot;

public class App {
    public static void main(String[] args) throws IOException, GeneralSecurityException, TelegramApiException {
        TelegramBotsApi tg = new TelegramBotsApi(DefaultBotSession.class);
        tg.registerBot(new Bot());
    }
}