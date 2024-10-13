package com.tradingtrends.stock.domain.repository;

import com.tradingtrends.stock.domain.model.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
    Page<Stock> findAll(Pageable pageable);
}
