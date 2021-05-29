package com.example.tradebot.repos;

import com.example.tradebot.domain.Billing;
import com.example.tradebot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BillingRepo extends JpaRepository<Billing, Long> {

        List<Billing> findByUser(User user);

        Billing findTopByUserOrderByDateDesc(User user);
}
