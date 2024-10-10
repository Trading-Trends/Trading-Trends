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
    private String koreanName;
    private String englishName;
    private boolean marketWarning;
    private boolean priceFluctuationsWarning;
    private boolean tradingVolumeSoaringWarning;
    private boolean depositAmountSoaringWarning;
    private boolean globalPriceDifferencesWarning;
    private boolean concentrationOfSmallAccountsWarning;

    public static CoinApiResponseDto fromEntity(CoinInfo coinInfo) {
        return CoinApiResponseDto.builder()
            .market(coinInfo.getMarket())
            .koreanName(coinInfo.getKoreanName())
            .englishName(coinInfo.getEnglishName())
            .marketWarning(coinInfo.isMarketWarning())
            .priceFluctuationsWarning(coinInfo.isPriceFluctuationsWarning())
            .tradingVolumeSoaringWarning(coinInfo.isTradingVolumeSoaringWarning())
            .depositAmountSoaringWarning(coinInfo.isDepositAmountSoaringWarning())
            .globalPriceDifferencesWarning(coinInfo.isGlobalPriceDifferencesWarning())
            .concentrationOfSmallAccountsWarning(builder().concentrationOfSmallAccountsWarning)
            .build();
    }
}
