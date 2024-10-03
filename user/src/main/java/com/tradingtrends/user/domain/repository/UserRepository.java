package com.tradingtrends.user.domain.repository;

import com.tradingtrends.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

    Optional<User> findByUsername(String username);
}
