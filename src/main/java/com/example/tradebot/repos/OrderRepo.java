package com.example.tradebot.repos;


import com.example.tradebot.domain.Order;
import com.example.tradebot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    Iterable<Order> findByUserIdOrderByTimestamp(Long id);

    List<Order> findByUserIdAndSymbolAndSideAndCounted(Long id, String symbol, String side, Boolean counted);

    @Query("SELECT o FROM Order o WHERE o.timestamp BETWEEN :from AND :to AND o.user = :user")
    List<Order> findOrderByDate(@Param("from") Date startDay,
                                @Param("to") Date endDay,
                                @Param("user") User user);

}
