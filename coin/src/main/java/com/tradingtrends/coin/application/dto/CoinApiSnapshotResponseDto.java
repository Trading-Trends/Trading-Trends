package com.tradingtrends.coin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinApiSnapshotResponseDto implements Serializable {

    private String market;
    private double signedChangePrice;
    private double signedChangeRate;
    private double tradePrice;

    // JSON 필드명과 다른 경우 @JsonProperty를 사용해 매핑
    @JsonProperty("signed_change_price")
    public void setSignedChangePrice(double signedChangePrice) {
        this.signedChangePrice = signedChangePrice;
    }

    @JsonProperty("signed_change_rate")
    public void setSignedChangeRate(double signedChangeRate) {
        this.signedChangeRate = signedChangeRate;
    }

    @JsonProperty("trade_price")
    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }
}
