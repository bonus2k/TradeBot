package com.example.tradebot.controller;

import com.example.tradebot.domain.Billing;
import com.example.tradebot.domain.User;
import com.example.tradebot.service.BillingService;
import com.example.tradebot.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BillingController {

    private final BillingService billingService;
    private final UserService userService;

    public BillingController(BillingService billingService, UserService userService) {
        this.billingService = billingService;
        this.userService = userService;
    }

    @GetMapping("billing")
    public String getBillingUser(
            @AuthenticationPrincipal User user,
            Model model) {
        model.addAttribute("userBilling", billingService.findTopByUserOrderByDateDesc(user));
        model.addAttribute("userDB", userService.loadUserByUsername(user));
        model.addAttribute("billings", billingService.getBillingUser(user));
        return "billing";
    }

    @GetMapping("{user}/billing")
    public String getBilling(
            @PathVariable User user,
            Model model) {

        model.addAttribute("userBilling", billingService.findTopByUserOrderByDateDesc(user));
        model.addAttribute("userDB", userService.loadUserByUsername(user));
        model.addAttribute("billings", billingService.getBillingUser(user));
        return "billing";
    }

    @PostMapping("{user}/billing")
    public String setBilling(
            @PathVariable User user,
            Billing billing)
    {
        billingService.saveBilling(user, billing);
        return "redirect:billing";
    }
    
}
