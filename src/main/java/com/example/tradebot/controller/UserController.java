package com.example.tradebot.controller;

import com.example.tradebot.domain.Role;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import com.example.tradebot.service.OrderService;
import com.example.tradebot.service.UserService;
import com.example.tradebot.util.Util;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private Map<String, String> mapProfile;

    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("orders", orderService.findByUser(user));
        model.addAttribute("comment", user.getComment());
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(@RequestParam String username,
                           @RequestParam Map<String, String> form,
                           @RequestParam("userId") User user,
                           @RequestParam(required = false, defaultValue = "off") String isRun
    ) {
        userService.userSave(username, form, user, isRun);
        return "redirect:/user";
    }


    @GetMapping("profile")
    public String getProfile(@AuthenticationPrincipal User user,
                             Model model) {
        User userDB = userService.loadUserByUsername(user);
        orderService.setSymbolsTrade(userDB);
        model.addAttribute("userDB", userDB);
        model.addAttribute("symbols", Symbol.values());
        model.addAttribute("tradeIsEnable", orderService.getTradeIsEnable());
        if (mapProfile!=null){
            model.addAllAttributes(mapProfile);
            mapProfile.clear();
        }

        return "profile";
    }

    @PostMapping("profile")
    public String editProfile(@AuthenticationPrincipal User user,
                              @RequestParam Map<String, String> symbols,
                              @Valid User userForm,
                              BindingResult bindingResult,
                              Model model) {


        User userDB = userService.loadUserByUsername(user);
        orderService.setSymbolsTrade(userDB);
        model.addAttribute("userDB", userDB);
        model.addAttribute("symbols", Symbol.values());
        model.addAttribute("tradeIsEnable", orderService.getTradeIsEnable());

        if (bindingResult.hasErrors()) {
            Map<String, String> mapErrors = Util.getErrors(bindingResult);
            model.addAllAttributes(mapErrors);
            return "profile";
        }

        mapProfile = userService.updateProfile(user, userForm, symbols);
        return "redirect:profile";
    }
}
