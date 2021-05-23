package com.example.tradebot;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.*;

import java.util.Date;
import java.util.List;

public class Test {
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

        Account account = client.getAccount(50000L, new Date().getTime());
        NewOrder limitOrder = NewOrder.limitSell("ETHUSDT", TimeInForce.GTC, "0.006", "3450");
        limitOrder.recvWindow(50000L);
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
        openOrders.forEach(o-> System.out.println(o+":"+new Date(o.getTime())));

//        orderAll.forEach(System.out::println);
        System.out.println("----------------");

        getStatus(listOrder);
        Double d = -0.0001;
        System.out.println(String.format("\uD83D\uDD14Покупка %s, пара: %s, %s цена: %.3f%%, сигнал от: %s",
                new Date(), "USDT", (d>0)?"\uD83D\uDCC8":"\uD83D\uDCC9", 0.0005468, "Bot"));

    }

    public static void getStatus(String[] listOrderId) {
        for (String orderId : listOrderId) {
            OrderStatusRequest orderStatusRequest = new OrderStatusRequest("ETHUSDT", orderId);
            orderStatusRequest.recvWindow(50000L);
            Order orderStatus = client.getOrderStatus(orderStatusRequest);
            System.out.println(orderStatus.toString() +" : " + new Date(orderStatus.getTime()));
        }

    }
}
