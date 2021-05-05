package com.example.tradebot.repos;

import com.example.tradebot.domain.Alerts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertsRepo extends CrudRepository<Alerts, Integer> {
}
