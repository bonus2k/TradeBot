package com.example.tradebot.repos;

import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    User findByActivationCode(String code);

    List<User> findBySymbolAndIsRunAndIsCanTrade(Symbol symbol, boolean isRun, boolean isCanTrade);

    Optional<User> findByEmail(String email);

    List<User> findBySymbolAndIsCanTrade(Symbol valueOf, boolean isCanTrade);
}
