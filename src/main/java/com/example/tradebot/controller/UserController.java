package com.example.tradebot.controller;

import com.example.tradebot.domain.Role;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import com.example.tradebot.service.OrderService;
import com.example.tradebot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

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
        User userDB = userService.getUserByUsername(user);
        orderService.setSymbolsTrade(userDB);
        model.addAttribute("username", userDB.getUsername());
        model.addAttribute("email", userDB.getEmail());
        model.addAttribute("symbols", Symbol.values());
        model.addAttribute("key", userDB.getKey());
        model.addAttribute("userdb", userDB);
        model.addAttribute("tradeIsEnable", orderService.getTradeIsEnable());
        model.addAttribute("isCanTrade", userDB.isCanTrade());

        return "profile";
    }

    @PostMapping("profile")
    public String editProfile(@AuthenticationPrincipal User user,
                              @RequestParam Map<String, String> form) {

        userService.updateProfile(user, form);

        return "redirect:/user/profile";
    }
}
