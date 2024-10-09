package com.tradingtrends.coin.domain.repository;

import com.tradingtrends.coin.domain.model.WatchlistCoin;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistCoinRepository extends JpaRepository<WatchlistCoin, UUID> {

}
