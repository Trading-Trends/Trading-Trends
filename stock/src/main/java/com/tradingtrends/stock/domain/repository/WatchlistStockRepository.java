package com.tradingtrends.stock.domain.repository;

import com.tradingtrends.stock.domain.model.WatchlistStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WatchlistStockRepository extends JpaRepository<WatchlistStock, UUID> {
}
