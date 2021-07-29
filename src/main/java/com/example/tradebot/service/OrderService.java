package com.example.tradebot.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.*;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.exception.BinanceApiException;
import com.example.tradebot.domain.*;
import com.example.tradebot.repos.AlertsRepo;
import com.example.tradebot.repos.OrderRepo;
import com.example.tradebot.repos.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderService {

    private Map<String, Boolean> symbolsTrade;
    private Map<String, List<User>> deletedSymbol;
    private Boolean tradeIsEnable = false;
    private List<TickerPrice> allPrices;
    private Long lastQuery = 0L;
    private MathContext mc = new MathContext(6, RoundingMode.DOWN);
    private Map<String, Boolean> isBuySymbol;

    private final OrderRepo orderRepo;
    private final AlertsRepo alertsRepo;
    private final UserRepo userRepo;
    private final TelegramService telegramService;

    public OrderService(OrderRepo orderRepo, AlertsRepo alertsRepo, UserRepo userRepo, TelegramService telegramService) {
        this.orderRepo = orderRepo;
        this.alertsRepo = alertsRepo;
        this.userRepo = userRepo;
        this.telegramService = telegramService;

        symbolsTrade = Arrays.stream(Symbol.values())
                .map(Symbol::name)
                .collect(Collectors.toMap(
                        Function.identity(), Boolean::new));

        deletedSymbol = Arrays.stream(Symbol.values())
                .collect(Collectors.toMap(Symbol::name, o -> new ArrayList<>()));

        isBuySymbol = Arrays.stream(Symbol.values())
                .map(Symbol::name)
                .collect(Collectors.toMap(
                        Function.identity(), o -> true));

    }

    private void buy(String symbol) {
        if (tradeIsEnable && symbolsTrade.get(symbol)) {

            Set<User> users = userRepo.findBySymbolAndIsRunAndIsCanTrade(Symbol.valueOf(symbol), true, true);
            for (User user : users) {
                BinanceApiRestClient client = getBinanceApiRestClient(user);
                BigDecimal quantityBuy = getQuantityUsdtWithCommission(client, user);

                if (quantityBuy.compareTo(new BigDecimal(11)) > 0) {
                    int scale = Symbol.valueOf(symbol).getScale();
                    NewOrder buyOrder = NewOrder.marketBuy(symbol,
                            quantityBuy.divide(new BigDecimal(getPrice(symbol)),
                                    scale, RoundingMode.DOWN).toString());
                    sendOrder(user, client, buyOrder);
                }
            }
            if (Symbol.valueOf(symbol).isLimit()) {
                sellLimit(symbol);
            }
        }
    }

    private BinanceApiRestClient getBinanceApiRestClient(User user) {
        BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance(user.getKey(), user.getSecret());
        return clientFactory.newRestClient();
    }

    private void sellLimit(String symbol) {
        Set<User> users = userRepo.findBySymbolAndIsCanTrade(Symbol.valueOf(symbol), true);
        List<User> userDeletedSymbol = deletedSymbol.get(symbol);
        users.removeAll(userDeletedSymbol);
        users.addAll(userDeletedSymbol);
        userDeletedSymbol.clear();
        int scale = Symbol.valueOf(symbol).getScale();
        BigDecimal priceDecimal = new BigDecimal(getPrice(symbol))
                .multiply(new BigDecimal((Symbol.valueOf(symbol).getPercent() / 100) + 1));
        String price = priceDecimal.setScale(Symbol.valueOf(symbol).getScaleLimit(), RoundingMode.UP).toString();

        for (User user : users) {
            BinanceApiRestClient client = getBinanceApiRestClient(user);
            BigDecimal quantitySymbol = getQuantitySymbol(client, Symbol.valueOf(symbol));
            getQuantityUsdtWithCommission(client, user);

            if (quantitySymbol.multiply(new BigDecimal(getPrice(symbol))).compareTo(new BigDecimal(11)) > 0) {

                quantitySymbol = quantitySymbol.setScale(scale, RoundingMode.DOWN);
                NewOrder sellOrder = NewOrder.limitSell(symbol, TimeInForce.GTC, quantitySymbol.toString(), price);
                sendOrder(user, client, sellOrder);
            }
        }
    }

    private void sell(String symbol) {
        Set<User> users = userRepo.findBySymbolAndIsCanTrade(Symbol.valueOf(symbol), true);
        List<User> userDeletedSymbol = deletedSymbol.get(symbol);
        users.removeAll(userDeletedSymbol);
        users.addAll(userDeletedSymbol);
        userDeletedSymbol.clear();

        for (User user : users) {
            BinanceApiRestClient client = getBinanceApiRestClient(user);
            cancelLimitOrder(symbol, user, client);
            BigDecimal quantitySymbol = getQuantitySymbol(client, Symbol.valueOf(symbol));
            getQuantityUsdtWithCommission(client, user);

            if (quantitySymbol.multiply(new BigDecimal(getPrice(symbol))).compareTo(new BigDecimal(11)) > 0) {
                int scale = Symbol.valueOf(symbol).getScale();
                quantitySymbol = quantitySymbol.setScale(scale, RoundingMode.DOWN);
                NewOrder sellOrder = NewOrder.marketSell(symbol, quantitySymbol.toString());
                sendOrder(user, client, sellOrder);
            }
        }
    }

    private boolean cancelLimitOrder(String symbol, User user, BinanceApiRestClient client) {
        Set<usrOrder> cancelUsrOrder = orderRepo.findByUserAndSymbolAndStatus(user, symbol, OrderStatus.NEW);
        for (usrOrder order : cancelUsrOrder) {
            CancelOrderRequest cancelOrderRequest = new CancelOrderRequest(symbol, order.getClientOrderId());
            try {
                cancelOrderRequest.recvWindow(50000L);
                CancelOrderResponse cancel = client.cancelOrder(cancelOrderRequest);
                if (cancel.getOrderId() != null) {
                    order.setStatus(OrderStatus.CANCELED);
                    order.setProfit(0.0);
                    orderRepo.save(order);
                    telegramService.sendUserOrder(user, order);
                }
            } catch (BinanceApiException e) {
                log.error(user.getUsername() + ": " + order.toString() + ": " + e.getError());
            }
        }

        return true;
    }

    private void sendOrder(User user, BinanceApiRestClient client, NewOrder newOrder) {

        try {
            newOrder.recvWindow(50000L);
            NewOrderResponse newOrderResponse = client.newOrder(newOrder);
            log.info(user.getUsername() + ": " + newOrder.toString());
            save(newOrderResponse, user, null);
        } catch (BinanceApiException e) {
            log.error(user.getUsername() + ": " + newOrder.toString());
            log.error(user.getUsername() + ": " + e.getMessage() + ": " + e.getError());
        }
    }

    private BigDecimal getQuantitySymbol(BinanceApiRestClient client, Symbol symbol) {
        Account account = client.getAccount(50000L, new Date().getTime());
        AssetBalance balance = account.getAssetBalance(symbol.getAsset());
        return new BigDecimal(balance.getFree(), mc);
    }


    private BigDecimal getQuantityUsdtWithCommission(BinanceApiRestClient client, User user) {
        Account account = client.getAccount(50000L, new Date().getTime());
        List<AssetBalance> balances = account.getBalances();

        Map<String, BigDecimal> balance = balances.stream()
                .filter(o -> !o.getLocked().equals("0.00000000") || !o.getFree().equals("0.00000000"))
                .collect(Collectors.toMap(AssetBalance::getAsset,
                        o -> new BigDecimal(o.getFree()).add(new BigDecimal(o.getLocked()))));

        BigDecimal amountTotal = balance.entrySet()
                .stream()
                .filter(o -> !o.getKey().equals("USDT"))
                .map(o -> o.getValue().multiply(new BigDecimal(getPrice(o.getKey() + "USDT")), mc))
                .reduce(new BigDecimal(0), (b1, b2) -> b1.add(b2, mc));

        Optional<BigDecimal> USDT = Optional.ofNullable(balance.get("USDT"));
        BigDecimal balUSDT = USDT.orElse(new BigDecimal("0")).round(mc);
        amountTotal = amountTotal.add(balUSDT).round(mc);

        user.setAmount(amountTotal.doubleValue());
        userRepo.save(user);

        BigDecimal userQuantityUSDT = (amountTotal.divide(new BigDecimal(user.getSymbol().size()), mc).compareTo(balUSDT) > 0) ?
                balUSDT :
                amountTotal.divide(new BigDecimal(user.getSymbol().size()), mc);

        BigDecimal usdtWithCommission = userQuantityUSDT.multiply(new BigDecimal("1.002"), mc);

        userQuantityUSDT = (userQuantityUSDT.compareTo(usdtWithCommission) > 0) ? userQuantityUSDT :
                userQuantityUSDT.subtract(userQuantityUSDT.multiply(new BigDecimal("0.002")), mc);

        return userQuantityUSDT;
    }

    private void save(NewOrderResponse newOrderResponse, User user, Long orderId) {
        usrOrder order;
        if (orderId == null) {
            order = new usrOrder();
        } else order = orderRepo.getOne(orderId);
        System.out.println(newOrderResponse);
        order.setOrderId(newOrderResponse.getOrderId());
        order.setSymbol(newOrderResponse.getSymbol());
        order.setClientOrderId(newOrderResponse.getClientOrderId());
        order.setPrice(getPrice(newOrderResponse.getSymbol()));
        order.setOrigQty(newOrderResponse.getOrigQty());
        order.setExecutedQty(newOrderResponse.getExecutedQty());
        order.setStatus(newOrderResponse.getStatus());
        order.setTimeInForce(newOrderResponse.getTimeInForce());
        order.setType(newOrderResponse.getType());
        order.setSide(newOrderResponse.getSide());
        order.setTime(new Date(newOrderResponse.getTransactTime()));
        order.setUser(user);
        if (newOrderResponse.getStatus() == OrderStatus.FILLED) {
            order.setSum(new BigDecimal(newOrderResponse.getExecutedQty())
                    .multiply(new BigDecimal(order.getPrice())).doubleValue());

            order.setProfit(getProfit(order, user));
        } else {
            order.setSum(0.0);
            order.setProfit(0.0);
        }
        orderRepo.save(order);
        if (order.getStatus() == OrderStatus.NEW
                || order.getStatus() == OrderStatus.FILLED
                || order.getStatus() == OrderStatus.CANCELED) {
            telegramService.sendUserOrder(user, order);
        }
    }

    @Scheduled(cron = "0 0/15 * * * *")
    private void checkLimitOrder() {
        Set<usrOrder> usrOrders = orderRepo.findByStatus(OrderStatus.NEW);
        Map<User, List<usrOrder>> userOrderMap = usrOrders.stream()
                .collect(Collectors.groupingBy(usrOrder::getUser));
        for (Map.Entry<User, List<usrOrder>> value : userOrderMap.entrySet()) {
            User user = value.getKey();
            BinanceApiRestClient client = getBinanceApiRestClient(user);
            for (usrOrder order : value.getValue()) {
                OrderStatusRequest orderStatusRequest = new OrderStatusRequest(order.getSymbol(), order.getOrderId());
                orderStatusRequest.recvWindow(50000L);
                Order orderStatus = client.getOrderStatus(orderStatusRequest);
                if (orderStatus.getStatus() == OrderStatus.FILLED) {
                    order.setExecutedQty(orderStatus.getExecutedQty());
                    order.setStatus(orderStatus.getStatus());
                    order.setPrice(orderStatus.getPrice());
                    order.setSum(new BigDecimal(order.getExecutedQty())
                            .multiply(new BigDecimal(order.getPrice())).doubleValue());
                    order.setProfit(getProfit(order, user));
                    orderRepo.save(order);
                    telegramService.sendUserOrder(user, order);
                }
                if (orderStatus.getStatus() == OrderStatus.CANCELED) {
                    order.setExecutedQty(orderStatus.getExecutedQty());
                    order.setStatus(orderStatus.getStatus());
                    order.setSum(0.0);
                    order.setProfit(0.0);
                    order.setCounted(true);
                    orderRepo.save(order);
                }
            }
        }

    }

    private Double getProfit(usrOrder order, User user) {
        if (order.getSide().equals(OrderSide.BUY)) {
            return 0.0;
        }

        List<usrOrder> orderSell = orderRepo
                .findByUserIdAndSymbolAndSideAndCounted
                        (user.getId(), order.getSymbol(), OrderSide.BUY, false);

        if (orderSell.size() < 1) {
            return 0.0;
        }

        BigDecimal sumSell = orderSell.stream()
                .peek(o -> o.setCounted(true))
                .peek(orderRepo::save)
                .map(usrOrder::getSum)
                .map(BigDecimal::valueOf)
                .reduce((o1, o2) -> o1.add(o2)).get();
        BigDecimal sumBuy = new BigDecimal(order.getExecutedQty())
                .multiply(new BigDecimal(order.getPrice()), mc);

        return sumBuy.subtract(sumSell, mc).doubleValue();
    }

    private String getPrice(String symbol) {
        BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = clientFactory.newRestClient();
        String price = "0.0";
        if (System.currentTimeMillis() - lastQuery > 10000) {
            allPrices = client.getAllPrices();
            lastQuery = System.currentTimeMillis();
        }
        for (TickerPrice tickerPrice : allPrices) {
            if (tickerPrice.getSymbol() != null && tickerPrice.getSymbol().contains(symbol)) {
                price = tickerPrice.getPrice();
            }
        }
        return price;
    }

    public void updateTradeRule(Map<String, String> form, Set<String> symbols) {
        tradeIsEnable = "on".equals(form.get("tradeIsEnable"));

        symbolsTrade = Arrays.stream(Symbol.values())
                .map(Symbol::name)
                .collect(Collectors.toMap(
                        Function.identity(), Boolean::new));

        for (String key : form.keySet()) {
            if (symbols.contains(key)) {
                symbolsTrade.put(key, true);
            }
        }
    }

    //Для отражения в представлении
    public void setSymbolsTrade(User admin) {
        if (admin.getRoles().contains(Role.ADMIN)) {
            Set<Symbol> set = symbolsTrade.entrySet()
                    .stream()
                    .filter(o -> o.getValue() == true)
                    .map(o -> o.getKey())
                    .map(o -> Symbol.valueOf(o))
                    .collect(Collectors.toSet());
            admin.setSymbol(set);
        }
    }

    public Boolean getTradeIsEnable() {
        return tradeIsEnable;
    }

    public void setTradeIsEnable(Boolean tradeIsEnable) {
        this.tradeIsEnable = tradeIsEnable;
    }

    public Page<usrOrder> findByUser(User user, Pageable pageable) {
        return orderRepo.findByUserId(user.getId(), pageable);
    }

    public Page<Alerts> findAllAlerts(Pageable pageable) {
        return alertsRepo.findAll(pageable);
    }

    public void addMapRemoteSymbol(Set<Symbol> symbols, User userDB) {
        symbols.removeAll(userDB.getSymbol());
        symbols.forEach(o -> deletedSymbol.get(o.name()).add(userDB));
    }

    public void saveAlert(String alert, String symbol, String name) {

        Alerts alerts = new Alerts(alert, symbol, Double.parseDouble(getPrice(symbol)), 0.0, name, new Date());

        if ("sell".equalsIgnoreCase(alert)) {
            isBuySymbol.put(symbol, true);
            sell(symbol);
            Alerts alertsBuy = alertsRepo.findTopBySymbolOrderByDateDesc(symbol).orElse(alerts);
            if (alertsBuy.getAlert().equalsIgnoreCase("buy")) {
                BigDecimal rate = (((new BigDecimal(alerts.getPrice()).subtract(new BigDecimal(alertsBuy.getPrice()), mc))
                        .divide(new BigDecimal(alerts.getPrice()), mc))
                        .multiply(new BigDecimal("100"), mc));
                alerts.setRate(rate.doubleValue());
            }
        }
        if ("buy".equalsIgnoreCase(alert) && isBuySymbol.get(symbol)) {
            isBuySymbol.put(symbol, false);
            telegramService.sendAllAlert(alerts);
            buy(symbol);
        }
        alertsRepo.save(alerts);
        log.info("Symbol: " + symbol + " alert: " + alert + isBuySymbol.get(symbol));
    }

    public Page<Alerts> findAlertsFilter(Map<String, String> form, PageRequest pageRequest) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String symbol = "%";
        String alert = "%";
        Date start = new Date(1212121212121L);
        Date stop = new Date();
        if (form.get("selectSymbol") != null && !form.get("selectSymbol").equals("")) {
            symbol = form.get("selectSymbol");
        }
        if (form.get("selectSide") != null && !form.get("selectSide").equals("")) {
            alert = form.get("selectSide").toLowerCase();
        }
        if (form.get("selectStart") != null && !form.get("selectStart").equals("")) {
            try {
                start = formatter.parse(form.get("selectStart"));
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        if (form.get("selectStop") != null && !form.get("selectStop").equals("")) {
            try {
                stop = formatter.parse(form.get("selectStop"));
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        return alertsRepo.findAlertsFilter(alert, symbol, start, stop, pageRequest);
    }

    public Page<usrOrder> findOrdersFilter(User user, Map<String, String> form, PageRequest pageRequest) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String symbol = "%";
        OrderStatus[] orderStatus = OrderStatus.values();
        OrderSide[] orderSide = OrderSide.values();
        OrderType[] orderType = OrderType.values();
        Date start = new Date(1212121212121L);
        Date stop = new Date();
        if (form.get("selectSymbol") != null && !form.get("selectSymbol").equals("")) {
            symbol = form.get("selectSymbol");
        }
        if (form.get("selectSide") != null && !form.get("selectSide").equals("")) {
           orderSide = new OrderSide[]{OrderSide.valueOf(form.get("selectSide"))};
        }
        if (form.get("selectStatus") != null && !form.get("selectStatus").equals("")) {
            orderStatus = new OrderStatus[]{OrderStatus.valueOf(form.get("selectStatus"))};
        }
        if (form.get("selectType") != null && !form.get("selectType").equals("")) {
            orderType = new OrderType[]{OrderType.valueOf(form.get("selectType"))};
        }
        if (form.get("selectStart") != null && !form.get("selectStart").equals("")) {
            try {
                start = formatter.parse(form.get("selectStart"));
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        if (form.get("selectStop") != null && !form.get("selectStop").equals("")) {
            try {
                stop = formatter.parse(form.get("selectStop"));
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        return orderRepo.findOrdersFilter(user, orderStatus, orderType, orderSide, symbol, start, stop, pageRequest);
    }

}
