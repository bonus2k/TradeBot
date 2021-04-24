package com.example.tradebot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "usr_order")
@Getter @Setter @NoArgsConstructor
public class Order {
    private String symbol;
    private String side;
    private String type;
    private Double quantity;
    private Double price;
    private Double sum;
    private Date timestamp;
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}