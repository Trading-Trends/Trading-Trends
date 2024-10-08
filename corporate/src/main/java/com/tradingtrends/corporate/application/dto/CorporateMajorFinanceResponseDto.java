package com.tradingtrends.corporate.application.dto;

import com.tradingtrends.corporate.domain.model.entity.CorporateMajorFinance;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CorporateMajorFinanceResponseDto {
    private UUID id;
    private String bsnsYear;      // 사업 연도
    private String corpCode;      // 고유번호
    private String stockCode;     // 종목코드
    private String reprtCode;     // 보고서 코드
    private String idxClCode;     // 지표분류코드
    private String idxClNm;       // 지표분류명
    private String idxCode;       // 지표코드
    private String idxNm;         // 지표명
    private String idxVal;        // 지표값

    public CorporateMajorFinanceResponseDto(CorporateMajorFinance corporateMajorFinance) {
        this.id = corporateMajorFinance.getId();
        this.bsnsYear = corporateMajorFinance.getBsnsYear();      // 사업 연도
        this.corpCode = corporateMajorFinance.getCorpCode();      // 고유번호
        this.stockCode = corporateMajorFinance.getStockCode();     // 종목 코드
        this.reprtCode = corporateMajorFinance.getReprtCode();     // 보고서 코드
        this.idxClCode = corporateMajorFinance.getIdxClCode();     // 지표분류코드
        this.idxClNm = corporateMajorFinance.getIdxClNm();       // 지표분류명
        this.idxCode = corporateMajorFinance.getIdxCode();       // 지표코드
        this.idxNm = corporateMajorFinance.getIdxNm();         // 지표명
        this.idxVal = corporateMajorFinance.getIdxVal();
    }
}
