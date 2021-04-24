package com.example.tradebot.repos;

import com.example.tradebot.domain.Alerts;
import org.springframework.data.repository.CrudRepository;

public interface AlertsRepo extends CrudRepository<Alerts, Integer> {
}
