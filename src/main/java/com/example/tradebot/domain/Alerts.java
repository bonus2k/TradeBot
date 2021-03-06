package com.example.tradebot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Alerts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String alert;
    private String symbol;
    private Double price;
    private Double rate;
    private String botName;
    private Date date;


    public Alerts(String alert, String symbol, Double price, Double rate, String botName, Date date) {
        this.alert = alert;
        this.symbol = symbol;
        this.price = price;
        this.botName = botName;
        this.rate = rate;
        this.date = date;
    }

}
