package com.example.tradebot.service;

import com.example.tradebot.domain.Billing;
import com.example.tradebot.domain.Order;
import com.example.tradebot.domain.User;
import com.example.tradebot.repos.BillingRepo;
import com.example.tradebot.repos.OrderRepo;
import com.example.tradebot.repos.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Scheduled(cron = "0 0 0 * * 6")
    public void setBill() {
        List<User> users = userRepo.findAll();

        for (User user : users) {
            Billing billing = getBilling(user);
            user.setBilling(billing);
            if (billing.getBalance() < 0) {
                user.setRun(false);
            }
            userRepo.save(user);
            log.info(user);
        }
    }

    private Billing getBilling(User user) {

        Billing billing = user.getBilling();
        List<Order> orders = orderRepo.findOrderByDate(getWeek(new Date()), new Date(), user);
        Double profit = orders.stream().mapToDouble(Order::getProfit).sum();
        if (profit > 0) {
            Double balance = billing.getBalance() - (profit * billing.getRate() / 100);
            billing.setBalance(balance);
        }
        billing.setProfitOnWeek(profit);
        Billing newBilling = new Billing(billing);
        billingRepo.save(newBilling);
        return newBilling;
    }

    private Date getWeek(Date date) {
        Date dateWeek = new Date(date.getTime() - 604_800_000);
        return dateWeek;
    }

    public List<Billing> getBillingUser(User user) {
        return billingRepo.findByUser(user);
    }

    public void saveBilling(User user, Billing billing) {
        Billing loadBilling = user.getBilling();
        loadBilling.setBalance(billing.getBalance());
        loadBilling.setRate(billing.getRate());
        loadBilling.setComment(billing.getComment());
        user.setBilling(loadBilling);
        billingRepo.save(new Billing(loadBilling));
        userRepo.save(user);
    }
}
