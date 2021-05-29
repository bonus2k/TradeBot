package com.example.tradebot.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Getter
@Setter

@Table(name = "billing")
public class Billing implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id", referencedColumnName="id")
    private User user;
    private Double balance;
    private Date date;
    private Double profitOnWeek;
    private String comment;
    private Integer rate;

    public Billing(){

    }

    public Billing(Billing o) {
        this.user = o.user;
        this.balance = o.balance;
        this.date = new Date();
        this.profitOnWeek = o.profitOnWeek;
        this.comment = o.comment;
        this.rate = o.rate;
    }

    public Billing(User user, Double balance, Date date, Integer rate) {
        this.user = user;
        this.balance = balance;
        this.date = date;
        this.rate = rate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
