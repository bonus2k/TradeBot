package com.example.tradebot.controller;

import com.example.tradebot.domain.Alerts;
import com.example.tradebot.domain.Order;
import com.example.tradebot.domain.User;
import com.example.tradebot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/alert")
    public String main(Map<String, Object> map) {
        Iterable<Alerts> alert = orderService.findAllAlerts();
        map.put("alerts", alert);
        return "alert";
    }

    @GetMapping("/order")
    public String getOrder(@AuthenticationPrincipal User user, Map<String, Object> map) {
        Iterable<Order> order = orderService.findByUser(user);
        map.put("orders", order);
        return "order";
    }

}
