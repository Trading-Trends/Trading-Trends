package com.tradingtrends.stock.domain.repository;

import com.tradingtrends.stock.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
