package com.tradingtrends.stock.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalRequest {
    private String tr_id;  // 거래 ID
    private String tr_key; // 종목번호
}
