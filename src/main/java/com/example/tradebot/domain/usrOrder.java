package com.example.tradebot.domain;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "usr_order")
@Getter @Setter @NoArgsConstructor @ToString

public class usrOrder{


    @Id
    private Long orderId;
    private String symbol;
    private String clientOrderId;
    private String price;
    private String origQty;
    private String executedQty;
    private OrderStatus status;
    private TimeInForce timeInForce;
    private OrderType type;
    private OrderSide side;
    private String stopPrice;
    private String icebergQty;
    private Date time;

    private Double sum;
    private Double profit;
//    private Date timestamp;
    private boolean counted;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}