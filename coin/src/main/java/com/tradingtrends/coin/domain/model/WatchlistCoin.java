package com.tradingtrends.coin.domain.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradingtrends.coin.domain.repository.WatchlistCoinRepository;
import com.tradingtrends.coin.presentation.request.CoinWatchlistRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "P_WATCHLIST_COIN", schema = "s_coin")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class WatchlistCoin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "watchlist_coin_id")
    private UUID id;

    @Column(name = "upper_limit_price")
    private Double upperLimitPrice;

    @Column(name = "lower_limit_price")
    private Double lowerLimitPrice;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "coin_id")
    private CoinInfo coinInfo;

    public static WatchlistCoin addWatchlistCoin(CoinWatchlistRequest request, CoinInfo coinInfo){
        return WatchlistCoin.builder()
            .upperLimitPrice(request.getUpperLimitPrice())
            .lowerLimitPrice(request.getLowerLimitPrice())
            .coinInfo(coinInfo)
            .build();
    }

    public void updateLimitPrice(CoinWatchlistRequest request) {
        if (request.getUpperLimitPrice() != null) {
            this.upperLimitPrice = request.getUpperLimitPrice();
        }
        if (request.getLowerLimitPrice() != null) {
            this.lowerLimitPrice = request.getLowerLimitPrice();
        }
    }
}
