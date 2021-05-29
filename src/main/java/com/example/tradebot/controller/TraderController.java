package com.example.tradebot.controller;

import com.example.tradebot.domain.Alerts;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import com.example.tradebot.domain.usrOrder;
import com.example.tradebot.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class TraderController {


    private final OrderService orderService;

    public TraderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/webhook")
    public ResponseEntity hook(@RequestBody String alert,
                               @RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "sym", required = false) String symbol) {
        orderService.saveAlert(alert, symbol, name);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/handler")
    public String hookHandler(@RequestParam(name = "sym", required = false) String symbol) {
        orderService.saveAlert("sell", symbol, "handler");
        return "redirect:/user/trade";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/trade/{symbol}")
    public String getLimitOrder(@PathVariable String symbol, Model model) {
        model.addAttribute("symbolUsr", Symbol.valueOf(symbol));
        return "/settings";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping ("/user/trade/{symbol}")
    public String setLimitOrder(@RequestParam Map<String, String> form) {
        Symbol symbol = Symbol.valueOf(form.get("symbol"));
        symbol.setLimit(form.get("limit"));
        symbol.setPercent(Double.parseDouble(form.get("percent")));
        return "redirect:/user/trade";
    }

    @GetMapping("/alert")
    public String getAlerts(Model model, @PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC, size = 15)
                                    Pageable pageable) {

        Page<Alerts> alert = orderService.findAllAlerts(pageable);
        model.addAttribute("alerts", alert);
        model.addAttribute("url", "alert");
        return "alert";
    }

    @GetMapping("/order")
    public String getOrder(@AuthenticationPrincipal User user,
                           Model model,
                           @PageableDefault(sort = {"time"}, direction = Sort.Direction.DESC, size = 15)
            Pageable pageable) {
        Iterable<usrOrder> order = orderService.findByUser(user, pageable);
        model.addAttribute("orders", order);
        model.addAttribute("url", "order");
        return "order";
    }

}
