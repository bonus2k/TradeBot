package com.example.tradebot.controller;

import com.example.tradebot.domain.User;
import com.example.tradebot.service.TelegramService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
@RequestMapping("/telegram")
public class TelegramController {

    @Value("${telegram.bot.username}")
    private String BOT;
    private final TelegramService telegramService;

    public TelegramController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @GetMapping ("{user}")
    public String registerTelegramUser(@PathVariable User user, Model model){
        String memcache_key = telegramService.addUser(user);
        return String.format("redirect:https://t.me/%s?start=%s", BOT, memcache_key);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity onUpdateReceived(@RequestBody Update update) {
        telegramService.onWebhookUpdateReceived(update);
        return new ResponseEntity(HttpStatus.OK);
    }
}
