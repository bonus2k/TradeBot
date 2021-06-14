package com.example.tradebot.controller;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.example.tradebot.domain.Alerts;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import com.example.tradebot.domain.usrOrder;
import com.example.tradebot.service.OrderService;
import com.example.tradebot.util.Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import javax.servlet.http.HttpServletRequest;
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
    @PostMapping("/user/trade/{symbol}")
    public String setLimitOrder(@RequestParam Map<String, String> form) {
        Symbol symbol = Symbol.valueOf(form.get("symbol"));
        symbol.setLimit(form.get("limit"));
        symbol.setPercent(Double.parseDouble(form.get("percent")));
        return "redirect:/user/trade";
    }


    @GetMapping("/orderFilter")
    public String getOrders(@AuthenticationPrincipal User user,
                            HttpServletRequest request,
                            Model model,
                            @PageableDefault(sort = {"time"}, direction = Sort.Direction.DESC, size = 15) Pageable pageable,
                            @RequestParam(defaultValue = "", required = false) String direc,
                            @RequestParam Map<String, String> form) {


        String filterRequest = Util.getStringRequest(form);
        PageRequest pageRequest = Util.getPageRequest(pageable, direc);
        Page<usrOrder> order = orderService.findOrdersFilter(user, form, pageRequest);

        model.addAttribute("orders", order);
        model.addAttribute("url", request.getRequestURI());
        model.addAttribute("symbols", Symbol.values());
        model.addAttribute("statusList", OrderStatus.values());
        model.addAttribute("typeList", OrderType.values());
        model.addAttribute("direction", direc);
        model.addAllAttributes(form);
        model.addAttribute("sumProfit", order.get().mapToDouble(o -> o.getProfit()).sum());
        model.addAttribute("total", order.getTotalElements());
        model.addAttribute("request", filterRequest);
        return "orderFilter";
    }

    @PostMapping("/orderFilter")
    public String setOrdersFilter(@AuthenticationPrincipal User user,
                                  HttpServletRequest request,
                                  Model model,
                                  @PageableDefault(sort = {"time"}, direction = Sort.Direction.DESC, size = 15) Pageable pageable,
                                  @RequestParam(defaultValue = "", required = false) String direc,
                                  @RequestParam(required = false) Map<String, String> form) {


        String filterRequest = Util.getStringRequest(form);
        PageRequest pageRequest = Util.getPageRequest(pageable, direc);
        Page<usrOrder> order = orderService.findOrdersFilter(user, form, pageRequest);

        model.addAttribute("orders", order);
        model.addAttribute("url", request.getRequestURI());
        model.addAttribute("symbols", Symbol.values());
        model.addAttribute("statusList", OrderStatus.values());
        model.addAttribute("typeList", OrderType.values());
        model.addAttribute("direction", direc);
        model.addAllAttributes(form);
        model.addAttribute("sumProfit", order.get().mapToDouble(o -> o.getProfit()).sum());
        model.addAttribute("total", order.getTotalElements());
        model.addAttribute("request", filterRequest);

        return "orderFilter";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}/userOrder")
    public String getUserOrder(@PathVariable User user,
                               HttpServletRequest request,
                               Model model,
                               @PageableDefault(sort = {"time"}, direction = Sort.Direction.DESC, size = 15) Pageable pageable,
                               @RequestParam(defaultValue = "", required = false) String direc,
                               @RequestParam(required = false) Map<String, String> form) {
        return getOrders(user, request, model, pageable, direc, form);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("{user}/userOrder")
    public String setUserOrder(@PathVariable User user,
                               HttpServletRequest request,
                               Model model,
                               @PageableDefault(sort = {"time"}, direction = Sort.Direction.DESC, size = 15) Pageable pageable,
                               @RequestParam(defaultValue = "", required = false) String direc,
                               @RequestParam(required = false) Map<String, String> form) {
        return getOrders(user, request, model, pageable, direc, form);
    }

    @GetMapping("/alertFilter")
    public String getAlerts(Model model,
                            @PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC, size = 15) Pageable pageable,
                            @RequestParam(defaultValue = "", required = false) String direc,
                            @RequestParam(required = false) Map<String, String> form) {

        String filterRequest = Util.getStringRequest(form);
        PageRequest pageRequest = Util.getPageRequest(pageable, direc);
        Page<Alerts> alert = orderService.findAlertsFilter(form, pageRequest);
        model.addAttribute("alerts", alert);
        model.addAttribute("url", "alertFilter");
        model.addAttribute("direction", direc);
        model.addAttribute("symbols", Symbol.values());
        model.addAllAttributes(form);
        model.addAttribute("total", alert.getTotalElements());
        model.addAttribute("request", filterRequest);
        return "alertFilter";
    }


    @PostMapping("/alertFilter")
    public String setAlertsFilter(Model model,
                                  @PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC, size = 15) Pageable pageable,
                                  @RequestParam(defaultValue = "", required = false) String direc,
                                  @RequestParam(required = false) Map<String, String> form) {

        String filterRequest = Util.getStringRequest(form);
        PageRequest pageRequest = Util.getPageRequest(pageable, direc);
        Page<Alerts> alert = orderService.findAlertsFilter(form, pageRequest);

        model.addAttribute("alerts", alert);
        model.addAttribute("url", "alertFilter");
        model.addAttribute("direction", direc);
        model.addAttribute("symbols", Symbol.values());
        model.addAllAttributes(form);
        model.addAttribute("total", alert.getTotalElements());
        model.addAttribute("request", filterRequest);
        return "alertFilter";
    }

}
