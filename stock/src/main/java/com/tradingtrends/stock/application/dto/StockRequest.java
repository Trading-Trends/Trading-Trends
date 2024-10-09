package com.tradingtrends.stock.application.dto;

import lombok.Getter;

@Getter
public class StockRequest {
    private String pdno; // 종목 코드들
    private String prdtTypeCd; // 상품유형코드
}
