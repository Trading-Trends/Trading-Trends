package com.tradingtrends.batch.application.dto;

import com.tradingtrends.batch.domain.model.CorporateCodes;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CorporateCodesResponseDto {

    private String corpCode;   // 공시 대상 회사의 고유번호 (8자리)
    private String corpName;   // 정식명칭
    private String stockCode;  // 상장회사인 경우 주식의 종목코드 (6자리)
    private String modifyDate; // 최종변경일자 (YYYYMMDD)
}
