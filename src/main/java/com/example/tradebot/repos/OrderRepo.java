package com.example.tradebot.repos;


import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.example.tradebot.domain.User;
import com.example.tradebot.domain.usrOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface OrderRepo extends JpaRepository<usrOrder, Long> {
    Page<usrOrder> findByUserId(Long id, Pageable pageable);

    List<usrOrder> findByUserIdAndSymbolAndSideAndCounted(Long id, String symbol, OrderSide orderSide, Boolean counted);

    Set<usrOrder> findByUserAndSymbolAndStatus(User user, String symbol, OrderStatus orderStatus);

    Set<usrOrder> findByStatus(OrderStatus orderStatus);

    @Query("SELECT o FROM usrOrder o WHERE o.time BETWEEN :from AND :to AND o.user = :user")
    List<usrOrder> findOrderByDate(@Param("from") Date startDay,
                                   @Param("to") Date endDay,
                                   @Param("user") User user);

}
