package com.example.tradebot.service;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.exception.BinanceApiException;
import com.example.tradebot.domain.*;
import com.example.tradebot.repos.AlertsRepo;
import com.example.tradebot.repos.OrderRepo;
import com.example.tradebot.repos.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
                BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance(user.getKey(), user.getSecret());
                BinanceApiRestClient client = clientFactory.newRestClient();
                Account account = client.getAccount(50000L, new Date().getTime());
                BigDecimal quantityBuy = getQuantityUsdtWithCommission(account, user);

                if (quantityBuy.compareTo(new BigDecimal(11)) > 0) {
                    int scale = Symbol.valueOf(symbol).getScale();
                    NewOrder buyOrder = NewOrder.marketBuy(symbol,
                            quantityBuy.divide(new BigDecimal(getPrice(symbol)),
                                    scale, RoundingMode.DOWN).toString());
                    sendOrder(user, client, buyOrder);
                }
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
            BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance(user.getKey(), user.getSecret());
            BinanceApiRestClient client = clientFactory.newRestClient();
            Account account = client.getAccount(50000L, new Date().getTime());
            BigDecimal quantitySymbol = getQuantitySymbol(account, Symbol.valueOf(symbol));
            getQuantityUsdtWithCommission(account, user);

            if (quantitySymbol.multiply(new BigDecimal(getPrice(symbol))).compareTo(new BigDecimal(11)) > 0) {
                int scale = Symbol.valueOf(symbol).getScale();
                quantitySymbol = quantitySymbol.setScale(scale, RoundingMode.DOWN);
                NewOrder sellOrder = NewOrder.marketSell(symbol, quantitySymbol.toString());
                sendOrder(user, client, sellOrder);
            }
        }
    }

    private void sendOrder(User user, BinanceApiRestClient client, NewOrder newOrder) {

        try {
            newOrder.recvWindow(50000L);
            client.newOrder(newOrder);
            log.info(user.getUsername() + ": " + newOrder.toString());
        } catch (BinanceApiException e) {
            log.error(newOrder.toString());
            log.error(user.getUsername() + ": " + e.getMessage() + ": " + e.getError());
        }
        save(newOrder, user);
    }

    private BigDecimal getQuantitySymbol(Account account, Symbol symbol) {
        List<AssetBalance> balances = account.getBalances();
        BigDecimal quantitySymbol = new BigDecimal(0);
        for (AssetBalance balance : balances) {
            if (balance.getAsset().equals(symbol.getAsset())) {
                quantitySymbol = new BigDecimal(balance.getFree(), mc);
            }
        }
        return quantitySymbol;
    }


    private BigDecimal getQuantityUsdtWithCommission(Account account, User user) {
        List<AssetBalance> balances = account.getBalances();
        BigDecimal amountTotal = new BigDecimal(0);
        BigDecimal balUSDT = new BigDecimal(0);

        for (AssetBalance balance : balances
        ) {
            if (!balance.getAsset().equals("USDT") && !balance.getFree().equals("0.00000000")) {
                BigDecimal result = new BigDecimal(balance.getFree()).multiply(
                        new BigDecimal(getPrice(balance.getAsset() + "USDT")), mc);
                amountTotal = amountTotal.add(result, mc);
            }
            if (balance.getAsset().equals("USDT")) {
                balUSDT = new BigDecimal(balance.getFree(), mc);
                amountTotal = amountTotal.add(balUSDT, mc);
            }
        }

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

    private void save(NewOrder newOrder, User user) {
        Order order = new Order();
        order.setPrice(order.getPrice());
        order.setQuantity(new BigDecimal(newOrder.getQuantity(), mc).doubleValue());
        order.setSide(newOrder.getSide().toString());
        order.setSymbol(newOrder.getSymbol());
        order.setTimestamp(new Date(newOrder.getTimestamp()));
        order.setType(newOrder.getType().toString());
        order.setPrice(new BigDecimal(getPrice(newOrder.getSymbol()), mc).doubleValue());
        order.setSum(new BigDecimal(newOrder.getQuantity())
                .multiply(new BigDecimal(getPrice(newOrder.getSymbol())), mc).doubleValue());
        order.setUser(user);
        order.setProfit(getProfit(newOrder, user));
        orderRepo.save(order);
    }

    private Double getProfit(NewOrder newOrder, User user) {
        if (newOrder.getSide().equals(OrderSide.BUY)) {
            return 0.0;
        }

        List<Order> orderSell = orderRepo
                .findByUserIdAndSymbolAndSideAndCounted
                        (user.getId(), newOrder.getSymbol(), "BUY", false);

        if (orderSell.size() < 1) {
            return 0.0;
        }

        BigDecimal sumSell = orderSell.stream()
                .peek(o -> o.setCounted(true))
                .peek(orderRepo::save)
                .map(Order::getSum)
                .map(BigDecimal::valueOf)
                .reduce((o1, o2) -> o1.add(o2)).get();
        BigDecimal sumBuy = new BigDecimal(newOrder.getQuantity())
                .multiply(new BigDecimal(getPrice(newOrder.getSymbol())), mc);

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

    public Iterable<Order> findByUser(User user) {
        return orderRepo.findByUserIdOrderByTimestamp(user.getId());
    }

    public Iterable<Alerts> findAllAlerts() {
        return alertsRepo.findAll();
    }

    public void addMapRemoteSymbol(Set<Symbol> symbols, User userDB) {
        symbols.removeAll(userDB.getSymbol());
        symbols.forEach(o -> deletedSymbol.get(o.name()).add(userDB));
    }

    public void saveAlert(String alert, String symbol, String name) {
        SimpleDateFormat formater = new SimpleDateFormat("dd MMMM в HH:mm:ss");
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
                telegramService.sendAll(String.format("\uD83D\uDD14Продажа %s, пара: %s, цена: %s, %sдоход: %.3f%%, сигнал от: %s",
                        formater.format(alerts.getDate()),
                        alerts.getSymbol(),
                        alerts.getPrice(),
                        (alerts.getRate() > 0) ? "\uD83D\uDCC8" : "\uD83D\uDCC9",
                        alerts.getRate(),
                        alerts.getBotName()));
            }
        }
        if ("buy".equalsIgnoreCase(alert) && isBuySymbol.get(symbol)) {
            isBuySymbol.put(symbol, false);
            buy(symbol);
            telegramService.sendAll(String.format("\uD83D\uDD14Покупка %s, пара: %s, цена: %s, сигнал от: %s",
                    formater.format(alerts.getDate()), alerts.getSymbol(), alerts.getPrice(), alerts.getBotName()));
        }
        alertsRepo.save(alerts);
        log.info("Symbol: " + symbol + " alert: " + alert + isBuySymbol.get(symbol));
    }
}
