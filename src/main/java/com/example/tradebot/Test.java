package com.example.tradebot;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.market.TickerPrice;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data @Setter @Getter
public class Test extends ExchangeInfo {


    private static BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance
            ("oKB5YsxX6N0V8PNohyBz9AguHZBB9FhM9AdZZwx1KurI8MTYxVUtowOtONroIUBX",
                    "9ew65zomTqlZzNYjOm2HTApyEOUevLUglvgq9ASfsfVuEe4bB5n70RDq6U9s2Enw");
    private static BinanceApiRestClient client = clientFactory.newRestClient();

    private static String[] listOrder = new String[]{"o6oGFxeHNUs4omXuu2nxV7",
            "QwtGR1xQeRVl963euNZGLm",
            "Rnjt1jyrulCRzFg272ztL0",
            "smKIDJA0UGaVW6keqg1YDZ",
            "amejhoLSAHlC8i2PfoxAxf",
            "6Ksw21tLBng35AcYo7Kmsw"};

    public static void main(String[] args) {
        MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
        Account account = client.getAccount(50000L, new Date().getTime());
        List<AssetBalance> balances = account.getBalances();

        Map<String, BigDecimal> balance = balances.stream()
                .filter(o -> !o.getLocked().equals("0.00000000") || !o.getFree().equals("0.00000000"))
                .collect(Collectors.toMap(AssetBalance::getAsset,
                        o -> new BigDecimal(o.getFree()).add(new BigDecimal(o.getLocked()))));

        balance.entrySet().forEach(System.out::println);

        BigDecimal amountTotal = balance.entrySet()
                .stream()
                .filter(o -> !o.getKey().equals("USDT"))
                .map(o -> o.getValue().multiply(new BigDecimal(getPrice(o.getKey() + "USDT")), mc))
                .reduce(new BigDecimal(0), (b1, b2) -> b1.add(b2, mc));

        System.out.println(amountTotal);

        BigDecimal balUSDT = balance.get("USDT").round(mc);

        System.out.println(balUSDT);


        amountTotal = amountTotal.add(balUSDT).round(mc);
        System.out.println(amountTotal);

//        SymbolInfo exchangeInfo = client.getExchangeInfo().getSymbolInfo("ETH");
//        System.out.println();
//        System.out.println(exchangeInfo.toString());
//        NewOrder limitOrder = NewOrder.limitSell("ETHUSDT", TimeInForce.GTC, "0.006", "3450");
//        limitOrder.recvWindow(50000L);
////        Symbol.BNBUSDT.setLimit(true);
////        Symbol.BNBUSDT.setPercent(2.0);
//        String text = "null";
//        Optional<String> str = Optional.ofNullable(text);
//        System.out.println("ntcn "+str.isPresent());
//        System.out.println("\u274C");
//
//
//        System.out.println(Symbol.BNBUSDT.isLimit());
//        System.out.println(Symbol.BNBUSDT.getPercent());
//        NewOrderResponse newOrderResponse = client.newOrder(limitOrder);
//        System.out.println(newOrderResponse.getTransactTime());
//        System.out.println(newOrderResponse.toString());


//        CancelOrderRequest cancelOrderRequest = new CancelOrderRequest("ETHUSDT", "o6oGFxeHNUs4omXuu2nxV7");
//        cancelOrderRequest.recvWindow(50000L);
//        CancelOrderResponse cancel = client.cancelOrder(cancelOrderRequest);
//        System.out.println(cancel.toString());
//
//        System.out.println("----------------");

//        AllOrdersRequest allOrdersRequest = new AllOrdersRequest("USDT");
//        allOrdersRequest.recvWindow(50000L);
//        List<Order> allOrder = client.getAllOrders(allOrdersRequest);
//        allOrder.forEach(o-> System.out.println(o +":"+new Date(o.getTime())));

        System.out.println("------------------");

        List<Order> openOrders = client.getOpenOrders(new OrderRequest("ETHUSDT").recvWindow(50000L));
        openOrders.forEach(o -> System.out.println(o + ":" + new Date(o.getTime())));

//        orderAll.forEach(System.out::println);
        System.out.println("----------------");

//        getStatus(listOrder);
        Double d = -0.0001;
        System.out.println(String.format("\uD83D\uDD14Покупка %s, пара: %s, %s цена: %.3f%%, сигнал от: %s",
                new Date(), "USDT", (d > 0) ? "\uD83D\uDCC8" : "\uD83D\uDCC9", 0.0005468, "Bot"));

    }

    public static void getStatus(String[] listOrderId) {
        for (String orderId : listOrderId) {
            OrderStatusRequest orderStatusRequest = new OrderStatusRequest("ETHUSDT", orderId);
            orderStatusRequest.recvWindow(50000L);
            Order orderStatus = client.getOrderStatus(orderStatusRequest);
            System.out.println(orderStatus.toString() + " : " + new Date(orderStatus.getTime()));
        }

    }

    private static String getPrice(String symbol) {


        BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = clientFactory.newRestClient();
        List<TickerPrice> allPrices = client.getAllPrices();
        for (TickerPrice tickerPrice : allPrices) {
            if (tickerPrice.getSymbol() != null && tickerPrice.getSymbol().contains(symbol)) {
                return tickerPrice.getPrice();
            }
        }
        return "0";
    }
}


