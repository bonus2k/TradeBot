package com.example.tradebot.repos;

import com.example.tradebot.domain.Alerts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AlertsRepo extends CrudRepository<Alerts, Integer> {

    Optional<Alerts> findTopBySymbolOrderByDateDesc(String symbol);

//    @Query("select a from Alerts a where a.alert = ?1 and a.symbol = ?2 and a.date between ?3 and ?4")
    Page<Alerts> findAll(Pageable pageable);

    @Query("select a from Alerts a where a.alert like :alert and a.symbol like :symbol and a.date between :start and :stop")
    Page<Alerts> findAlertsFilter(@Param("alert") String alert,
                                  @Param("symbol") String symbol,
                                  @Param("start") Date start,
                                  @Param("stop") Date stop,
                                  Pageable pageable);

}
