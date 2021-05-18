package com.example.tradebot.repos;

import com.example.tradebot.domain.Role;
import com.example.tradebot.domain.Symbol;
import com.example.tradebot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    User findByActivationCode(String code);

    Set<User> findBySymbolAndIsRunAndIsCanTrade(Symbol symbol, boolean isRun, boolean isCanTrade);

    @Transactional
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Set<User> findBySymbolAndIsCanTrade(Symbol valueOf, boolean isCanTrade);

    @Transactional
    User save(User user);

    Set<User> findByRoles(Role role);

}
