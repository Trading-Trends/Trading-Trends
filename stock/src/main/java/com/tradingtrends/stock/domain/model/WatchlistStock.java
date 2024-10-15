package com.tradingtrends.stock.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradingtrends.stock.application.dto.StockWatchlistRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "P_WATCHLIST_STOCK", schema = "s_stock")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WatchlistStock {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "upper_limit_price")
    private Double upperLimitPrice;

    @Column(name = "lower_limit_price")
    private Double lowerLimitPrice;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "pdno")
    private Stock stock;

    public void updateLimitPrice(StockWatchlistRequest request) {
        if (request.getUpperLimitPrice() != null) {
            this.upperLimitPrice = request.getUpperLimitPrice();
        }
        if (request.getLowerLimitPrice() != null) {
            this.lowerLimitPrice = request.getLowerLimitPrice();
        }
    }
}