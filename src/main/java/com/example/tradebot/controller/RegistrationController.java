package com.example.tradebot.controller;

import com.example.tradebot.domain.User;
import com.example.tradebot.domain.dto.CaptchaResponseDto;
import com.example.tradebot.service.UserService;
import com.example.tradebot.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private final UserService userService;
    private final RestTemplate restTemplate;
    private static String CAPTCHA_URL =
            "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    @Value("${captcha.secret}")
    private String secret;

    public RegistrationController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("g-recaptcha-response") String captchaResponse,
            @RequestParam("password2") String passwordConfirm,
            @Valid User user,
            BindingResult bindingResult,
            Model model) {

        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);


        if (userService.addUser(user, passwordConfirm, response, bindingResult).hasErrors()) {
            Map<String, String> mapErrors = Util.getErrors(bindingResult);
            mapErrors.entrySet().forEach(System.out::println);
            model.addAllAttributes(mapErrors);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            return "registration";
        }


        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivate = userService.activateUser(code);
        if (isActivate) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "Пользователь активирован");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Код активации не найден");
        }


        return "login";
    }
}
