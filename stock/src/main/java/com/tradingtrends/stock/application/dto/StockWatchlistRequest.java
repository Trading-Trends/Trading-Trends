package com.tradingtrends.stock.application.dto;

import lombok.Data;

@Data
public class StockWatchlistRequest {
    private Double upperLimitPrice;
    private Double lowerLimitPrice;
}