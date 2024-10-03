package com.tradingtrends.batch.application.dto;

import java.util.List;
import lombok.Data;

@Data
public class DartCorporateFinanceApiResponse {

    private String status;    // 응답 상태 코드
    private String message;   // 응답 메시지
    private List<CorporateFinanceDto> list; // 실제 데이터 리스트

    @Data
    public static class CorporateFinanceDto {
        private String bsns_year;      // 사업 연도
        private String corp_code;      // 고유번호
        private String stock_code;     // 종목 코드
        private String reprt_code;     // 보고서 코드
        private String idx_cl_code;     // 지표분류코드
        private String idx_cl_nm;       // 지표분류명
        private String idx_code;       // 지표코드
        private String idx_nm;         // 지표명
        private String idx_val;        // 지표값
    }
}
