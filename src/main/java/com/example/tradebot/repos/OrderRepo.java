package com.example.tradebot.repos;


import com.example.tradebot.domain.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepo extends CrudRepository<Order, Long> {
    Iterable<Order> findByUserId(Long id);

//    Order findByUserIdAndSymbolAndSideOrderByTimestampDesc(Long id, String symbol, String side);
}
