package com.tradingtrends.coin.application.dto;

import com.tradingtrends.coin.domain.model.CoinInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoinApiResponseDto {

    private String market;
    private String korean_name;
    private String english_name;
    private String market_warning;
    private MarketEvent market_event;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketEvent {
        private Caution caution;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class Caution {
            private boolean PRICE_FLUCTUATIONS;
            private boolean TRADING_VOLUME_SOARING;
            private boolean DEPOSIT_AMOUNT_SOARING;
            private boolean GLOBAL_PRICE_DIFFERENCES;
            private boolean CONCENTRATION_OF_SMALL_ACCOUNTS;
        }
    }

    public static CoinApiResponseDto fromEntity(CoinInfo coinInfo) {
        return CoinApiResponseDto.builder()
            .market(coinInfo.getMarket())
            .korean_name(coinInfo.getKoreanName())
            .english_name(coinInfo.getEnglishName())
            .market_warning(coinInfo.getMarketWarning())
            .market_event(
                CoinApiResponseDto.MarketEvent.builder()
                    .caution(
                        CoinApiResponseDto.MarketEvent.Caution.builder()
                            .PRICE_FLUCTUATIONS(coinInfo.isPriceFluctuationsWarning())
                            .TRADING_VOLUME_SOARING(coinInfo.isTradingVolumeSoaringWarning())
                            .DEPOSIT_AMOUNT_SOARING(coinInfo.isDepositAmountSoaringWarning())
                            .GLOBAL_PRICE_DIFFERENCES(coinInfo.isGlobalPriceDifferencesWarning())
                            .CONCENTRATION_OF_SMALL_ACCOUNTS(coinInfo.isConcentrationOfSmallAccountsWarning())
                            .build()
                    )
                    .build()
            )
            .build();
    }
}
