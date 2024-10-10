package com.tradingtrends.coin.presentation.request;

import lombok.Data;

@Data
public class CoinWatchlistRequest {
    private Double upperLimitPrice;
    private Double lowerLimitPrice;
}
