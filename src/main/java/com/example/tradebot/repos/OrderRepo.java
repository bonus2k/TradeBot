package com.example.tradebot.repos;


import com.example.tradebot.domain.Order;
import com.example.tradebot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    Iterable<Order> findByUserId(Long id);

    Optional<Order> findTopByUserIdAndSymbolAndSideOrderByTimestampDesc(Long id, String symbol, String side);

    @Query("SELECT o FROM Order o WHERE o.timestamp BETWEEN :from AND :to AND o.user = :user")
    List<Order> findOrderByDate(@Param("from") Date startDay,
                                @Param("to") Date endDay,
                                @Param("user") User user);

}
