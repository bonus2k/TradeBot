package com.example.tradebot.repos;

import com.example.tradebot.domain.Alerts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertsRepo extends CrudRepository<Alerts, Integer> {

    Optional<Alerts> findTopBySymbolOrderByDateDesc(String symbol);

    Page<Alerts> findAll(Pageable pageable);
}
