package com.example.tradebot.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.exception.BinanceApiException;
import com.example.tradebot.domain.Role;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import com.example.tradebot.domain.dto.CaptchaResponseDto;
import com.example.tradebot.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final OrderService orderService;
    private final MailSender mailSender;

    public UserService(UserRepo userRepo, OrderService orderService, MailSender mailSender) {
        this.userRepo = userRepo;
        this.orderService = orderService;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepo.findByUsername(s);
    }

    public boolean emailIsUsed(User user) {
        User userDB = userRepo.findByEmail(user.getEmail());
        if (userDB != null) return true;
        return false;
    }

    public boolean usernameIsUsed(User user) {
        User userDB = userRepo.findByUsername(user.getUsername());
        if (userDB != null) return true;
        return false;
    }

    public BindingResult addUser(User user, String password2, CaptchaResponseDto response, BindingResult bindingResult) {

        boolean usernameIsUsed = usernameIsUsed(user);
        boolean emailIsUsed = emailIsUsed(user);
        boolean password2IsCorrect = user.getPassword().equals(password2);
        boolean captchaResponse = response.isSuccess();

        if (usernameIsUsed || emailIsUsed || !password2IsCorrect || !captchaResponse) {
            if (usernameIsUsed)
                bindingResult.addError(new FieldError("usernameIsUsed", "username",
                        "Логин уже занят"));
            if (emailIsUsed)
                bindingResult.addError(new FieldError("emailIsUsed", "email",
                        "Email уже используется"));
            if (!password2IsCorrect)
                bindingResult.addError(new FieldError("password2IsCorrect", "password2",
                        "Пароли не совподают"));
            if (!captchaResponse)
                bindingResult.addError(new FieldError("captchaResponse", "captcha",
                        "Заполните капчу"));

            return bindingResult;
        }

        if (!bindingResult.hasErrors()) {
            user.setActive(false);
            user.setRoles(Collections.singleton(Role.USER));
            user.setActivationCode(UUID.randomUUID().toString());
            userRepo.save(user);
            sendActivationCode(user);
        }
        return bindingResult;
    }

    private void sendActivationCode(User user) {
        String message = String.format(
                "Здравствуйте, %s \n" +
                        "Для подтверждения email и активации учетной записи\n" +
                        "перейдите по адресу http://bot.el-service24.tech/activate/%s",
                user.getUsername(), user.getActivationCode());
        mailSender.send(user.getEmail(), "Код активации", message);
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) return false;

        user.setActivationCode(null);
        user.setActive(true);
        userRepo.save(user);

        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void userSave(String username, Map<String, String> form, User user, String isRun) {
        user.setUsername(username);
        user.setRun(isRun.equals("on"));

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        user.setComment(form.get("comment"));

        userRepo.save(user);
    }

    public void updateProfile(User user, Map<String, String> form) {

        User userDB = getUserByUsername(user);
        Boolean editKey = false;


        if (form.get("password")!=null && !form.get("password").isEmpty()) {
            userDB.setPassword(form.get("password"));
        }

        if (form.get("key") != null && !form.get("key").isEmpty()) {
            userDB.setKey(form.get("key"));
            editKey = true;
        }

        if (form.get("secret") != null && !form.get("secret").isEmpty()) {
            userDB.setSecret(form.get("secret"));
            editKey = true;
        }

        if (form.get("email") != null && !form.get("email").equalsIgnoreCase(userDB.getEmail())) {
            userDB.setEmail(form.get("email"));
            userDB.setActivationCode(UUID.randomUUID().toString());
            userDB.setActive(false);
            sendActivationCode(userDB);
        }


        Set<String> symbols = Arrays.stream(Symbol.values())
                .map(Symbol::name)
                .collect(Collectors.toSet());

        Set<Symbol> oldSymbolSet = new HashSet<>(userDB.getSymbol());

        userDB.getSymbol().clear();

        if (user.getRoles().contains(Role.ADMIN)) {
            orderService.updateTradeRule(form, symbols);
        } else {
            for (String key : form.keySet()) {
                if (symbols.contains(key)) {
                    userDB.getSymbol().add(Symbol.valueOf(key));
                }
            }
        }

        if (editKey) {
            userDB.setCanTrade(isValidKey(userDB));
        }

        userRepo.save(userDB);
        orderService.addMapRemoteSymbol(oldSymbolSet, userDB);
    }

    private boolean isValidKey(User userDB) {
        BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance(userDB.getKey(), userDB.getSecret());
        BinanceApiRestClient client = clientFactory.newRestClient();
        try {
            return client.getAccount(50000L, new Date().getTime()).isCanTrade();
        } catch (BinanceApiException e) {
            return false;
        }
    }


    public User getUserByUsername(User user) {
        return userRepo.findByUsername(user.getUsername());
    }

}
