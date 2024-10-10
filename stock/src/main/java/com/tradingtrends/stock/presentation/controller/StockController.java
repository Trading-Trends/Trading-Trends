package com.tradingtrends.stock.presentation.controller;

import com.tradingtrends.stock.application.dto.StockRequest;
import com.tradingtrends.stock.application.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;

    @PostMapping("/info")
    public ResponseEntity<Void> saveStockInfo(@RequestBody StockRequest stockRequest) throws InterruptedException {
        stockService.saveStockInfo(stockRequest);
        return ResponseEntity.ok().build();
    }
}