package com.tradingtrends.coin.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoinApiSnapshotResponseDto implements Serializable {

    @JsonProperty("market")
    private String market;

    @JsonProperty("signed_change_price")
    private double signedChangePrice;

    @JsonProperty("signed_change_rate")
    private double signedChangeRate;

    @JsonProperty("trade_price")
    private double tradePrice;

}
