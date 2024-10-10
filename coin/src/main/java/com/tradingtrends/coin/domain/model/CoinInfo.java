package com.tradingtrends.coin.domain.model;

import com.tradingtrends.coin.application.dto.CoinApiResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "P_COIN", schema = "s_coin")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class CoinInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coin_id")
    private UUID id;

    @Column(name = "market", nullable = false)
    private String market;

    @Column(name = "korean_name", nullable = false)
    private String koreanName;

    @Column(name = "english_name", nullable = false)
    private String englishName;

    /**
     * 아래 1개 필드는 유의종목 지정 여부
     */
    @Column(name = "market_warning")
    private boolean marketWarning;

    /**
     * 아래 5개 필드는 주의종목 지정 여부
     */
    @Column(name = "price_fluctuations_warning")
    private boolean priceFluctuationsWarning;

    @Column(name = "trading_volume_soaring_warning")
    private boolean tradingVolumeSoaringWarning;

    @Column(name = "deposit_amount_soaring_warning")
    private boolean depositAmountSoaringWarning;

    @Column(name = "global_price_differences_warning")
    private boolean globalPriceDifferencesWarning;

    @Column(name = "concentration_of_small_accounts_warning")
    private boolean concentrationOfSmallAccountsWarning;

    public static CoinInfo createCoin(CoinApiResponseDto dto){
        return CoinInfo.builder()
            .market(dto.getMarket())
            .koreanName(dto.getKoreanName())
            .englishName(dto.getEnglishName())
            .marketWarning(dto.isMarketWarning())
            .priceFluctuationsWarning(dto.isPriceFluctuationsWarning())
            .tradingVolumeSoaringWarning(dto.isTradingVolumeSoaringWarning())
            .depositAmountSoaringWarning(dto.isDepositAmountSoaringWarning())
            .globalPriceDifferencesWarning(dto.isGlobalPriceDifferencesWarning())
            .concentrationOfSmallAccountsWarning(dto.isConcentrationOfSmallAccountsWarning())
            .build();
    }
}
