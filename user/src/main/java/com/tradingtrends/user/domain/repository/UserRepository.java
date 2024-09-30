package com.tradingtrends.user.domain.repository;

import com.tradingtrends.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    Boolean existsByUserId(String userId);
}
