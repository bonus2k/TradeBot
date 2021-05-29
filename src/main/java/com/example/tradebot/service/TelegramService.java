package com.example.tradebot.service;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.example.tradebot.config.TelegramBotConfig;
import com.example.tradebot.domain.Alerts;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import com.example.tradebot.domain.usrOrder;
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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class TelegramService extends DefaultAbsSender implements WebhookBot {

    final TelegramBotConfig config;
    final UserRepo userRepo;
    private final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MMMM в HH:mm:ss");


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
        Optional<Update> updateOptional = Optional.ofNullable(update);
        String text = updateOptional
                .map(Update::getMessage)
                .map(Message::getText)
                .orElseGet(String::new);
        if (!text.isEmpty() && text.contains("/start")) {
            addTelegramChatIdUser(update);
        }
        return null;
    }

    private void addTelegramChatIdUser(Update update) {

        String memcache_key = update.getMessage().getText();
        memcache_key = memcache_key.substring(memcache_key.indexOf(" ") + 1);
        User user = userRepo.findByActivationCode(memcache_key);
        if (user != null) {
            user.setTelegram_chat_id(update.getMessage().getChatId());
            user.setActivationCode(null);
            userRepo.save(user);
        }
    }

    public void sendAllAlert(Alerts alerts) {
        String message = String.format("\uD83D\uDD14Покупка %s, пара: %s, цена: %.3f, сигнал от: %s",
                FORMATTER.format(alerts.getDate()), alerts.getSymbol(), alerts.getPrice(), alerts.getBotName());
        List<User> adminList = userRepo.findAll();
        for (User admin : adminList) {
            sendMsg(message, admin);
        }
    }

    private void sendMsg(String message, User user) {
        if (user.getTelegram_chat_id() != null) {
            SendMessage msg = new SendMessage();
            msg.setText(message);
            msg.setChatId(user.getTelegram_chat_id().toString());
            try {
                execute(msg);
            } catch (TelegramApiException e) {
                log.error(user.getUsername()+": "+e.getMessage());
                if ("bot was blocked by the user".equals(e.getMessage())){
                    user.setTelegram_chat_id(null);
                    userRepo.save(user);
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

    public void sendUserOrder(User user, usrOrder order) {
        String message;
        if (order.getStatus() == OrderStatus.NEW && order.getType() == OrderType.LIMIT) {
            message = String.format("\uD83D\uDEA9Размещен лимитный ордер на продажу №%s, %s, пара: %s, курс: %s, объем: %s %s",
                    order.getOrderId(),
                    FORMATTER.format(order.getTime()),
                    order.getSymbol(),
                    replaceZero(order.getPrice()),
                    replaceZero(order.getOrigQty()),
                    Symbol.valueOf(order.getSymbol()).getAsset());
            sendMsg(message, user);
            return;
        }
        if (order.getStatus() == OrderStatus.FILLED && order.getType() == OrderType.LIMIT) {
            message = String.format("\u2714Ордер на продажу №%s исполнен, пара: %s, курс: %s, объем: %s %s %sPNL: %s USDT",
                    order.getOrderId(),
                    order.getSymbol(),
                    replaceZero(order.getPrice()),
                    replaceZero(order.getOrigQty()),
                    Symbol.valueOf(order.getSymbol()).getAsset(),
                    (order.getProfit() > 0) ? "\uD83D\uDCC8" : "\uD83D\uDCC9",
                    order.getProfit());
            sendMsg(message, user);
            return;
        }
        if (order.getType() == OrderType.MARKET
                && order.getSide() == OrderSide.BUY) {
            message = String.format("\uD83D\uDCE5Покупка %s, курс: %s, объем: %s %s на %.2f USDT",
                    FORMATTER.format(order.getTime()),
                    replaceZero(order.getPrice()),
                    replaceZero(order.getOrigQty()),
                    Symbol.valueOf(order.getSymbol()).getAsset(),
                    order.getSum());
            sendMsg(message, user);
            return;
        }
        if (order.getType() == OrderType.MARKET
                && order.getSide() == OrderSide.SELL) {
            message = String.format("\uD83D\uDCE4Продажа %s, курс: %s, объем: %s %s на %.2f USDT %sPNL: %s USDT",
                    FORMATTER.format(order.getTime()),
                    replaceZero(order.getPrice()),
                    replaceZero(order.getOrigQty()),
                    Symbol.valueOf(order.getSymbol()).getAsset(),
                    order.getSum(),
                    (order.getProfit() > 0) ? "\uD83D\uDCC8" : "\uD83D\uDCC9",
                    order.getProfit());
            sendMsg(message, user);
            return;
        }
        if (order.getStatus() == OrderStatus.CANCELED) {
            message = String.format("\u274CЛимитный ордер №%s отменен %s",
                    order.getOrderId(),
                    FORMATTER.format(order.getTime()));
            sendMsg(message, user);
            return;
        }
    }

    private String replaceZero(String str) {
        return str.replaceAll("[\\.0]*$", "");
    }
}
