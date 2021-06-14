package com.example.tradebot.service;

import com.example.tradebot.domain.Billing;
import com.example.tradebot.domain.User;
import com.example.tradebot.domain.usrOrder;
import com.example.tradebot.repos.BillingRepo;
import com.example.tradebot.repos.OrderRepo;
import com.example.tradebot.repos.UserRepo;
import com.example.tradebot.util.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class BillingService {

    private final UserRepo userRepo;

    private final OrderRepo orderRepo;

    private final BillingRepo billingRepo;

    public BillingService(UserRepo userRepo, OrderRepo orderRepo, BillingRepo billingRepo) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.billingRepo = billingRepo;
    }

    @Scheduled(cron = "0 0 0 * * 7")
    public void setBill() {
        List<User> users = userRepo.findAll();
        for (User user : users) {
            Billing billing = getBilling(user);
            if (billing.getBalance() < 0) {
                user.setRun(false);
            }
            log.info(user);
        }
    }

    private Billing getBilling(User user) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM YY");

        Billing billing = billingRepo.findTopByUserOrderByDateDesc(user);
        List<usrOrder> orders = orderRepo.findOrderByDate(Util.getWeek(new Date()), new Date(), user);
        Double profit = orders.stream().mapToDouble(usrOrder::getProfit).sum();
        Double commission = 0.0;
        if (profit > 0) {
            commission = (profit * billing.getRate() / 100);
            Double balance = billing.getBalance() - commission;
            billing.setBalance(balance);
        }
        billing.setComment(String.format("Период расчета с %s по %s \n" +
                        "Комиссия: %s \n" +
                        "Средств на бирже: %sUSDT",
                formatter.format(Util.getWeek(new Date())), formatter.format(new Date()), commission, user.getAmount()));
        billing.setProfitOnWeek(profit);
        return billingRepo.save(new Billing(billing));
    }



    public List<Billing> getBillingUser(User user) {
        return billingRepo.findByUser(user);
    }

    public void saveBilling(User user, Billing billing) {
        Billing loadBilling = billingRepo.findTopByUserOrderByDateDesc(user);
        loadBilling.setBalance(billing.getBalance());
        loadBilling.setRate(billing.getRate());
        loadBilling.setComment(billing.getComment());
        billingRepo.save(new Billing(loadBilling));
    }

    public Billing findTopByUserOrderByDateDesc(User user){
        return billingRepo.findTopByUserOrderByDateDesc(user);
    }
}
