package com.example.tradebot;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.exception.BinanceApiException;
import com.example.tradebot.domain.Symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test {

    static List<String> list = new ArrayList<>();
    private List<TickerPrice> allPrices;
    private Long lastQuery = 0L;

    public static void main(String[] args) throws InterruptedException {


        Integer[] integers = new Integer[]{0,0,0,0,0,0,0,};

        Arrays.stream(integers).forEach(o-> div(0));

        System.out.println(integers.length);


//        MathContext mc = new MathContext(6, RoundingMode.DOWN);
//        BigDecimal bigDecimal1 = new BigDecimal("0.454545456535");
//        BigDecimal bigDecimal2 = bigDecimal1.divide(new BigDecimal("0.64446675867"), mc);
//        System.out.println(new BigDecimal("555487.8676776565576565765765", mc));
//        BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();
//        BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance("oKB5YsxX6N0V8PNohyBz9AguHZBB9FhM9AdZZwx1KurI8MTYxVUtowOtONroIUBX", "9ew65zomTqlZzNYjOm2HTApyEOUevLUglvgq9ASfsfVuEe4bB5n70RDq6U9s2Enw");
//        BinanceApiRestClient client = clientFactory.newRestClient();
//        Account account = client.getAccount(50000L, new Date().getTime());
//        Test test = new Test();
////        test.test(client, "VETUSDT", 6);
//        Arrays.stream(Symbol.values()).forEach(o -> test.test(client, o.name(), o.getScale()));
//        for (int i = 0; i < 8; i++) {
//
//            System.out.println(i);
//            BigDecimal bigDecimal = new BigDecimal("44.6");
//            bigDecimal = bigDecimal.setScale(i, RoundingMode.DOWN);
//            System.out.println(bigDecimal);
//        }
//        NewOrder newOrder = NewOrder.marketBuy("TRXUSDT", "7483.3");
//        newOrder.recvWindow(50000L);
//
////
//        try {
//            client.newOrderTest(newOrder);
//        }catch (BinanceApiException e){
//            System.out.println(e.getError().getMsg());
//        }
//        list.forEach(System.out::println);

//        List<TickerPrice> tickerPrices = client.getAllPrices();
//        tickerPrices.forEach(System.out::println);
//        tickerPrices.forEach(o-> client.getExchangeInfo());

//        System.out.println(exchangeInfoList.getRateLimits());

//        System.out.println(client.getAccount(50000L, new Date().getTime()).isCanTrade());
//
//
////        MathContext context = new MathContext(6, RoundingMode.DOWN);
////        BigDecimal result = new BigDecimal(0.00176980, context);
////        result = result.setScale(6, RoundingMode.DOWN);
////        System.out.println(result);
//
////        Date date = new Date(client.getServerTime());
////        System.out.println(date);
////
//
//        Account account = client.getAccount(50000L, new Date().getTime());
//        List<AssetBalance> balances = account.getBalances();
//        for (AssetBalance bak : balances
//        ) {
//            if (!bak.getFree().equals("0.00000000")) {
//                System.out.println(bak.toString());
//            }
//        }
//
//
////       List<Trade> trades = client.getMyTrades("BTCUSDT", (Integer)null, (Long)null, 50000L, System.currentTimeMillis());
////        for (Trade trade:trades
////             ) {
////            System.out.format("%s; %s; %s; %s; %s; %s; %s; \n",trade.getId(), trade.getSymbol(), trade.getPrice(), trade.getQty(), trade.getCommission(), new Date(trade.getTime()), trade.isBuyer());
////        }
////        List<BookTicker> bookTickers = client.getBookTickers();
////        for (BookTicker bt:bookTickers) {
////            System.out.println(bookTickers.toString());
////        }
////
////        OrderBook orderBook = client.getOrderBook("BTCUSDT", 10);
////        List<OrderBookEntry> asks = orderBook.getAsks();
////        for (OrderBookEntry order:asks) {
////            System.out.println(order.toString());
////        }
////        OrderBookEntry firstAskEntry = asks.get(0);
////        System.out.println(firstAskEntry.getPrice() + " / " + firstAskEntry.getQty());
////        System.out.println(asks.size());
////
////        List<AggTrade> aggTrades = client.getAggTrades("BTCUSDT");
////        for (AggTrade agg:aggTrades
////             ) {
////            System.out.print(new Date(agg.getTradeTime()));
////            System.out.println(agg);
////        }
////        System.out.println("---------------------");
//
//
////        List<Candlestick> candlesticks = client.getCandlestickBars("BTCUSDT", CandlestickInterval.MONTHLY);
////        for (Candlestick cand:candlesticks
////             ) {
////            System.out.print(new Date( cand.getOpenTime()));
////            System.out.println(cand);
////        }
//
////        System.out.println(candlesticks);
//
//
////            Date date = new Date();
////
//        List<TickerPrice> allPrices = client.getAllPrices();
//        for (TickerPrice tickerPrice : allPrices) {
//            if (tickerPrice.getSymbol().contains("USDT")) {
//                System.out.println(tickerPrice.toString());
//            }
//        }
//
//
////        System.out.println(allPrices);
//
////        Long timeBinance = client.getServerTime();
////        Long timePC = System.currentTimeMillis();
////
////        System.out.println(timeBinance-timePC);
////        NewOrder newOrder = NewOrder.marketSell("BTCUSDT", "0.000179");
////        newOrder.recvWindow(50000L);
//
////        main main = new main();
////        main.saveOrder(newOrder);
//////        client.newOrder(newOrder);
////        System.out.println(newOrder.toString());
//
//
    }

    private static void div(int i) {

        try{
            i=10/0;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public void test(BinanceApiRestClient client, String symbol, int scale) {
//        if (scale > -1) {
//            BigDecimal quantity = new BigDecimal("10.897458568").divide(getPrice(symbol), 6, RoundingMode.DOWN);
//            quantity = quantity.setScale(scale, RoundingMode.DOWN);
//            System.out.println("Округляем до: "+ quantity+ " до " + scale + " знаков");
//
//            NewOrder newOrder = NewOrder.marketBuy(symbol, quantity.toString());
//            newOrder.recvWindow(50000L);
//
//            try {
//                client.newOrderTest(newOrder);
//                System.out.println("Успешно " + symbol + " " + quantity);
//                list.add(symbol + " " + quantity);
//            } catch (BinanceApiException e) {
//
//                System.out.println(symbol + " " + quantity);
//                System.out.println(e.getError());
//                System.out.println("---------------------\n");
//            }
//
//        }
//    }
//    private BigDecimal getPrice(String symbol) {
//        BinanceApiClientFactory clientFactory = BinanceApiClientFactory.newInstance();
//        BinanceApiRestClient client = clientFactory.newRestClient();
//        String price = "0.0";
//        if (System.currentTimeMillis() - lastQuery > 10000) {
//            allPrices = client.getAllPrices();
//            lastQuery = System.currentTimeMillis();
//        }
//        for (TickerPrice tickerPrice : allPrices) {
//            if (tickerPrice.getSymbol() != null && tickerPrice.getSymbol().contains(symbol)) {
//                price = tickerPrice.getPrice();
//            }
//        }
//
//        return new BigDecimal(price);
//    }

}
