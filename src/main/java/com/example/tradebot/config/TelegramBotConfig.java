package com.example.tradebot.config;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import javax.validation.constraints.NotEmpty;

@Configuration
@Validated
public class TelegramBotConfig extends DefaultBotOptions {
    @Value("${telegram.bot.token}")
    @NotNull
    @NotEmpty
    private String token;

    @Value("${telegram.bot.username}")
    @NotNull
    @NotEmpty
    private String username;

    @Value("${telegram.bot.url}")
    @NotNull
    @NotEmpty
    private String url;

    @Value("${telegram.bot.max_connections}")
    @NotNull
    @NotEmpty
    private int maxConnections;

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}

