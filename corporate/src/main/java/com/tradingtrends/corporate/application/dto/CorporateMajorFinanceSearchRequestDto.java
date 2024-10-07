package com.tradingtrends.corporate.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CorporateMajorFinanceSearchRequestDto {
    private String corpCode;
    private String stockCode;
    private String fromBsnsYear;
    private String toBsnsYear;
}
