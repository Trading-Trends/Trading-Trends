package com.tradingtrends.stock.application.dto;

import com.tradingtrends.stock.domain.model.WatchlistStock;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StockWatchlistResponse {
    private UUID id;
    private Double upperLimitPrice;
    private Double lowerLimitPrice;

    public static StockWatchlistResponse fromEntity(WatchlistStock entity) {
        return StockWatchlistResponse.builder()
                .id(entity.getId())
                .upperLimitPrice(entity.getUpperLimitPrice())
                .lowerLimitPrice(entity.getLowerLimitPrice())
                .build();
    }
}