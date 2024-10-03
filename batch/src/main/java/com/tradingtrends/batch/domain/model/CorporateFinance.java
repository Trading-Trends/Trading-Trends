package com.tradingtrends.batch.domain.model;

import com.tradingtrends.batch.application.dto.DartCorporateFinanceApiResponse.CorporateFinanceDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "P_CORPORATE_FINANCE", schema = "s_corporate")
public class CorporateFinance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "corp_id")
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

    public CorporateFinance(CorporateFinanceDto dto) {
        this.bsnsYear = dto.getBsns_year();
        this.corpCode = dto.getCorp_code();
        this.stockCode = dto.getStock_code();
        this.reprtCode = dto.getReprt_code();
        this.idxClCode = dto.getIdx_cl_code();
        this.idxClNm = dto.getIdx_cl_nm();
        this.idxCode = dto.getIdx_code();
        this.idxNm = dto.getIdx_nm();
        this.idxVal = dto.getIdx_val() != null ? dto.getIdx_val() : "0";
    }
}
