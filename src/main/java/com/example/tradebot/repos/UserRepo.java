package com.example.tradebot.repos;

import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByActivationCode(String code);

    List<User> findBySymbolAndIsRunAndIsCanTrade(Symbol symbol, boolean isRun, boolean isCanTrade);

    User findByEmail(String email);

    List<User> findBySymbolAndIsCanTrade(Symbol valueOf, boolean isCanTrade);
}
