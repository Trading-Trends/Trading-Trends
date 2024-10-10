package com.tradingtrends.coin.application.dto;

import com.tradingtrends.coin.domain.model.WatchlistCoin;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinWatchlistResponseDto {

    private UUID id;
    private Double upperLimitPrice;
    private Double lowerLimitPrice;

    public static CoinWatchlistResponseDto fromEntity(WatchlistCoin watchlistCoin) {
        return CoinWatchlistResponseDto.builder()
            .id(watchlistCoin.getId())
            .upperLimitPrice(watchlistCoin.getUpperLimitPrice())
            .lowerLimitPrice(watchlistCoin.getLowerLimitPrice())
            .build();
    }
}
