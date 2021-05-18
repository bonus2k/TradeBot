package com.example.tradebot.service;

import com.example.tradebot.config.TelegramBotConfig;
import com.example.tradebot.domain.User;
import com.example.tradebot.repos.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class TelegramService extends DefaultAbsSender implements WebhookBot {

    final TelegramBotConfig config;
    final UserRepo userRepo;


    public TelegramService(TelegramBotConfig config, UserRepo userRepo) {
        super(config);
        this.config = config;
        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl(config.getUrl());
        try {
            setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        this.userRepo = userRepo;
    }

    public String addUser(User user) {
        String memcache_key = UUID.randomUUID().toString();
        user = userRepo.findById(user.getId()).get();
        user.setActivationCode(memcache_key);
        userRepo.save(user);
        return memcache_key;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        if (update.getMessage().getText().contains("/start")) {
            addTelegramChatIdUser(update);
        }
        return null;
    }

    private void addTelegramChatIdUser(Update update) {

        String memcache_key = update.getMessage().getText();
        memcache_key = memcache_key.substring(memcache_key.indexOf(" ")+1);
        User user = userRepo.findByActivationCode(memcache_key);
        if (user != null) {
            user.setTelegram_chat_id(update.getMessage().getChatId());
            user.setActivationCode(null);
            userRepo.save(user);
        }
    }

    public void sendAll(String message){
        List<User> adminList = userRepo.findAll();
        for (User admin: adminList) {
            if (admin.getTelegram_chat_id() != null) {
                SendMessage msg = new SendMessage();
                msg.setText(message);
                msg.setChatId(admin.getTelegram_chat_id().toString());
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        try {
            final RestTemplate rest = new RestTemplate();
            final HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "application/json");

            final String setWebhookUrl = String.format("https://api.telegram.org/bot%s/%s?url=%s", getBotToken(), SetWebhook.PATH, config.getUrl());
            rest.exchange(setWebhookUrl, HttpMethod.POST, new HttpEntity<>(setWebhook, headers), ApiResponse.class);
        } catch (Exception e) {
            throw new TelegramApiRequestException("Error executing setWebHook method", e);
        }
    }

    @Override
    public String getBotPath() {
        return config.getUrl();
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
