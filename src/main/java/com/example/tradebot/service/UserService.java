package com.example.tradebot.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.exception.BinanceApiException;
import com.example.tradebot.domain.Role;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import com.example.tradebot.domain.dto.CaptchaResponseDto;
import com.example.tradebot.repos.UserRepo;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;
@Configurable
@Service
public class UserService implements UserDetailsService {

//    @Autowired
//    private ListableBeanFactory factory;
//
//    private static UserService instance;

    private final UserRepo userRepo;
    private final OrderService orderService;
    private final MailSender mailSender;

    public UserService(UserRepo userRepo, OrderService orderService, MailSender mailSender) {
        this.userRepo = userRepo;
        this.orderService = orderService;
        this.mailSender = mailSender;
    }

//    @PostConstruct
//    public void init()
//    {
//        instance = this;
//    }
//
//    public static ListableBeanFactory getFactory()
//    {
//        return instance.factory;
//    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepo.findByUsername(s).get();
    }

    public User loadUserByUsername(User user){
        return userRepo.findByUsername(user.getUsername()).get();
    }


    public BindingResult addUser(User user, CaptchaResponseDto response, BindingResult bindingResult) {
        bindingResult = validUserCaptcha(response, bindingResult);
        if (!bindingResult.hasErrors()) {
            user.setActive(false);
            user.setRoles(Collections.singleton(Role.USER));
            user.setActivationCode(UUID.randomUUID().toString());
            userRepo.save(user);
            sendActivationCode(user);
        }
        return bindingResult;
    }

    private BindingResult validUserCaptcha(CaptchaResponseDto response, BindingResult bindingResult) {
        if (!response.isSuccess()){
            bindingResult.addError(new FieldError("captchaResponse", "captcha",
                    "Заполните капчу"));
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

    public Map<String, String> updateProfile(User user, User userForm, Map<String, String> symbolsForm) {

        Map<String, String> map = new HashMap<>();
        User userDB = userRepo.findByUsername(user.getUsername()).get();
        Boolean editKey = false;


        if (!userDB.getPassword().equals(userForm.getPassword())) {
            userDB.setPassword(userForm.getPassword());
            userDB.setConfirmPassword(userForm.getConfirmPassword());
            map.merge("message", "Пароль изменен", (a, b) -> a.join("; ", b));
        }


        if (userForm.getKey()!=null && !userForm.getKey().equals(userDB.getKey())) {
            userDB.setKey(userForm.getKey());
            map.merge("message", "Ключ изменен", (a, b) -> a.join("; ", b));
            editKey = true;
        }

        if (userForm.getSecret() != null && !userForm.getSecret().isEmpty()) {
            userDB.setSecret(userForm.getSecret());
            map.merge("message", "Секретный ключ изменен", (a, b) -> a.join("; ", b));
            editKey = true;
        }

        if (!userDB.getEmail().equalsIgnoreCase(userForm.getEmail())) {
            userDB.setEmail(userForm.getEmail());
            userDB.setActivationCode(UUID.randomUUID().toString());
            userDB.setActive(false);
            sendActivationCode(userDB);
            map.merge("message",
                    String.format("Email изменен. На email:%s выслано письмо с активацией", userForm.getEmail()),
                    (a, b) -> a.join("; ", b));
        }


        Set<String> symbols = Arrays.stream(Symbol.values())
                .map(Symbol::name)
                .collect(Collectors.toSet());

        Set<Symbol> oldSymbolSet = new HashSet<>(userDB.getSymbol());

        userDB.getSymbol().clear();

        if (user.getRoles().contains(Role.ADMIN)) {
            orderService.updateTradeRule(symbolsForm, symbols);
        }
        else {
            for (String key : symbolsForm.keySet()) {
                if (symbols.contains(key)) {
                    userDB.getSymbol().add(Symbol.valueOf(key));
                }
            }
        }

        if (editKey) {
            userDB.setCanTrade(isValidKey(userDB));
        }

        orderService.addMapRemoteSymbol(oldSymbolSet, userDB);
        userRepo.save(userDB);
        return map;
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

    public UserRepo getUserRepo(){
        return userRepo;
    }

}
