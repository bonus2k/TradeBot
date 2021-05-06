package com.example.tradebot.repos;


import com.example.tradebot.domain.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends CrudRepository<Order, Long> {
    Iterable<Order> findByUserId(Long id);

    Optional<Order> findTopByUserIdAndSymbolAndSideOrderByTimestampDesc(Long id, String symbol, String side);
}
